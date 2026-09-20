# Ejercicio 1 — Desbordamiento de pila y manejador global

Código **tal cual viene en el enunciado**: no se pide modificarlo. Está aquí para poder
ejecutarlo y comprobar de primera mano qué ocurre.

## Compilar y ejecutar

```bash
javac -encoding UTF-8 -d bin src/principal/Main.java
java -cp bin principal.Main
```

## Qué ocurre

```
Aplicación iniciada
```

y nada más: el proceso termina con código de salida **1**. `Aplicación terminada` no se
imprime nunca. En el directorio desde el que se ejecuta aparece un fichero `errores.log`
con la traza del `StackOverflowError` (unas 1030 líneas: la cabecera más los 1024 marcos
de pila que la JVM imprime como máximo por defecto).

La explicación completa y la lista de cosas cuestionables del código están en
[`../RESPUESTAS.md`](../RESPUESTAS.md).
