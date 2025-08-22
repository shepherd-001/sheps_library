package com.shepherd.shepslibrary.auditing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class SensitiveDataMasker {
    private static final List<String> SENSITIVE_KEYS = List.of("password", "token", "otp", "refreshToken", "accessToken");

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String mask(String payload){
        if(payload == null || payload.isBlank())
            return payload;
        try{
            JsonNode root = objectMapper.readTree(payload);
            maskNode(root);
            return objectMapper.writeValueAsString(root);
        }catch (Exception ex){
            return payload;
        }
    }

    private void maskNode(JsonNode node) {
        if(node.isObject()){
            ObjectNode obj = (ObjectNode) node;
            Iterator<String> fieldNames = obj.fieldNames();
            while(fieldNames.hasNext()){
                String fieldName = fieldNames.next();
                JsonNode child = obj.get(fieldName);
                if(SENSITIVE_KEYS.contains(fieldName)){
                    obj.put(fieldName, "*****");
                }else{
                    maskNode(child);
                }
            }
        }else if(node.isArray()){
            for(JsonNode child : node){
                maskNode(child);
            }
        }
    }
}
