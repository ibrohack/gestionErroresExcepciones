# Ejercicio 2 — Ejemplo con NullPointerException

Programa de consola que recorre tres escenarios:

1. **Referencia directamente nula**: se invoca `length()` sobre un `String` que vale `null`.
2. **Atributo nulo dentro de una lista**: los objetos `Usuario` no son nulos, pero uno tiene
   el correo a `null`. Es el caso difícil de detectar, porque el fallo está en el dato y no
   en el código que lo recorre.
3. **Cómo evitarla**: comprobación explícita, `Objects.requireNonNull` y `Optional`.

Aprovecha los mensajes detallados de `NullPointerException` (*helpful NullPointerException*,
disponibles desde Java 14), que indican exactamente qué referencia era nula.

## Compilar y ejecutar

```bash
javac -encoding UTF-8 -d bin $(find src -name '*.java')
java -cp bin referencianula.Main
```

## Ficheros

- `src/referencianula/Main.java` — los tres escenarios.
- `src/referencianula/Usuario.java` — objeto de dominio con un atributo que puede ser nulo.
