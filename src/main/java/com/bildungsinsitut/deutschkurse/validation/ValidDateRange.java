package com.bildungsinsitut.deutschkurse.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.time.LocalDate;

// Custom validation annotation
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDateRange.DateRangeValidator.class)
@Documented
public @interface ValidDateRange {
    String message() default "End date must be after start date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    // Validator implementation
    class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

        @Override
        public void initialize(ValidDateRange constraintAnnotation) {
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }

            try {
                // Use reflection to get startdatum and enddatum fields
                java.lang.reflect.Field startField = value.getClass().getDeclaredField("startdatum");
                java.lang.reflect.Field endField = value.getClass().getDeclaredField("enddatum");

                startField.setAccessible(true);
                endField.setAccessible(true);

                LocalDate startDate = (LocalDate) startField.get(value);
                LocalDate endDate = (LocalDate) endField.get(value);

                if (startDate != null && endDate != null) {
                    return endDate.isAfter(startDate);
                }

                return true; // If either date is null, let other validations handle it

            } catch (Exception e) {
                return false;
            }
        }
    }
}