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
 * <p>Numbers may contain a decimal point or a scientific exponent, and whitespace may appear
 * between tokens. The keypad glyphs {@code ×}, {@code ÷}, and {@code −} are accepted alongside
 * their keyboard forms {@code *}, {@code /}, and {@code -}. The constants {@code π} and
 * {@code ℯ} are accepted alongside {@code pi} and {@code e}. The evaluator does not change the
 * supplied string. It reports malformed input and division by zero using
 * {@link CalculationException}, allowing callers such as a Swing controller to show a helpful
 * message instead of terminating the application.</p>
 *
 * <p>When tangent is evaluated, angles whose cosine has an absolute value below
 * {@code 1.0e-12} are treated as singular. This small tolerance avoids presenting a huge
 * floating-point approximation where the mathematical function is undefined.</p>
 *
 * <p>For example, {@code evaluate("2 + 3 * 4")} returns {@code 14.0}; multiplication is
 * completed before addition.</p>
 */
public class ExpressionEvaluator {
    private String input;
    private int position;
    private AngleUnit angleUnit;

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
     * {@code ^}; the keypad glyphs {@code −}, {@code ×}, and {@code ÷} are accepted too.
     * Parentheses, decimal numbers, scientific notation, and the constants {@code π} and
     * {@code ℯ} are also supported. The grammar follows
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
        return evaluate(expression, AngleUnit.DEGREES);
    }

    /** Evaluates a complete expression using the selected trigonometric angle unit.
     * @param expression complete expression text
     * @param angleUnit angle unit for {@code sin}, {@code cos}, and {@code tan}
     * @return the finite result
     * @throws CalculationException when the expression is malformed or outside the real domain
     */
    public double evaluate(String expression, AngleUnit angleUnit) {
        if (expression == null || expression.trim().isEmpty()) {
            throw invalidExpression();
        }
        if (angleUnit == null) throw new IllegalArgumentException("Angle unit is required.");
        input = expression;
        this.angleUnit = angleUnit;
        position = 0;
        double result = parseExpression();
        skipWhitespace();
        if (position != input.length()) {
            throw invalidExpression();
        }
        return finite(result);
    }

    private double parseExpression() {
        double value = parseTerm();
        while (true) {
            if (consume('+')) {
                value = finite(value + parseTerm());
            } else if (consume('-') || consume('−')) {
                value = finite(value - parseTerm());
            } else {
                return value;
            }
        }
    }

    private double parseTerm() {
        double value = parseUnary();
        while (true) {
            if (consume('*') || consume('×')) {
                value = finite(value * parseUnary());
            } else if (consume('/') || consume('÷')) {
                double divisor = parseUnary();
                if (divisor == 0.0) {
                    throw new CalculationException("Division by zero is not allowed.");
                }
                value = finite(value / divisor);
            } else {
                return value;
            }
        }
    }

    private double parseUnary() {
        if (consume('+')) {
            return parseUnary();
        }
        if (consume('-') || consume('−')) {
            return -parseUnary();
        }
        return parsePower();
    }

    private double parsePower() {
        double base = parsePrimary();
        if (consume('^')) {
            return finite(Math.pow(base, parseUnary()));
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
        skipWhitespace();
        if (position < input.length() && Character.isLetter(input.charAt(position))) {
            return parseIdentifier();
        }
        return parseNumber();
    }

    private double parseIdentifier() {
        int start = position;
        while (position < input.length() && Character.isLetter(input.charAt(position))) position++;
        String name = input.substring(start, position);
        if ("pi".equals(name) || "π".equals(name)) return Math.PI;
        if ("e".equals(name) || "ℯ".equals(name)) return Math.E;
        if (!"sqrt".equals(name) && !"sin".equals(name) && !"cos".equals(name)
                && !"tan".equals(name) && !"log".equals(name) && !"ln".equals(name)) {
            throw invalidExpression();
        }
        if (!consume('(')) throw invalidExpression();
        double argument = parseExpression();
        if (!consume(')')) throw invalidExpression();
        if ("sqrt".equals(name)) {
            if (argument < 0.0) throw new CalculationException("Square root requires a non-negative value.");
            return finite(Math.sqrt(argument));
        }
        if ("log".equals(name) || "ln".equals(name)) {
            if (argument <= 0.0) throw new CalculationException("Logarithms require a positive value.");
            return finite("log".equals(name) ? Math.log10(argument) : Math.log(argument));
        }
        double radians = angleUnit == AngleUnit.DEGREES ? Math.toRadians(argument) : argument;
        if ("tan".equals(name) && Math.abs(Math.cos(radians)) < 1.0e-12) {
            throw new CalculationException("Tangent is undefined at this angle.");
        }
        if ("sin".equals(name)) return finite(Math.sin(radians));
        if ("cos".equals(name)) return finite(Math.cos(radians));
        return finite(Math.tan(radians));
    }

    private double finite(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new CalculationException("The result is outside the calculator's finite range.");
        }
        return value;
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
        if (position < input.length() && (input.charAt(position) == 'e' || input.charAt(position) == 'E')) {
            position++;
            if (position < input.length() && (input.charAt(position) == '+' || input.charAt(position) == '-')) {
                position++;
            }
            int exponentStart = position;
            while (position < input.length() && Character.isDigit(input.charAt(position))) position++;
            if (position == exponentStart) throw invalidExpression();
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
