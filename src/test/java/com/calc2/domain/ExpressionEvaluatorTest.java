package com.calc2.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpressionEvaluatorTest {
    private final ExpressionEvaluator evaluator = new ExpressionEvaluator();

    @Test
    void evaluatesDecimalOperands() {
        assertEquals(3.75, evaluator.evaluate("1.5 + 2.25"), 0.000001);
    }

    @Test
    void evaluatesEveryBinaryOperator() {
        assertEquals(7.0, evaluator.evaluate("3 + 4"), 0.000001);
        assertEquals(-1.0, evaluator.evaluate("3 - 4"), 0.000001);
        assertEquals(12.0, evaluator.evaluate("3 * 4"), 0.000001);
        assertEquals(0.75, evaluator.evaluate("3 / 4"), 0.000001);
        assertEquals(81.0, evaluator.evaluate("3 ^ 4"), 0.000001);
    }

    @Test
    void multiplicationHasHigherPrecedenceThanAddition() {
        assertEquals(14.0, evaluator.evaluate("2 + 3 * 4"), 0.000001);
    }

    @Test
    void nestedParenthesesOverrideNormalPrecedence() {
        assertEquals(20.0, evaluator.evaluate("2 * (3 + (4 + 3))"), 0.000001);
    }

    @Test
    void exponentiationIsRightAssociative() {
        assertEquals(512.0, evaluator.evaluate("2^3^2"), 0.000001);
    }

    @Test
    void rejectsUnknownTokensAndMalformedDecimals() {
        assertThrows(CalculationException.class, () -> evaluator.evaluate("2 & 3"));
        assertThrows(CalculationException.class, () -> evaluator.evaluate("1.2.3 + 4"));
        assertThrows(CalculationException.class, () -> evaluator.evaluate("2 +"));
    }

    @Test
    void rejectsUnbalancedParentheses() {
        assertThrows(CalculationException.class, () -> evaluator.evaluate("(2 + 3"));
        assertThrows(CalculationException.class, () -> evaluator.evaluate("2 + 3)"));
    }

    @Test
    void reportsDivisionByZeroAsCalculationError() {
        assertThrows(CalculationException.class, () -> evaluator.evaluate("8 / 0"));
    }
}
