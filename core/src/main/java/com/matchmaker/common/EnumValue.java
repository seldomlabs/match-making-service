package com.matchmaker.common;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnumValueValidator.class)  // Link the validator
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumValue {
    String message() default "Invalid value. Must be one of the enum values.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // Specify which enum class this annotation should be used with
    Class<? extends Enum<?>> enumClass();
}