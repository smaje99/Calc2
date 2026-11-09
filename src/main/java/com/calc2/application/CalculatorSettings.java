package com.calc2.application;

import com.calc2.domain.AngleUnit;

/** Immutable user preferences for angle interpretation, display precision, and colors. */
public final class CalculatorSettings {
    private final AngleUnit angleUnit;
    private final int maximumFractionDigits;
    private final CalculatorTheme theme;
    /** Creates validated settings; precision must be one of 2, 4, 6, or 10 digits.
     * @param angleUnit trigonometric angle unit
     * @param maximumFractionDigits display precision
     * @param theme selected color theme
     */
    public CalculatorSettings(AngleUnit angleUnit, int maximumFractionDigits, CalculatorTheme theme) {
        if (angleUnit == null || theme == null) throw new IllegalArgumentException("Angle unit and theme are required.");
        if (maximumFractionDigits != 2 && maximumFractionDigits != 4 && maximumFractionDigits != 6 && maximumFractionDigits != 10)
            throw new IllegalArgumentException("Precision must be 2, 4, 6, or 10 fractional digits.");
        this.angleUnit = angleUnit; this.maximumFractionDigits = maximumFractionDigits; this.theme = theme;
    }
    /** Returns the trigonometric angle unit.
     * @return angle unit
     */
    public AngleUnit getAngleUnit() { return angleUnit; }
    /** Returns the maximum number of displayed fractional digits.
     * @return display precision
     */
    public int getMaximumFractionDigits() { return maximumFractionDigits; }
    /** Returns the selected color theme.
     * @return color theme
     */
    public CalculatorTheme getTheme() { return theme; }
    /** Creates the documented default settings: degrees, 10 digits, and dark theme.
     * @return fresh immutable defaults
     */
    public static CalculatorSettings defaults() { return new CalculatorSettings(AngleUnit.DEGREES, 10, CalculatorTheme.DARK); }
}
