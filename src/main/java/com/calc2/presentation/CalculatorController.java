package com.calc2.presentation;

import com.calc2.application.CalculateExpression;
import com.calc2.domain.CalculationException;
import com.calc2.application.CalculationHistory;
import com.calc2.application.CalculationRecord;
import com.calc2.domain.AngleUnit;
import java.time.Instant;

/**
 * Owns the calculator's input state and connects key presses to the calculation use case.
 *
 * <p>The controller translates a button press into a change in the current expression. It
 * also decides when a digit starts a fresh calculation, when an operator continues from a
 * previous result, and how to recover after an invalid expression. The window only displays
 * the state returned by this class, so arithmetic and input rules are not hidden inside
 * Swing event listeners.</p>
 *
 * <p>For example, calling {@code press("2")}, {@code press("+")}, {@code press("3")},
 * and {@code press("=")} displays {@code 5.0}. Pressing another digit after the result
 * starts a new expression instead of appending it to the previous one.</p>
 */
public class CalculatorController {
    private final CalculateExpression calculateExpression;
    private final CalculationHistory history = new CalculationHistory(50);
    private AngleUnit angleUnit = AngleUnit.DEGREES;
    private final StringBuilder expression = new StringBuilder();
    private String display = "0";
    private boolean resultDisplayed;
    private boolean errorDisplayed;

    /**
     * Creates a controller that uses the standard application calculation operation.
     */
    public CalculatorController() {
        this.calculateExpression = new CalculateExpression();
    }

    /**
     * Applies one calculator key to the current input state.
     *
     * <p>Accepted keys are the digits {@code 0} through {@code 9}, a decimal point,
     * {@code +}, {@code -}, {@code *}, {@code /}, {@code ^}, parentheses, {@code =}, and
     * {@code AC}. Unsupported or contextually misplaced keys are ignored so they cannot
     * create an accidental malformed token through normal button use.</p>
     *
     * @param key the visible key label, such as {@code "7"}, {@code "÷"}, or {@code "="}
     */
    public void press(String key) {
        if (key == null) {
            return;
        }
        String token = normalizeKey(key);
        if ("AC".equals(token)) {
            clear();
        } else if ("=".equals(token)) {
            evaluateCurrent();
        } else if (token.length() == 1 && Character.isDigit(token.charAt(0))) {
            enterDigit(token);
        } else if (".".equals(token)) {
            enterDecimalPoint();
        } else if ("(".equals(token)) {
            enterOpeningParenthesis();
        } else if (")".equals(token)) {
            enterClosingParenthesis();
        } else if (isOperator(token)) {
            enterOperator(token);
        }
    }

    /**
     * Returns the expression currently shown above the result.
     *
     * @return the typed expression, or an empty string before the first key press
     */
    public String getExpression() {
        return expression.toString();
    }

    /** Evaluates complete text supplied by the editable expression field.
     * @param expressionText complete user-entered expression
     */
    public void evaluate(String expressionText) {
        expression.setLength(0);
        if (expressionText != null) expression.append(expressionText);
        resultDisplayed = false;
        errorDisplayed = false;
        if (expression.length() == 0 || expression.toString().trim().isEmpty()) {
            display = "The expression is incomplete or invalid.";
            errorDisplayed = true;
            return;
        }
        evaluateCurrent();
    }

    /**
     * Returns the text that belongs in the calculator's main result display.
     *
     * @return the last result, the current expression, an error message, or {@code "0"}
     */
    public String getDisplay() {
        if (!display.isEmpty()) {
            return display;
        }
        return expression.length() == 0 ? "0" : expression.toString();
    }

    /**
     * Reports whether the last evaluation produced a user-correctable calculation error.
     *
     * @return {@code true} when the main display currently contains an error message
     */
    public boolean hasError() {
        return errorDisplayed;
    }

    /** Reports whether a successful result is currently available for operator continuation.
     * @return true after success, until new input or clear
     */
    public boolean isResultDisplayed() { return resultDisplayed; }

    /** Returns the current session history from newest to oldest.
     * @return immutable history snapshot
     */
    public java.util.List<CalculationRecord> getHistory() { return history.newestFirst(); }
    /** Changes the unit used by trigonometric functions.
     * @param value selected unit; null leaves the current unit unchanged
     */
    public void setAngleUnit(AngleUnit value) { if (value != null) angleUnit = value; }
    /** Restores a history expression to the input state without evaluating it.
     * @param value expression to recall
     */
    public void recall(String value) { expression.setLength(0); if (value != null) expression.append(value); display = ""; resultDisplayed = false; errorDisplayed = false; }

    /**
     * Clears the expression, result, and error state so a new calculation can begin.
     */
    public void clear() {
        expression.setLength(0);
        display = "0";
        resultDisplayed = false;
        errorDisplayed = false;
    }

    private void enterDigit(String digit) {
        if (resultDisplayed || errorDisplayed) {
            startFreshExpression();
        }
        if (expression.length() > 0 && expression.charAt(expression.length() - 1) == ')') {
            return;
        }
        expression.append(digit);
        updateInputDisplay();
    }

    private void enterDecimalPoint() {
        if (resultDisplayed || errorDisplayed) {
            startFreshExpression();
        }
        if (expression.length() > 0 && expression.charAt(expression.length() - 1) == ')') {
            return;
        }
        int tokenStart = currentNumberStart();
        if (expression.substring(tokenStart).contains(".")) {
            return;
        }
        if (expression.length() == 0 || isOperator(String.valueOf(expression.charAt(expression.length() - 1)))
                || expression.charAt(expression.length() - 1) == '(') {
            expression.append("0.");
        } else {
            expression.append('.');
        }
        updateInputDisplay();
    }

    private void enterOpeningParenthesis() {
        if (resultDisplayed || errorDisplayed) {
            startFreshExpression();
        }
        if (expression.length() == 0 || isOperator(String.valueOf(expression.charAt(expression.length() - 1)))
                || expression.charAt(expression.length() - 1) == '(') {
            expression.append('(');
            updateInputDisplay();
        }
    }

    private void enterClosingParenthesis() {
        if (expression.length() == 0) {
            return;
        }
        char last = expression.charAt(expression.length() - 1);
        if ((Character.isDigit(last) || last == ')') && hasUnclosedParenthesis()) {
            expression.append(')');
            updateInputDisplay();
        }
    }

    private void enterOperator(String operator) {
        if (resultDisplayed) {
            expression.setLength(0);
            expression.append(display);
            resultDisplayed = false;
        }
        if (errorDisplayed) {
            return;
        }
        if (expression.length() == 0) {
            if ("-".equals(operator)) {
                expression.append(operator);
                updateInputDisplay();
            }
            return;
        }
        char last = expression.charAt(expression.length() - 1);
        if (Character.isDigit(last) || last == ')') {
            expression.append(operator);
            updateInputDisplay();
        } else if ("-".equals(operator) && (isOperator(String.valueOf(last)) || last == '(')) {
            expression.append(operator);
            updateInputDisplay();
        }
    }

    private void evaluateCurrent() {
        if (expression.length() == 0) {
            return;
        }
        try {
            double result = calculateExpression.execute(expression.toString(), angleUnit);
            display = Double.toString(result);
            resultDisplayed = true;
            errorDisplayed = false;
            history.add(new CalculationRecord(expression.toString(), result, Instant.now()));
        } catch (CalculationException exception) {
            display = exception.getMessage();
            resultDisplayed = false;
            errorDisplayed = true;
        }
    }

    private void startFreshExpression() {
        expression.setLength(0);
        display = "";
        resultDisplayed = false;
        errorDisplayed = false;
    }

    private void updateInputDisplay() {
        display = "";
        errorDisplayed = false;
        resultDisplayed = false;
    }

    private boolean hasUnclosedParenthesis() {
        int depth = 0;
        for (int index = 0; index < expression.length(); index++) {
            if (expression.charAt(index) == '(') {
                depth++;
            } else if (expression.charAt(index) == ')') {
                depth--;
            }
        }
        return depth > 0;
    }

    private int currentNumberStart() {
        int start = expression.length();
        while (start > 0) {
            char previous = expression.charAt(start - 1);
            if (isOperator(String.valueOf(previous)) || previous == '(' || previous == ')') {
                break;
            }
            start--;
        }
        return start;
    }

    private boolean isOperator(String token) {
        return "+".equals(token) || "-".equals(token) || "*".equals(token)
                || "/".equals(token) || "^".equals(token);
    }

    private String normalizeKey(String key) {
        if ("÷".equals(key)) {
            return "/";
        }
        if ("×".equals(key)) {
            return "*";
        }
        if ("−".equals(key)) {
            return "-";
        }
        return key;
    }
}
