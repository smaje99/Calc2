package com.calc2.presentation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalculatorControllerTest {
    @Test
    void buildsAndEvaluatesAnExpressionFromCalculatorKeys() {
        CalculatorController controller = new CalculatorController();

        controller.press("2");
        controller.press("+");
        controller.press("3");
        controller.press("*");
        controller.press("4");
        controller.press("=");

        assertEquals("2+3*4", controller.getExpression());
        assertEquals("14.0", controller.getDisplay());
        assertFalse(controller.hasError());
    }

    @Test
    void startsANewExpressionAfterShowingAResult() {
        CalculatorController controller = new CalculatorController();
        controller.press("2");
        controller.press("+");
        controller.press("3");
        controller.press("=");

        controller.press("7");

        assertEquals("7", controller.getExpression());
        assertEquals("7", controller.getDisplay());
        assertFalse(controller.hasError());
    }

    @Test
    void showsCalculationErrorsAndAllowsTheUserToStartAgain() {
        CalculatorController controller = new CalculatorController();
        controller.press("2");
        controller.press("+");
        controller.press("=");

        assertTrue(controller.hasError());
        assertEquals("The expression is incomplete or invalid.", controller.getDisplay());

        controller.press("8");

        assertFalse(controller.hasError());
        assertEquals("8", controller.getExpression());
        assertEquals("8", controller.getDisplay());
    }

    @Test
    void entersDecimalsAndParenthesesWithoutStaleInput() {
        CalculatorController controller = new CalculatorController();
        controller.press("(");
        controller.press("1");
        controller.press(".");
        controller.press("5");
        controller.press("+");
        controller.press("2");
        controller.press(")");
        controller.press("=");

        assertEquals("(1.5+2)", controller.getExpression());
        assertEquals("3.5", controller.getDisplay());
    }
}
