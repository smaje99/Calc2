# Calc2

Calc2 es una calculadora científica de escritorio escrita en Java y Swing. Permite escribir
expresiones, evaluarlas con precisión de punto flotante, consultar cálculos recientes y
personalizar el modo angular, la precisión mostrada y el tema.

## Requisitos

- JDK 8 o posterior.
- Maven 3.6.3 o posterior.
- Un entorno gráfico compatible con Swing para abrir la aplicación.

## Compilar y ejecutar

Desde la raíz del repositorio:

```bash
mvn clean test
mvn package
java -jar target/calc2-1.0.0.jar
```

`mvn clean test` compila el proyecto y ejecuta las pruebas automatizadas. `mvn package`
genera el JAR ejecutable en `target/`. La aplicación no recibe argumentos de línea de
comandos por ahora.

## Funciones

- Operaciones: suma (`+`), resta (`−`), multiplicación (`×`), división (`÷`) y potencia (`^`).
- Agrupación con paréntesis y números decimales o en notación científica.
- Raíz cuadrada (`√`), seno (`sin`), coseno (`cos`), tangente (`tan`), logaritmo base 10
  (`log₁₀`) y logaritmo natural (`ln`).
- Constantes pi (`π` o `pi`) y Euler (`ℯ` o `e`).
- `Ans` inserta el último resultado correcto como número en la posición del cursor. Si el
  carácter anterior ya termina un valor, añade multiplicación explícita; por ejemplo, se puede
  insertar el resultado dentro de `cos()`.
- Historial de hasta 50 operaciones correctas durante la sesión. Elegir una entrada recupera
  su expresión para editarla; los errores no se guardan.
- Modo angular en grados o radianes, precisión mostrada de 2, 4, 6 o 10 decimales y temas
  oscuro o claro.

La pantalla conserva una altura fija para el resultado y los errores, de modo que un mensaje
de error no desplaza la cuadrícula. Los botones tienen descripciones emergentes.

## Sintaxis de expresiones

Las funciones se escriben con el nombre en minúsculas y un argumento entre paréntesis. Las
operaciones entre constantes, funciones y números deben incluir el operador correspondiente;
por ejemplo, usa `6.85E8*π`. `Ans` es un botón que inserta el último resultado como número; no
es una palabra reservada que se pueda escribir en una expresión. No se admite multiplicación
implícita como `2π`.

| Expresión | Significado |
| --- | --- |
| `2 + 3 * 4` | Multiplicación antes que suma; resultado `14` |
| `2^3^2` | Potencias asociadas de derecha a izquierda: `2^(3^2)` |
| `-2^2` | El signo unario se aplica después de la potencia: `-(2^2)` |
| `sqrt(9)` | Raíz cuadrada |
| `sin(90)` | Seno de 90 grados con el modo inicial |
| `log(100)` | Logaritmo base 10 |
| `ln(e)` | Logaritmo natural |
| `6.8514E8*π` | Notación científica y constante pi |

Se reconocen también los símbolos de teclado `*`, `/`, `-` y los símbolos visuales `×`, `÷`,
`−`. La unidad angular solo afecta a `sin`, `cos` y `tan`; las constantes `π` y `ℯ` mantienen
su valor matemático en ambos modos.

## Uso de la interfaz

- Escribe en el campo de expresión o utiliza el teclado en pantalla. El cursor y la selección
  permiten insertar o reemplazar texto; también se admite pegar texto.
- `Enter` calcula la expresión y `Escape` la borra. `AC` también limpia la operación.
- Los botones de función insertan una plantilla, como `cos()`, y dejan el cursor dentro de los
  paréntesis para introducir el argumento.
- Después de un resultado, un operador continúa el cálculo con ese resultado; un número inicia
  una operación nueva. Para usar el resultado anterior como argumento de una función, pulsa el
  botón de función y luego `Ans` con el cursor dentro de sus paréntesis.
- Cambiar el modo angular, la precisión o el tema actualiza la preferencia guardada.

## Errores y precisión

El evaluador rechaza expresiones incompletas, división por cero, raíces cuadradas de números
negativos, logaritmos de valores no positivos, tangentes en sus singularidades y resultados no
finitos. Los errores se muestran en la interfaz y no se incorporan al historial.

Los cálculos se conservan como valores `double`; la precisión elegida solo redondea el texto
mostrado. El formato usa punto decimal independientemente de la configuración regional. El
redondeo mostrado es `HALF_UP` y se eliminan ceros decimales finales, conservando al menos un
dígito decimal.

## Preferencias y datos de sesión

La unidad angular, la precisión y el tema se guardan con Java Preferences bajo el nodo de
preferencias del paquete `com.calc2`. Las claves son `angleUnit`, `maximumFractionDigits` y
`theme`. Si falta un valor, es inválido o el almacenamiento no está disponible, se utilizan
los valores predeterminados: grados, 10 dígitos y tema oscuro.

El historial y `Ans` viven en memoria y se reinician al cerrar la aplicación. El historial
retiene las 50 operaciones correctas más recientes.

## Estructura del código

```text
src/main/java/com/calc2/
├── Main.java                 # Arranque y composición de la aplicación
├── domain/                   # Evaluación, errores y unidad angular
├── application/              # Casos de uso, ajustes e historial
├── infrastructure/           # Adaptador de Java Preferences
└── presentation/             # Ventana Swing, controlador y formato de resultados
```

La interfaz no implementa reglas matemáticas: envía la expresión al controlador y al caso de
uso de cálculo. Consulta [la guía de arquitectura](docs/architecture.md) para conocer las
responsabilidades y dependencias entre estas partes.

## Capturas

- [Interfaz antes de la refactorización](docs/reference/ui-before-refactor.png)
- [Interfaz después de la refactorización](docs/reference/ui-after-refactor.png)
- [Interfaz con las capacidades científicas](docs/reference/ui-after-capabilities.png)
