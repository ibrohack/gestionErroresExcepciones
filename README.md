# Tarea de gestión de errores y excepciones

Resolución de los cinco ejercicios de la tarea de gestión de errores y excepciones
(2.º DAM, OPT/PGR).

| Carpeta | Ejercicio | Contenido |
|---|---|---|
| [`ejercicio-1-desbordamiento-de-pila/`](ejercicio-1-desbordamiento-de-pila/) | 1 | Código del enunciado, para reproducir el `StackOverflowError` y el manejador global |
| [`ejercicio-2-referencia-nula/`](ejercicio-2-referencia-nula/) | 2 | Ejemplo con `NullPointerException` y tres formas de evitarla |
| [`ejercicio-3-calculadora/`](ejercicio-3-calculadora/) | 3 | Calculadora con `ArithmeticException` e `IllegalArgumentException` |
| [`ejercicio-4-arquitectura-por-capas/`](ejercicio-4-arquitectura-por-capas/) | 4 | El programa del enunciado separado por capas, con el manejador global en su propia clase |
| [`ejercicio-5-banco/`](ejercicio-5-banco/) | 5 | Proyecto `banco` con los errores de compilación corregidos y las buenas prácticas aplicadas |

**Las respuestas escritas a todas las preguntas, junto con el código completo y las
salidas de consola reales, están en [`RESPUESTAS.md`](RESPUESTAS.md).**

## Cómo compilar y ejecutar

Proyectos Java sin herramienta de construcción: carpeta `src/` con paquetes, estilo Eclipse.
Se pueden importar directamente en Eclipse o IntelliJ como proyecto existente, o compilarse
a mano. Desarrollado y probado con **JDK 25**; necesita Java 14 o superior para los mensajes
detallados de `NullPointerException` del ejercicio 2.

```bash
cd ejercicio-3-calculadora
javac -encoding UTF-8 -d bin $(find src -name '*.java')
java -cp bin calculadora.Main
```

Cada carpeta tiene su propio `README.md` con la clase principal y las pruebas que conviene
hacer. El ejercicio 5 debe ejecutarse desde su carpeta, para que encuentre `cuentas.txt`.

## Convenciones

- Paquetes en minúsculas, clases en `PascalCase`, métodos y variables en `camelCase`.
- Sin abreviaturas: `ControladorDeCalculo`, `GestorDeFicheros`, `correoElectronico`.
- Todo el código, los comentarios y los mensajes están en español.
- `bin/` es la salida de compilación y está ignorada por git.
