package de.farue.autocut.validation;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MaxFutureValidator implements ConstraintValidator<MaxFuture, LocalDate> {

    protected long amount;
    protected ChronoUnit unit;

    @Override
    public void initialize(MaxFuture constraintAnnotation) {
        this.amount = constraintAnnotation.amount();
        this.unit = constraintAnnotation.unit();
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        LocalDate today = LocalDate.now();
        return unit.between(today, value) <= amount;
    }
}
