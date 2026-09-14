package com.andrewaleynik.reportdesigner.reportdesigner.util;

import java.math.BigDecimal;

public final class FormValidators {

    private FormValidators() {
    }

    public static boolean isBlankOrTooShort(String value, int minLength) {
        return value == null || value.trim().length() < minLength;
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isOptionalNonNegativeLong(String value) {
        if (isBlank(value)) {
            return true;
        }
        try {
            return Long.parseLong(value.trim()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isOptionalNonNegativeBigDecimal(String value) {
        if (isBlank(value)) {
            return true;
        }
        try {
            return new BigDecimal(value.trim()).compareTo(BigDecimal.ZERO) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
