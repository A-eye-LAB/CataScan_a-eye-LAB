package org.cataract.web.helper;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PastOrPresentDateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPastOrPresentDate {
    String message() default "Date must be today or in the past";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}