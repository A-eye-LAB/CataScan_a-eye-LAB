package org.cataract.web.helper;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ImageIdStringValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImageIdString {
    String message() default "Invalid format. Must contain '-' and end with 'm', 'f', or 'o'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}