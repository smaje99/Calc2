# Calc2 Calculator Capabilities Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:executing-plans` to implement this plan task by task. Steps use checkbox (`- [ ]`) syntax for tracking. Keep all work on `main` and create one commit per completed phase, as requested by the user.

**Goal:** Extend Calc2 with practical expression editing, a calculation history, common scientific functions, persistent user preferences, and beginner-friendly documentation while preserving its clean separation between calculation rules and Swing.

**Architecture:** Keep the current Maven application and its domain/application/presentation boundaries. Continue the existing recursive-descent interpreter for the calculator's small expression language. Let Swing's editable text component handle caret movement and text editing; keep calculation and history state in the controller/application. Add a small application port and a `java.util.prefs` adapter only for settings persistence, where the external storage boundary justifies it. Keep presentation formatting out of the evaluator.

**Tech Stack:** Java 8, Maven, Swing, JUnit 5, `java.util.prefs.Preferences`.

**Spec:** The user-approved feature scope in this conversation: keyboard editing, calculation history, scientific functions, persistent preferences, and student-friendly English documentation.

## Global Constraints

- The application currently evaluates decimal arithmetic, parentheses, and `+`, `-`, `*`, `/`, and `^` through `ExpressionEvaluator`.
- `ExpressionEvaluator` is a recursive-descent interpreter. Extend its grammar instead of introducing a general parser framework or a speculative AST hierarchy.
- `CalculatorController` currently owns the whole expression-building state. Moving caret navigation and selection into a Swing text component avoids reimplementing text-editor behavior.
- Keep domain and application packages independent of Swing and `java.util.prefs`.
- Keep the Java 8 language/API baseline and current Maven packaging.
- All production Java code, UI messages, and Javadocs remain in English. Javadocs should teach the intent, rules, examples, and relevant edge cases to beginner Java students.
- Preserve all existing arithmetic behavior unless a new requirement below explicitly expands it.
- Keep phases buildable and independently reviewable. Commit each phase on `main` before starting the next.

## Review Focus

- Pasted or edited text with missing operands, extra operators, or mismatched parentheses must produce a controlled calculation error; pin this in evaluator/controller tests.
- Function names that are unknown, incomplete, or only partially match a supported name must be rejected; pin this in parser tests.
- Nested function calls, functions mixed with powers, and unary signs must preserve the documented precedence; pin this in parser tests.
- `sqrt` of a negative value, `log`/`ln` of non-positive values, tangent at an undefined angle, and overflow must not display NaN or infinity as valid answers; pin each domain boundary in evaluator tests.
- Missing, invalid, or inaccessible saved preferences must not prevent startup and must fall back to documented defaults; pin malformed values with isolated preference tests and exercise storage failure handling.

## Principle Analysis

- **High cohesion / SRP:** The Swing input widget should own caret and selection behavior; the controller should coordinate calculation state; the evaluator should own grammar and arithmetic; a formatter should own display rounding.
- **Low coupling / DIP at persistence:** `java.util.prefs` is an external storage API. Keep it behind a narrow application-owned settings repository so domain logic and tests do not depend on platform storage.
- **KISS / YAGNI:** History is a bounded in-memory list for the current run. Do not add a database, event sourcing, CQRS, plugin APIs, or undo/redo command infrastructure.
- **DRY:** Define function names, constants, angle conversion, and preference defaults in one place each; do not duplicate the same rules in keypad labels, controller, and evaluator.
- **Validation boundaries:** The evaluator validates the complete expression and mathematical domain. Swing prevents accidental malformed editing where practical but is not the authority for expression validity. Preference loading must validate stored values and fall back safely.

## Pattern Candidates and Recommended Choices

- **Interpreter (keep and extend):** The grammar is small, recursive, and already implemented by precedence-level methods. Add functions and constants to the current parser, with tests for every grammar rule. Do not add a separate AST until the application needs operations such as symbolic manipulation or multiple evaluators over the same syntax tree.
- **Strategy (defer):** Degree/radian mode changes only angle conversion. An `AngleUnit` enum and one conversion point are enough for two fixed choices. Separate strategy classes would add indirection without meaningful algorithm variation.
- **Command (defer):** History only needs successful expression/result records and recall. A simple immutable record class plus bounded list is sufficient; commands, replay infrastructure, and undo semantics are not required.
- **Repository/Adapter (use narrowly):** A settings repository port and a Java Preferences adapter isolate persistence and make the settings use case testable without touching user profile data.
- **No broad architecture rewrite:** Keep the current layers and add only the classes required by these capabilities.

## Recommended Defaults and Behavior

- Keyboard input supports digits, decimal point, arithmetic operators, parentheses, Enter/Equals, Escape/Clear, and Backspace. The expression field supports normal caret movement, selection, paste, and replacement using standard Swing behavior.
- Successful calculations are added to a bounded session history of the most recent 50 entries. Failed calculations are not added. Selecting an entry restores its expression to the editable field.
- Scientific grammar uses lowercase function names: `sqrt(expression)`, `sin(expression)`, `cos(expression)`, `tan(expression)`, `log(expression)` (base 10), and `ln(expression)` (natural logarithm). Support constants `pi` and `e`. Functions accept a parenthesized expression, for example `sqrt(9)` and `sin(pi / 2)`.
- Angle mode is either degrees or radians; the default is degrees. It applies to `sin`, `cos`, and `tan`, while `pi` and all other functions retain their normal mathematical meaning.
- Reject non-finite results and undefined real-domain operations with a readable `CalculationException`. For tangent, define and test behavior close to an odd multiple of 90 degrees or pi/2 radians so floating-point approximation does not silently produce an enormous result.
- Display precision is a maximum number of fractional digits, selectable from 2, 4, 6, or 10. Evaluation always retains the full `double` result; only presentation rounds. Preserve at least one fractional digit for compatibility with the current display style.
- Persist angle mode, display precision, and theme (light or dark). Default to degrees, 10 fractional digits, and the current dark theme. If saved values are missing or invalid, use these defaults.
- Settings persist between application runs. History remains session-only in this scope.

## Implementation Phases

### Phase 1: Editable expression and keyboard support

**Purpose:** Make normal text editing native to Swing and ensure mouse and keyboard input follow the same calculation flow.

**Likely files:**
- Modify: `src/main/java/com/calc2/presentation/CalculatorWindow.java`
- Modify: `src/main/java/com/calc2/presentation/CalculatorController.java`
- Modify: `src/test/java/com/calc2/presentation/CalculatorControllerTest.java`
- Modify: `src/test/java/com/calc2/presentation/CalculatorWindowTest.java`

**Tasks:**
- [ ] **Step 1: Add controller tests** for `evaluate(String)`, `clear()`, and starting/continuing input after a result. Assert the existing `2+3=5`, then `+2=7` flow and that entering `7` after a result starts a new expression.
- [ ] **Step 2: Run the controller tests** with `mvn -Dtest=CalculatorControllerTest test`; confirm new API/state tests fail before implementation.
- [ ] **Step 3: Make the expression field the single editable source of truth** in `CalculatorWindow`. Use `JTextField.getText()`, `getCaretPosition()`, and `replaceSelection(...)` for keypad insertion; do not mirror the full expression in another mutable `StringBuilder`.
- [ ] **Step 4: Add controller methods** `public void evaluate(String expression)`, `public void clear()`, `public String getDisplay()`, and `public boolean hasError()`. Keep result continuation/new-entry handling in the window's input insertion method, which can inspect whether a result is currently displayed.
- [ ] **Step 5: Bind keyboard actions** through the focused field's `InputMap`/`ActionMap`: Enter evaluates, Escape clears, while native caret, selection, paste, Backspace, and Delete behavior remains intact.
- [ ] **Step 6: Route keypad insertion through one helper** that maps `×` to `*`, `÷` to `/`, and `−` to `-`, inserts at the caret or replaces the selection, and places the caret inside function templates in Phase 3.
- [ ] **Step 7: Run controller and window tests** with `mvn -Dtest=CalculatorControllerTest,CalculatorWindowTest test`; manually check caret editing, paste, selection replacement, and keyboard shortcuts.
- [ ] **Step 8: Update Javadocs** to explain the text field as the input owner and the controller as the calculation coordinator.
- [ ] **Step 9: Commit Phase 1** on `main` after its tests and manual editing checklist pass.

**Acceptance:** A user can type or edit an expression at any caret position, select and replace text, evaluate with Enter or the button, clear with Escape or the clear key, and continue a result with an operator. Invalid pasted text produces a controlled calculation error.

### Phase 2: Session calculation history

**Purpose:** Let users review and reuse recent successful calculations without introducing persistent storage for transient history.

**Likely files:**
- Create: `src/main/java/com/calc2/application/CalculationRecord.java`
- Create: `src/main/java/com/calc2/application/CalculationHistory.java`
- Modify: `src/main/java/com/calc2/presentation/CalculatorController.java`
- Modify: `src/main/java/com/calc2/presentation/CalculatorWindow.java`
- Create or modify: `src/test/java/com/calc2/application/CalculationHistoryTest.java`
- Modify: `src/test/java/com/calc2/presentation/CalculatorControllerTest.java`
- Modify: `src/test/java/com/calc2/presentation/CalculatorWindowTest.java`

**Tasks:**
- [ ] **Step 1: Add `CalculationHistoryTest`** for newest-first ordering, capacity 50, and retention of repeated identical expressions as separate records.
- [ ] **Step 2: Run the history test** with `mvn -Dtest=CalculationHistoryTest test`; confirm it fails because the history types do not exist yet.
- [ ] **Step 3: Create immutable `CalculationRecord`** with constructor `CalculationRecord(String expression, double result, Instant calculatedAt)` and getters for those three values. Reject null expression/time.
- [ ] **Step 4: Create `CalculationHistory`** with constructor `CalculationHistory(int capacity)`, `void add(CalculationRecord record)`, and `List<CalculationRecord> newestFirst()`. Return an unmodifiable snapshot and evict the oldest entries when capacity is exceeded.
- [ ] **Step 5: Inject one `CalculationHistory` into `CalculatorController`** and add a record only after `CalculateExpression.execute(...)` succeeds. Do not record calculation exceptions.
- [ ] **Step 6: Add a history dialog/list** in `CalculatorWindow` showing newest-first expression and result; selecting a row copies its expression into the field without evaluating it.
- [ ] **Step 7: Test controller recording and recall**, then run `mvn -Dtest=CalculationHistoryTest,CalculatorControllerTest,CalculatorWindowTest test`.
- [ ] **Step 8: Document** the session-only limit and immutable record fields in student-facing Javadocs.
- [ ] **Step 9: Commit Phase 2** on `main` after history behavior and UI recall pass review.

**Acceptance:** Each successful calculation appears in newest-first order, at most 50 entries remain, a selected expression can be edited or recalculated, and errors do not create history entries.

### Phase 3: Scientific expression grammar

**Purpose:** Extend the evaluator with common scientific functions and constants while keeping grammar rules explicit and testable.

**Likely files:**
- Modify: `src/main/java/com/calc2/domain/ExpressionEvaluator.java`
- Modify or create: `src/main/java/com/calc2/domain/CalculationException.java`
- Create: `src/main/java/com/calc2/domain/AngleUnit.java`
- Modify: `src/main/java/com/calc2/application/CalculateExpression.java`
- Modify: `src/main/java/com/calc2/presentation/CalculatorController.java`
- Modify: `src/main/java/com/calc2/presentation/CalculatorWindow.java`
- Modify: `src/test/java/com/calc2/domain/ExpressionEvaluatorTest.java`
- Modify: `src/test/java/com/calc2/application/CalculateExpressionTest.java`
- Modify: `src/test/java/com/calc2/presentation/CalculatorControllerTest.java`

**Tasks:**
- [ ] **Step 1: Add failing evaluator tests** for constants; each function; nested calls; `-2^2`, `2^-2`, and `2^3^2`; both angle units; unknown/incomplete function calls; invalid real domains; tangent singularities; and non-finite arithmetic results.
- [ ] **Step 2: Run `mvn -Dtest=ExpressionEvaluatorTest test`** and confirm the new grammar tests fail while existing arithmetic tests remain unchanged.
- [ ] **Step 3: Add `AngleUnit`** in the domain with `DEGREES` and `RADIANS`. Add `ExpressionEvaluator.evaluate(String expression, AngleUnit angleUnit): double`; retain `evaluate(String): double` as a degrees-default convenience method for existing callers.
- [ ] **Step 4: Extend `parsePrimary()`** to recognize parenthesized expressions, numeric values, exact lowercase constants `pi` and `e`, and function calls. Require parentheses after function names and reject trailing identifier characters.
- [ ] **Step 5: Add one private function evaluator** that accepts a normalized function name and evaluated argument, implements the six documented functions, and raises `CalculationException` for unknown functions or invalid domains.
- [ ] **Step 6: Pass `AngleUnit` through `CalculateExpression.execute(String expression, AngleUnit angleUnit): double`**. Keep the existing one-argument method as a degrees-default overload; do not introduce a function registry or per-function class hierarchy.
- [ ] **Step 7: Add finite-result checks** after operations and define a documented tangent tolerance around odd multiples of 90 degrees / pi/2 radians. Test the exact singular points and nearby valid points.
- [ ] **Step 8: Add keypad templates and angle selection** in the UI. Inserting `sin()` or another function must use `replaceSelection(...)` and place the caret between the parentheses.
- [ ] **Step 9: Run domain, application, and controller tests** with `mvn -Dtest=ExpressionEvaluatorTest,CalculateExpressionTest,CalculatorControllerTest test`; update README examples and Javadocs from the final grammar.
- [ ] **Step 10: Commit Phase 3** on `main` after all existing and new parser tests pass.

**Acceptance:** The documented grammar and evaluator agree; each scientific operation works in both relevant angle modes; malformed and undefined expressions fail cleanly; all prior arithmetic tests continue to pass.

### Phase 4: Preferences and settings persistence

**Purpose:** Preserve theme, angle mode, and display precision across runs while keeping platform storage outside the domain.

**Likely files:**
- Create: `src/main/java/com/calc2/application/CalculatorSettings.java`
- Create: `src/main/java/com/calc2/application/SettingsRepository.java`
- Create: `src/main/java/com/calc2/application/LoadCalculatorSettings.java` (or a similarly small use case)
- Create: `src/main/java/com/calc2/infrastructure/PreferencesSettingsRepository.java`
- Create: `src/main/java/com/calc2/presentation/ResultFormatter.java`
- Modify: `src/main/java/com/calc2/Main.java`
- Modify: `src/main/java/com/calc2/presentation/CalculatorWindow.java`
- Modify: `src/main/java/com/calc2/presentation/CalculatorController.java`
- Create: `src/test/java/com/calc2/application/CalculatorSettingsTest.java`
- Create: `src/test/java/com/calc2/application/LoadCalculatorSettingsTest.java`
- Create: `src/test/java/com/calc2/presentation/ResultFormatterTest.java`
- Create: `src/test/java/com/calc2/infrastructure/PreferencesSettingsRepositoryTest.java`

**Tasks:**
- [ ] **Step 1: Add settings tests** for defaults (`DEGREES`, 10 digits, dark), allowed precision values, rejection of unsupported values, and independent fallback of malformed persisted fields.
- [ ] **Step 2: Create `CalculatorSettings`** with constructor `CalculatorSettings(AngleUnit angleUnit, int maximumFractionDigits, CalculatorTheme theme)` and getters. Add application-level `CalculatorTheme` values `DARK` and `LIGHT`; accept only precision values 2, 4, 6, and 10.
- [ ] **Step 3: Define `SettingsRepository`** with `CalculatorSettings load()` and `void save(CalculatorSettings settings)`. Add `LoadCalculatorSettings.execute()` and `SaveCalculatorSettings.execute(CalculatorSettings)` as thin use cases only if UI construction would otherwise depend directly on the port.
- [ ] **Step 4: Implement `PreferencesSettingsRepository`** using an injected `Preferences` node, explicit keys, per-field default fallback, and safe handling of `SecurityException`/backing-store failures so preferences cannot prevent calculator startup or use.
- [ ] **Step 5: Test the adapter** using a unique child node under `Preferences.userRoot()` and remove that node in `finally`/test cleanup; never read or write the application's real preference node in tests.
- [ ] **Step 6: Add `ResultFormatter.format(double value, int maximumFractionDigits): String`** in presentation. Use a locale-independent decimal point, `HALF_UP` display rounding, at least one fractional digit, and no scientific notation for ordinary calculator values. Test negative zero and very large/small finite values explicitly.
- [ ] **Step 7: Wire settings at `Main`** by creating the preferences adapter once, loading settings, and passing the immutable settings plus a save callback/use case to the window/controller. Do not use a singleton.
- [ ] **Step 8: Add theme, angle, and precision controls** that create a new `CalculatorSettings`, apply it immediately, save it, and leave the underlying numeric result unchanged.
- [ ] **Step 9: Run settings, formatter, evaluator, and presentation tests** with `mvn test`; verify malformed settings use defaults and formatting does not alter evaluation.
- [ ] **Step 10: Commit Phase 4** on `main` after persistence, formatting, and settings UI checks pass.

**Acceptance:** Preferences survive application restarts, invalid stored values recover to defaults, tests use isolated storage, theme and angle changes take effect immediately, and display precision never changes the value used by subsequent calculations.

### Phase 5: Documentation, integration, and release checks

**Purpose:** Bring the student-facing guide, examples, and packaged application into agreement with the implemented behavior.

**Likely files:**
- Modify: `README.md`
- Modify: public Java Javadocs across `src/main/java/com/calc2`
- Optionally create: `docs/reference/keyboard-and-scientific-calculator.md`
- Modify: relevant application and UI tests

**Tasks:**
- [ ] **Step 1: Update README** with build/run commands, keyboard shortcuts, complete expression grammar, examples, angle defaults, history retention, preference keys/defaults, and domain errors.
- [ ] **Step 2: Audit public Javadocs** for purpose, parameters, return values, exceptions, state changes, and a short beginner-level example where it clarifies behavior.
- [ ] **Step 3: Check dependency direction** with source searches: domain has no Swing/application/preferences imports; application has no Swing/preferences imports; the Preferences adapter is the only class using `java.util.prefs`.
- [ ] **Step 4: Run `mvn clean package`** and generate Javadocs with `javadoc -Xdoclint:all` (through the configured project classpath/source path); resolve errors and new warnings.
- [ ] **Step 5: Complete the GUI checklist** for keyboard editing, history recall, scientific functions, theme switching, precision changes, and preference persistence after restart.
- [ ] **Step 6: Capture the final UI** at `docs/reference/ui-after-capabilities.png` if the UI changed materially; preserve existing before/after images.
- [ ] **Step 7: Commit the phase** with the staged changes, verify `git status --short` is clean, and confirm the phase commit is on the requested `main` branch.

**Acceptance:** README and Javadocs match the actual app; automated checks pass; the GUI checklist passes; the executable JAR launches with the documented command.

## Cross-Phase Verification Checklist

- Existing arithmetic contract remains covered: decimal operations, precedence, parentheses, right-associative powers, malformed expressions, and division by zero.
- Every new expression production has positive and negative domain tests.
- History and settings have deterministic tests independent of Swing windows and real user preference storage.
- Controller tests cover success/error transitions, editing actions, history recall, angle mode selection, and display-setting updates.
- `mvn clean package` succeeds on the Java 8 source/API target.
- Javadocs generate with `javadoc -Xdoclint:all` without warnings introduced by the work.
- Manual GUI check confirms Swing actions run on the event dispatch thread and text editing remains native.

## Risks and Tradeoffs

- Extending a hand-written parser is educational and fits the current scope, but grammar changes need carefully layered tests to prevent precedence regressions.
- Floating-point trigonometry near tangent singularities requires an explicit tolerance policy; document that policy instead of presenting approximate values as exact.
- Preferences are platform-managed and may be unavailable or malformed. The app must continue with defaults if loading/saving fails, and storage errors must not break calculation.
- Rich history UI can make a compact calculator crowded. Prefer a collapsible panel or dialog over shrinking the main keypad.
- Rounding is a display concern. Never feed formatted text back into the evaluator as the stored numeric result; recall should restore the original expression and recalculate when requested.

## Out of Scope

- Persistent calculation history, database storage, cloud sync, user accounts, or telemetry.
- Arbitrary user-defined functions, variables, symbolic algebra, graphing, and unit conversion.
- General-purpose parser libraries, plugin registries, command/event buses, undo/redo, and cross-platform UI frameworks.
