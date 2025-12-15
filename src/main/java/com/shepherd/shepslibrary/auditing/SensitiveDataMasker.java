package com.shepherd.shepslibrary.auditing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class SensitiveDataMasker {
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<Pattern> sensitiveKeyPatterns = List.of(
            Pattern.compile("token", Pattern.CASE_INSENSITIVE),
            Pattern.compile("refresh?token", Pattern.CASE_INSENSITIVE),
            Pattern.compile("access?token", Pattern.CASE_INSENSITIVE),
            Pattern.compile("otp", Pattern.CASE_INSENSITIVE),
            Pattern.compile("verification?code", Pattern.CASE_INSENSITIVE),
            Pattern.compile("card?number", Pattern.CASE_INSENSITIVE),
            Pattern.compile("ssn", Pattern.CASE_INSENSITIVE)
    );

    private final Pattern numberPattern = Pattern.compile("\\d{12,19}"); // basic CC detection

    public String mask(String payload) {
        if (payload == null || payload.isBlank()) return null;
        String trimmed = payload.trim();
        try {
            JsonNode root = mapper.readTree(payload);
            maskNode(root);
            return mapper.writeValueAsString(root);
        } catch (Exception ex) {
            try {
                if (trimmed.contains("=") && trimmed.contains("&")) return maskFormEncoded(trimmed);
            } catch (Exception ignored) {}
            String out = numberPattern.matcher(trimmed).replaceAll("****REDACTED_NUMBER****");
            out = maskTokenLike(out);
            return out.length() > 4000 ? out.substring(0, 4000) + "... (truncated)" : out;
        }
    }

    private void maskNode(JsonNode node) {
        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;
            Iterator<String> names = obj.fieldNames();
            List<String> copy = new ArrayList<>();
            names.forEachRemaining(copy::add);
            for (String name : copy) {
                JsonNode child = obj.get(name);
                if (isSensitiveKey(name)) {
                    obj.put(name, "*****");
                } else if (child.isTextual()) {
                    String txt = child.asText();
                    txt = numberPattern.matcher(txt).replaceAll("****REDACTED_NUMBER****");
                    txt = maskTokenLike(txt);
                    obj.put(name, txt);
                } else {
                    maskNode(child);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode n : node) maskNode(n);
        }
    }

    private String maskFormEncoded(String payload) {
        return Arrays.stream(payload.split("&"))
                .map(kv -> {
                    int idx = kv.indexOf('=');
                    if (idx == -1) return kv;
                    String key = kv.substring(0, idx);
                    String val = kv.substring(idx + 1);
                    if (isSensitiveKey(key)) return key + "=*****";
                    return key + "=" + val;
                })
                .collect(Collectors.joining("&"));
    }

    private String maskTokenLike(String text) {
        text = text.replaceAll("(?i)(token=)([^&\\s]+)", "$1*****");
        text = text.replaceAll("\\b[A-Za-z0-9\\-_]{32,}\\b", "****REDACTED_TOKEN****");
        return text;
    }

    private boolean isSensitiveKey(String key) {
        if(key == null) return false;
        return sensitiveKeyPatterns.stream().anyMatch(p -> p.matcher(key).find());
    }
}