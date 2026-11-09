package com.calc2.presentation;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Formats finite calculator values for display without changing their stored precision. */
public final class ResultFormatter {
    private ResultFormatter() { }
    /** Rounds with HALF_UP and formats with a decimal point, retaining at least one digit.
     * @param value finite calculation result
     * @param maximumFractionDigits maximum decimal places to show
     * @return locale-independent display text
     */
    public static String format(double value, int maximumFractionDigits) {
        if (Double.isNaN(value) || Double.isInfinite(value)) throw new IllegalArgumentException("Only finite values can be displayed.");
        BigDecimal rounded = BigDecimal.valueOf(value).setScale(maximumFractionDigits, RoundingMode.HALF_UP).stripTrailingZeros();
        String text = rounded.toPlainString();
        return text.indexOf('.') < 0 ? text + ".0" : text;
    }
}
