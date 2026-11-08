package com.calc2.domain;

/**
 * Describes an expression that the calculator cannot safely evaluate.
 *
 * <p>This exception is a domain-level explanation of a calculation problem. It is different
 * from an unexpected programming failure such as a {@link NullPointerException}: invalid
 * user input is an ordinary situation, so the presentation layer can catch this exception
 * and show its message without exposing a stack trace.</p>
 *
 * <p>Typical causes include a missing operand, an unknown character, mismatched parentheses,
 * or division by zero. For example, evaluating {@code "4 / 0"} raises this exception with a
 * message that can be displayed directly to the user.</p>
 */
public class CalculationException extends IllegalArgumentException {
    /**
     * Creates a calculation error with a message that explains what went wrong.
     *
     * @param message a short, English description suitable for a calculator user
     */
    public CalculationException(String message) {
        super(message);
    }
}
