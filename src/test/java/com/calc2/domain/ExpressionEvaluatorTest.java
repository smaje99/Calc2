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

    @Test
    void evaluatesScientificFunctionsAndConstants() {
        assertEquals(Math.PI, evaluator.evaluate("pi"), 0.000001);
        assertEquals(Math.E, evaluator.evaluate("e"), 0.000001);
        assertEquals(3.0, evaluator.evaluate("sqrt(9)"), 0.000001);
        assertEquals(3.0, evaluator.evaluate("sqrt(sqrt(81))"), 0.000001);
        assertEquals(1.0, evaluator.evaluate("sin(90)"), 0.000001);
        assertEquals(1.0, evaluator.evaluate("cos(0)"), 0.000001);
        assertEquals(1.0, evaluator.evaluate("tan(45)"), 0.000001);
        assertEquals(2.0, evaluator.evaluate("log(100)"), 0.000001);
        assertEquals(1.0, evaluator.evaluate("ln(e)"), 0.000001);
        assertEquals(1.0, evaluator.evaluate("sin(pi / 2)", AngleUnit.RADIANS), 0.000001);
        assertEquals(-4.0, evaluator.evaluate("-2^2"), 0.000001);
        assertEquals(0.25, evaluator.evaluate("2^-2"), 0.000001);
    }

    @Test
    void evaluatesDisplayedScientificNotationWithUnicodeConstants() {
        assertEquals(6.8514001115625E8 * Math.PI,
                evaluator.evaluate("6.8514001115625E8*π"), 0.0001);
        assertEquals(Math.E, evaluator.evaluate("ℯ"), 0.000001);
        assertEquals(Math.PI, evaluator.evaluate("π"), 0.000001);
    }

    @Test
    void evaluatesTheOperatorSymbolsShownOnTheKeypad() {
        assertEquals(5.0, evaluator.evaluate("2 × 3 − 1"), 0.000001);
        assertEquals(4.0, evaluator.evaluate("8 ÷ 2"), 0.000001);
    }

    @Test
    void rejectsUnknownFunctionsAndInvalidRealDomains() {
        assertThrows(CalculationException.class, () -> evaluator.evaluate("sine(90)"));
        assertThrows(CalculationException.class, () -> evaluator.evaluate("sinq(90)"));
        assertThrows(CalculationException.class, () -> evaluator.evaluate("s"));
        assertThrows(CalculationException.class, () -> evaluator.evaluate("sin 90"));
        assertThrows(CalculationException.class, () -> evaluator.evaluate("sqrt(9"));
        assertThrows(CalculationException.class, () -> evaluator.evaluate("sqrt(-1)"));
        assertThrows(CalculationException.class, () -> evaluator.evaluate("log(0)"));
        assertThrows(CalculationException.class, () -> evaluator.evaluate("log(-1)"));
        assertEquals(Math.tan(Math.toRadians(89)), evaluator.evaluate("tan(89)"), 0.000001);
        assertThrows(CalculationException.class, () -> evaluator.evaluate("tan(90)"));
        assertThrows(CalculationException.class, () -> evaluator.evaluate("10^10000"));
        assertThrows(CalculationException.class, () -> evaluator.evaluate("tan(pi / 2)", AngleUnit.RADIANS));
        assertThrows(CalculationException.class, () -> evaluator.evaluate("ln(-1)"));
        assertThrows(CalculationException.class, () -> evaluator.evaluate("cos(1)" + "unknown"));
    }
}
