# Ejercicio 3 — Calculadora con control de excepciones

Pide dos números y una operación (`+`, `-`, `*`, `/`) y controla:

| Situación | Excepción | Quién la lanza |
|---|---|---|
| División entre cero | `ArithmeticException` | La JVM, al evaluar `numeroUno / 0` con operandos enteros |
| Operación no soportada | `IllegalArgumentException` | `Calculadora.calcular`, en el `default` del `switch` |
| Operando no numérico | `NumberFormatException` | `Integer.parseInt` |

Dos detalles importantes:

- Los operandos son `int` a propósito. Con `double`, `10 / 0` no lanzaría nada: devolvería
  `Infinity`, y el ejercicio pide precisamente provocar la `ArithmeticException`.
- El `catch (NumberFormatException)` va **antes** que el `catch (IllegalArgumentException)`.
  `NumberFormatException` hereda de `IllegalArgumentException`, así que al revés el bloque
  sería inalcanzable y el compilador daría error.

## Compilar y ejecutar

```bash
javac -encoding UTF-8 -d bin $(find src -name '*.java')
java -cp bin calculadora.Main
```

## Ficheros

- `src/calculadora/Main.java` — entrada por teclado y tratamiento de las excepciones.
- `src/calculadora/Calculadora.java` — el cálculo; no imprime nada, solo calcula o lanza.
