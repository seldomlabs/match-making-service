package com.matchmaker.common;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {

    private Enum<?>[] enumConstants;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        // Initialize with the enum class specified in the annotation
        enumConstants = constraintAnnotation.enumClass().getEnumConstants();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // This could be adjusted if you want to allow null values
        }

        // Check if the value matches any of the enum constants
        for (Enum<?> enumConstant : enumConstants) {
            if (enumConstant.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}