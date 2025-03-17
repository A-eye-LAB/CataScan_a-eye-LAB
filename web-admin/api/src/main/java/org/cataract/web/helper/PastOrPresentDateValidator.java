package org.cataract.web.helper;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Date;

public class PastOrPresentDateValidator implements ConstraintValidator<ValidPastOrPresentDate, Date> {

    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !value.after(new Date());
    }
}