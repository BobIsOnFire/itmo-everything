package com.bobisonfire.lab4.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BigDecimalConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BigDecimalConstraint {
    String message() default "String is not valid BigDecimal";

    double min() default Double.NEGATIVE_INFINITY;
    boolean minIncluding() default true;

    double max() default Double.POSITIVE_INFINITY;
    boolean maxIncluding() default true;

    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
