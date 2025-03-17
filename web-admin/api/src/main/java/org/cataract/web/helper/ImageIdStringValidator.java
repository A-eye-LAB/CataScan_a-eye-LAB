package org.cataract.web.helper;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageIdStringValidator implements ConstraintValidator<ValidImageIdString, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return value.matches("^[A-Za-z0-9]+=[mfo]$");
    }
}