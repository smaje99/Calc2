# Calc2

Calc2 is a small Java desktop calculator for learning how an application can separate its
user interface from its calculation rules. It evaluates decimal arithmetic expressions with
parentheses and the `+`, `-`, `*`, `/`, and `^` operators.

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

The domain and application packages do not depend on Swing. This keeps arithmetic rules
usable and testable without opening a window.

## Expression rules

Multiplication and division bind more tightly than addition and subtraction. Parentheses can
change the evaluation order, and exponentiation is right-associative: `2^3^2` means `2^(3^2)`
and evaluates to `512`. Invalid expressions and division by zero are shown as readable
calculation errors.

## UI comparison

The original interface is preserved at [`docs/reference/ui-before-refactor.png`](docs/reference/ui-before-refactor.png).
The refactored interface is captured at [`docs/reference/ui-after-refactor.png`](docs/reference/ui-after-refactor.png).
