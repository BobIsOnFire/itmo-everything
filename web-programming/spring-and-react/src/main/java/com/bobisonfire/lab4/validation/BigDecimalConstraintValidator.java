package com.bobisonfire.lab4.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

public class BigDecimalConstraintValidator implements ConstraintValidator<BigDecimalConstraint, String> {
    private double min;
    private boolean minIncluding;

    private double max;
    private boolean maxIncluding;

    public void initialize(BigDecimalConstraint constraint) {
        this.min = constraint.min();
        this.minIncluding = constraint.minIncluding();
        this.max = constraint.max();
        this.maxIncluding = constraint.maxIncluding();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || value.trim().isEmpty()) return false;
        try {
            BigDecimal number = new BigDecimal(value.trim());

            BigDecimal min = BigDecimal.valueOf(this.min);
            BigDecimal max = BigDecimal.valueOf(this.max);

            boolean isValidMin = number.compareTo(min) > 0 || minIncluding && number.compareTo(min) == 0;
            boolean isValidMax = number.compareTo(max) < 0 || maxIncluding && number.compareTo(max) == 0;

            return isValidMin && isValidMax;
        } catch (NumberFormatException exc) {
            return false;
        }
    }
}
