package com.calc2.application;

import java.time.Instant;

/** Immutable expression, numeric result, and calculation time kept in session history. */
public final class CalculationRecord {
    private final String expression;
    private final double result;
    private final Instant calculatedAt;
    /** Creates a record; equal expressions at different times remain separate entries.
     * @param expression exact source text
     * @param result unrounded numeric value
     * @param calculatedAt time of evaluation
     */
    public CalculationRecord(String expression, double result, Instant calculatedAt) {
        if (expression == null || calculatedAt == null) throw new IllegalArgumentException("Expression and time are required.");
        this.expression = expression; this.result = result; this.calculatedAt = calculatedAt;
    }
    /** Returns the exact expression that was evaluated.
     * @return source expression
     */
    public String getExpression() { return expression; }
    /** Returns the unrounded numeric result.
     * @return full precision result
     */
    public double getResult() { return result; }
    /** Returns when the expression was evaluated.
     * @return calculation time
     */
    public Instant getCalculatedAt() { return calculatedAt; }
}
