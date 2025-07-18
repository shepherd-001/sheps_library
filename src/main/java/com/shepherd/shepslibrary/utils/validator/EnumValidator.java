package com.shepherd.shepslibrary.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class EnumValidator implements ConstraintValidator<EnumValid, String> {
    private Set<String> acceptedValues;
    private boolean ignoreCase;

    @Override
    public void initialize(EnumValid constraintAnnotation) {
        ignoreCase = constraintAnnotation.ignoreCase();
        acceptedValues = Arrays.stream(constraintAnnotation.enumClass().getEnumConstants())
                .map(enum_constant -> ignoreCase ? enum_constant.name().toLowerCase(): enum_constant.name())
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) return true;
        value = value.trim();
        return ignoreCase ? acceptedValues.contains(value.toLowerCase()) : acceptedValues.contains(value);
    }
}