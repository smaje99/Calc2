package com.calc2.domain;

/**
 * Parses and evaluates arithmetic expressions without depending on a user interface.
 *
 * <p>The evaluator uses recursive descent: each parsing method recognizes one level of the
 * arithmetic grammar and calls the method for the next tighter level. This keeps precedence
 * visible in the code. Addition and subtraction have the lowest precedence, followed by
 * multiplication and division, unary signs, and exponentiation. Parentheses can group any
 * smaller expression. Exponentiation is right-associative, so {@code "2 ^ 3 ^ 2"} means
 * {@code 2 ^ (3 ^ 2)} and evaluates to {@code 512}.</p>
 *
 * <p>Numbers may contain a decimal point and whitespace may appear between tokens. The
 * evaluator does not change the supplied string. It reports malformed input and division
 * by zero using {@link CalculationException}, allowing callers such as a Swing controller
 * to show a helpful message instead of terminating the application.</p>
 *
 * <p>For example, {@code evaluate("2 + 3 * 4")} returns {@code 14.0}; multiplication is
 * completed before addition.</p>
 */
public class ExpressionEvaluator {
    private String input;
    private int position;

    /**
     * Creates an evaluator ready to parse arithmetic expressions.
     *
     * <p>The evaluator keeps only temporary parser position while an expression is being
     * read. Create one when a calculation service needs to evaluate user-entered text.</p>
     */
    public ExpressionEvaluator() {
        // No configuration is required for the calculator's arithmetic grammar.
    }

    /**
     * Evaluates one complete arithmetic expression and returns its numeric result.
     *
     * <p>Supported binary operators are {@code +}, {@code -}, {@code *}, {@code /}, and
     * {@code ^}. Parentheses and decimal numbers are also supported. The grammar follows
     * standard arithmetic precedence, and chained exponentiation is evaluated from right
     * to left. For example, {@code "2 + 3 * 4"} returns {@code 14.0}, while
     * {@code "2 ^ 3 ^ 2"} returns {@code 512.0}.</p>
     *
     * <p>The input is parsed from left to right using this precedence structure: addition
     * and subtraction, multiplication and division, unary signs, then powers and primary
     * values. Parentheses recursively parse a complete expression. After parsing, any
     * remaining character means that the input was not a single valid expression.</p>
     *
     * @param expression the text to parse; spaces between numbers and operators are allowed
     * @return the result as a {@code double}
     * @throws CalculationException if the text is empty or malformed, parentheses do not
     *         match, a number is invalid, or a division by zero is attempted
     */
    public double evaluate(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw invalidExpression();
        }
        input = expression;
        position = 0;
        double result = parseExpression();
        skipWhitespace();
        if (position != input.length()) {
            throw invalidExpression();
        }
        return result;
    }

    private double parseExpression() {
        double value = parseTerm();
        while (true) {
            if (consume('+')) {
                value += parseTerm();
            } else if (consume('-')) {
                value -= parseTerm();
            } else {
                return value;
            }
        }
    }

    private double parseTerm() {
        double value = parseUnary();
        while (true) {
            if (consume('*')) {
                value *= parseUnary();
            } else if (consume('/')) {
                double divisor = parseUnary();
                if (divisor == 0.0) {
                    throw new CalculationException("Division by zero is not allowed.");
                }
                value /= divisor;
            } else {
                return value;
            }
        }
    }

    private double parseUnary() {
        if (consume('+')) {
            return parseUnary();
        }
        if (consume('-')) {
            return -parseUnary();
        }
        return parsePower();
    }

    private double parsePower() {
        double base = parsePrimary();
        if (consume('^')) {
            return Math.pow(base, parseUnary());
        }
        return base;
    }

    private double parsePrimary() {
        if (consume('(')) {
            double value = parseExpression();
            if (!consume(')')) {
                throw invalidExpression();
            }
            return value;
        }
        return parseNumber();
    }

    private double parseNumber() {
        skipWhitespace();
        int start = position;
        boolean decimalPointSeen = false;
        boolean digitSeen = false;
        while (position < input.length()) {
            char current = input.charAt(position);
            if (Character.isDigit(current)) {
                digitSeen = true;
                position++;
            } else if (current == '.' && !decimalPointSeen) {
                decimalPointSeen = true;
                position++;
            } else {
                break;
            }
        }
        if (!digitSeen) {
            throw invalidExpression();
        }
        try {
            return Double.parseDouble(input.substring(start, position));
        } catch (NumberFormatException exception) {
            throw invalidExpression();
        }
    }

    private boolean consume(char expected) {
        skipWhitespace();
        if (position < input.length() && input.charAt(position) == expected) {
            position++;
            return true;
        }
        return false;
    }

    private void skipWhitespace() {
        while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
            position++;
        }
    }

    private CalculationException invalidExpression() {
        return new CalculationException("The expression is incomplete or invalid.");
    }
}
