package com.calc2.application;

import com.calc2.domain.ExpressionEvaluator;

/**
 * Provides the application's single calculation operation.
 *
 * <p>This small use case connects the presentation layer to the domain. A button listener
 * can pass the expression that the user entered to {@link #execute(String)} and display
 * the returned number. The application class deliberately contains no Swing components;
 * the same operation can therefore be called from a graphical window, a command-line tool,
 * or a unit test.</p>
 *
 * <p>The use case delegates the arithmetic rules to {@link ExpressionEvaluator}. This
 * avoids duplicating precedence or validation logic in the user interface.</p>
 */
public class CalculateExpression {
    private final ExpressionEvaluator evaluator;

    /**
     * Creates a calculation use case backed by the standard expression evaluator.
     */
    public CalculateExpression() {
        this.evaluator = new ExpressionEvaluator();
    }

    /**
     * Calculates the value of the supplied expression.
     *
     * @param expression a complete arithmetic expression such as {@code "2 + 3 * 4"}
     * @return the expression's numeric result
     * @throws com.calc2.domain.CalculationException if the expression is malformed or
     *         attempts division by zero; callers can display the exception message
     */
    public double execute(String expression) {
        return evaluator.evaluate(expression);
    }
}
