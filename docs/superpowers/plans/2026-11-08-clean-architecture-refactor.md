# Calc2 Clean Architecture Refactor Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:executing-plans` to implement this plan task by task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the current Spanish, tightly coupled Swing calculator with a cleanly separated Java application whose engine and documentation are in English, then redesign the UI using the saved screenshot as the before reference.

**Architecture:** Use Maven's standard source layout and four small areas: domain expression parsing/evaluation, an application use case, Swing presentation, and a composition entry point. Keep the calculation engine independent of Swing and avoid speculative interfaces or infrastructure; the UI calls one application use case, which delegates to the domain evaluator.

**Tech Stack:** Java 8 language/API baseline, Maven, Swing, JUnit 5 for automated tests.

**Spec:** User request in this conversation; visual baseline: `docs/reference/ui-before-refactor.png`.

## Global Constraints

- Keep the project buildable with Maven and Java 8 or newer.
- Use English for Java code, identifiers, user-facing messages, and Javadocs.
- Write explanatory Javadocs for public types and methods, teaching the purpose, inputs, outputs, algorithm, and relevant examples to beginner Java students.
- Keep domain and application code free of Swing imports and UI state.
- Preserve the calculator's arithmetic purpose, decimal input, parentheses, and `+`, `-`, `*`, `/`, and `^` operators.
- Save the pre-refactor screenshot at `docs/reference/ui-before-refactor.png` for later visual comparison.

## Review Focus

- Operator precedence and parentheses: tests must prove multiplication binds more tightly than addition and nested parentheses override precedence.
- Exponent associativity: tests must pin `2^3^2` to right-associative exponentiation (`512`) or explicitly revise the spec before implementation.
- Malformed expressions and unbalanced parentheses: domain tests must verify a clear, controlled calculation error rather than a stack exception or process crash.
- Decimal and division behavior: tests must cover decimal operands and define division-by-zero handling as a clear calculation error.
- UI input-state transitions: controller tests or a focused manual checklist must cover entering an expression, evaluating it, and starting the next expression without stale tokens.

---

### Task 1: Establish Maven layout and characterize the calculation contract

**Files:**
- Move: `src/Main.java` to `src/main/java/Main.java`
- Move: `src/Operaciones.java` to `src/main/java/Operaciones.java`
- Move: `src/Constrains.java` to `src/main/java/Constrains.java`
- Move: `src/RoundedBorder.java` to `src/main/java/RoundedBorder.java`
- Modify: `pom.xml`
- Create: `src/test/java/com/calc2/domain/ExpressionEvaluatorTest.java`
- Create: `src/test/java/com/calc2/application/CalculateExpressionTest.java`

**Interfaces:**
- Produces: the agreed supported expression behavior and a Maven test setup before the implementation is replaced.

- [ ] **Step 1: Add JUnit 5 and configure Maven Surefire** in `pom.xml`; move the current Java files to `src/main/java`, remove the custom `sourceDirectory` override, and use Maven's standard source and test directories while retaining Java 8 as the compiler release.
- [ ] **Step 2: Write domain tests** for decimal arithmetic, each binary operator, precedence, nested parentheses, right-associative exponentiation, malformed tokens, unbalanced parentheses, and division by zero.
- [ ] **Step 3: Write use-case tests** proving that a valid expression returns a numeric calculation result and invalid input produces the documented calculation error.
- [ ] **Step 4: Run `mvn test`** and confirm the tests fail against the old implementation or absent replacement types for the expected behavioral reasons.

### Task 2: Replace the calculation engine with a pure domain evaluator

**Files:**
- Create: `src/main/java/com/calc2/domain/ExpressionEvaluator.java`
- Create: `src/main/java/com/calc2/domain/CalculationException.java`
- Modify: `src/test/java/com/calc2/domain/ExpressionEvaluatorTest.java`
- Remove: `src/main/java/Operaciones.java`

**Interfaces:**
- Produces: `double ExpressionEvaluator.evaluate(String expression)`; invalid expressions and division by zero raise `CalculationException` with an English, user-readable message.
- Consumes: test contract from Task 1.

- [ ] **Step 1: Implement tokenization and parsing** with clear separation between parsing rules and arithmetic operations; reject unknown characters, missing operands, malformed decimals, and mismatched parentheses.
- [ ] **Step 2: Implement evaluation** with documented precedence and right-associative exponentiation; do not mutate caller-owned expression data.
- [ ] **Step 3: Add detailed English Javadocs** explaining the evaluator's public contract, operator precedence, associativity, failure cases, and examples suitable for students.
- [ ] **Step 4: Run `mvn test`** and confirm all domain tests pass.

### Task 3: Add the application use case and rebuild the Swing UI

**Files:**
- Create: `src/main/java/com/calc2/application/CalculateExpression.java`
- Create: `src/main/java/com/calc2/presentation/CalculatorWindow.java`
- Create: `src/main/java/com/calc2/presentation/CalculatorController.java`
- Create: `src/main/java/com/calc2/Main.java`
- Modify: `src/test/java/com/calc2/application/CalculateExpressionTest.java`
- Remove: `src/main/java/Main.java`, `src/main/java/Constrains.java`, `src/main/java/RoundedBorder.java`

**Interfaces:**
- Consumes: `ExpressionEvaluator.evaluate(String)` from Task 2.
- Produces: `double CalculateExpression.execute(String)` for the presentation layer; a fresh Swing UI retaining the current calculator operations and providing readable spacing, alignment, typography, and clear error feedback.

- [ ] **Step 1: Implement `CalculateExpression`** as the single application entry point for calculation, translating domain results/errors into a presentation-friendly result without importing Swing.
- [ ] **Step 2: Implement the controller** to own button/input state and delegate evaluation to `CalculateExpression`.
- [ ] **Step 3: Implement the redesigned Swing window** as a view only; initialize it on the Swing event dispatch thread and keep layout/styling out of the domain and use case.
- [ ] **Step 4: Add detailed English Javadocs** for public application and presentation types and methods, describing the flow from button input to result display.
- [ ] **Step 5: Run `mvn test`** and complete a manual GUI checklist for digits, decimals, operators, parentheses, equals, errors, and starting a new expression.
- [ ] **Step 6: Capture the new UI** and compare it with `docs/reference/ui-before-refactor.png`; adjust the layout until the result display and keypad are visually clear and consistently aligned.

### Task 4: Finish the English migration and document build/run

**Files:**
- Modify: `README.md`
- Modify: `pom.xml`
- Remove: any remaining legacy Java sources after replacement is complete.

**Interfaces:**
- Consumes: final package structure and `com.calc2.Main` from Task 3.
- Produces: English project documentation and a runnable Maven JAR.

- [ ] **Step 1: Configure the JAR manifest** in `pom.xml` to use `com.calc2.Main`.
- [ ] **Step 2: Rewrite the README in English** with JDK/Maven prerequisites and exact commands: `mvn clean test`, `mvn package`, and `java -jar target/calc2-1.0.0.jar`.
- [ ] **Step 3: Review public Java APIs** to ensure all Javadocs and messages are English, specific, and pedagogical.
- [ ] **Step 4: Run `mvn clean package`** and launch the JAR to confirm the packaged desktop application opens.
