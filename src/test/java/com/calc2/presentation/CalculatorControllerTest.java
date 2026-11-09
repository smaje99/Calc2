package com.calc2.presentation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalculatorControllerTest {
    @Test
    void evaluatesEditedExpressionAndSupportsContinuingTheResult() {
        CalculatorController controller = new CalculatorController();
        controller.evaluate("2+3");
        assertEquals("5.0", controller.getDisplay());
        controller.press("+");
        controller.press("2");
        controller.press("=");
        assertEquals("7.0", controller.getDisplay());
    }

    @Test
    void clearResetsExpressionAndErrorState() {
        CalculatorController controller = new CalculatorController();
        controller.evaluate("7+");
        assertTrue(controller.hasError());
        controller.clear();
        assertEquals("", controller.getExpression());
        assertEquals("0", controller.getDisplay());
        assertFalse(controller.hasError());
    }

    @Test
    void recordsSuccessfulCalculationsAndDoesNotRecordErrors() {
        CalculatorController controller = new CalculatorController();
        controller.evaluate("2+3");
        controller.evaluate("2+");
        assertEquals(1, controller.getHistory().size());
        assertEquals("2+3", controller.getHistory().get(0).getExpression());
        controller.recall("4*2");
        assertEquals("4*2", controller.getExpression());
        assertEquals(1, controller.getHistory().size());
    }

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

    @Test
    void reusesTheLastSuccessfulAnswerAsAnOperand() {
        CalculatorController controller = new CalculatorController();
        controller.evaluate("6*9");

        controller.press("+");
        controller.press("Ans");
        controller.press("=");

        assertEquals("54.0+54.0", controller.getExpression());
        assertEquals("108.0", controller.getDisplay());
    }

    @Test
    void answerKeyStartsANewExpressionWhenAResultIsAlreadyDisplayed() {
        CalculatorController controller = new CalculatorController();
        controller.evaluate("6*9");

        controller.press("Ans");

        assertEquals("54.0", controller.getExpression());
        assertEquals("54.0", controller.getDisplay());
    }
}
