# Calc2

Calc2 is a small Java desktop scientific calculator for learning how an application can
separate its user interface from its calculation rules. It evaluates arithmetic and common
scientific functions, keeps a short session history, and remembers display preferences.

## Requirements

- JDK 8 or newer
- Maven 3.6.3 or newer

## Build and run

Run the automated tests:

```bash
mvn clean test
```

Build the executable application JAR:

```bash
mvn package
```

Launch the desktop calculator:

```bash
java -jar target/calc2-1.0.0.jar
```

## Project structure

- `com.calc2.domain` parses expressions, applies operator precedence, and reports calculation errors.
- `com.calc2.application` exposes the `CalculateExpression` use case to the rest of the application.
- `com.calc2.presentation` contains the Swing window and its input-state controller.
- `com.calc2.Main` creates the presentation components and starts Swing on its event dispatch thread.

The domain and application packages do not depend on Swing or platform preferences. This
keeps calculation rules usable and testable without opening a window.

## Expression rules

Expressions support decimal numbers, parentheses, `+`, `-`, `*`, `/`, `^`, constants `pi` and
`e`, and `sqrt(x)`, `sin(x)`, `cos(x)`, `tan(x)`, `log(x)` (base 10), and `ln(x)`. Function
names are lowercase and require parentheses. Examples include `sqrt(9)`, `2^3^2` (which means
`2^(3^2)` and evaluates to `512`), and `sin(90)` in degree mode. Unary signs preserve the
usual power behavior: `-2^2` is `-4`, while `2^-2` is `0.25`. Trigonometry defaults to degrees;
the angle selector can switch it to radians. The `pi` constant retains its mathematical value
in either mode.

## Editing, history, and preferences

The expression field supports normal caret movement, selection, typing, paste, Backspace, and
Delete. Enter evaluates; Escape and `AC` clear. Keypad input replaces the selected text, and
function buttons insert templates such as `sin()` with the caret inside the parentheses.
History lists up to the 50 most recent successful calculations for the current run. Selecting
one restores its expression for editing; errors are not stored.

Display precision can be set to 2, 4, 6, or 10 fractional digits. Rounding applies only to the
shown result and uses a decimal point regardless of the computer's locale. The default is 10
digits. The theme defaults to dark; light is also available. Angle mode, precision, and theme
are saved using Java Preferences under the application package node with keys `angleUnit`,
`maximumFractionDigits`, and `theme`. Missing or invalid values independently fall back to
degrees, 10 digits, and dark. If preferences are unavailable, the calculator starts with those
defaults.

The calculator reports malformed expressions and mathematical domain errors, including
division by zero, negative square roots, logarithms of non-positive values, tangent singularities,
and overflow. Such expressions do not produce valid `NaN` or infinity results.
For tangent, an angle is treated as undefined when the absolute cosine of its radian
equivalent is below `1e-12`, which also covers floating-point approximations of odd
multiples of 90 degrees or `pi/2` radians.

## UI comparison

The original interface is preserved at [`docs/reference/ui-before-refactor.png`](docs/reference/ui-before-refactor.png).
The refactored interface is captured at [`docs/reference/ui-after-refactor.png`](docs/reference/ui-after-refactor.png).
