# Arquitectura de Calc2

Calc2 organiza la interfaz, las reglas de cálculo y la persistencia en paquetes con
responsabilidades pequeñas. La aplicación es una sola ventana Swing; no requiere servidor ni
base de datos.

## Paquetes y responsabilidades

| Paquete | Responsabilidad | Componentes principales |
| --- | --- | --- |
| `com.calc2.domain` | Gramática, evaluación matemática, errores y unidad angular | `ExpressionEvaluator`, `CalculationException`, `AngleUnit` |
| `com.calc2.application` | Operaciones que ofrece la aplicación y modelos de sesión/preferencias | `CalculateExpression`, `CalculationHistory`, `CalculationRecord`, `CalculatorSettings`, `SettingsRepository` |
| `com.calc2.presentation` | Ventana Swing, edición de la expresión, estado de entrada y formato de resultados | `CalculatorWindow`, `CalculatorController`, `ResultFormatter` |
| `com.calc2.infrastructure` | Adaptadores hacia mecanismos concretos del sistema | `PreferencesSettingsRepository` |
| `com.calc2` | Creación y conexión de los componentes | `Main` |

## Flujo de una operación

1. La ventana recibe texto desde el campo editable o los botones.
2. `CalculatorController` mantiene el estado de entrada, el último resultado y el historial.
3. Al calcular, el controlador llama a `CalculateExpression` con la expresión y el modo angular.
4. El caso de uso delega el análisis y la evaluación en `ExpressionEvaluator`.
5. El resultado numérico vuelve al controlador. Si es correcto, se guarda un `CalculationRecord`
   y se actualiza `Ans`; si la expresión falla, se conserva un mensaje legible para la ventana.
6. `ResultFormatter` aplica la precisión elegida para mostrar el valor, sin modificar el valor
   usado por los cálculos posteriores.

`ExpressionEvaluator` analiza la entrada con descenso recursivo. Cada método corresponde a un
nivel de precedencia: suma/resta, multiplicación/división, signos unarios, potencias y valores
primarios. Las potencias se asocian de derecha a izquierda. Los errores matemáticos se
representan con `CalculationException`.

## Flujo de preferencias

`Main` obtiene las preferencias con `Preferences.userNodeForPackage(Main.class)`, carga los
ajustes y los pasa a `CalculatorWindow`. La ventana informa los cambios a través de la
abstracción `SettingsRepository`; `PreferencesSettingsRepository` los guarda con Java
Preferences. El dominio y los casos de uso no dependen de Swing ni del almacenamiento concreto.

Las preferencias persistidas son unidad angular, cantidad máxima de decimales mostrados y
tema. Si la lectura está restringida o algún valor es desconocido, la aplicación usa los
valores predeterminados. Historial y `Ans` son estado de sesión y no se persisten.

## Pruebas

Las pruebas están en `src/test/java` y cubren el evaluador, los casos de uso, el controlador,
la ventana, el formato y el repositorio de preferencias. Para compilar y ejecutarlas:

```bash
mvn clean test
```

Las pruebas de persistencia usan nodos Java Preferences aislados para evitar modificar la
configuración personal del usuario.
