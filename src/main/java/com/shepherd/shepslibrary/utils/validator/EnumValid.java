package com.shepherd.shepslibrary.utils.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EnumValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValid {
    String message () default "Enum is invalid";
    Class<?>[] groups () default {};
    Class<? extends Payload>[] payload () default {};
    Class <? extends Enum<?>> enumClass();
    boolean ignoreCase() default false;
}