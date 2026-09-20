# Ejercicio 5 — Proyecto `banco` corregido

El código del enunciado **no compila**. Aquí están corregidos los errores y aplicadas las
buenas prácticas, respetando el paquete y los nombres de clase originales (solo
`GestorFicheros` pasa a llamarse `GestorDeFicheros`, para no usar abreviaturas).

## Errores de compilación del código original

```
banco\GestorFicheros.java:7: error: unreported exception FileNotFoundException;
                                   must be caught or declared to be thrown
banco\GestorFicheros.java:9: error: unreported exception IOException;
                                   must be caught or declared to be thrown
```

Al corregir `GestorFicheros`, `javac` destapa el tercero (no lo muestra antes porque el
análisis de flujo se detiene en el fichero que ya ha fallado):

```
banco\Main.java:8: error: unreported exception TransferenciaException;
                         must be caught or declared to be thrown
```

Los tres son el mismo problema: **excepciones comprobadas que nadie captura ni declara**.

## Buenas prácticas aplicadas

- `try-with-resources` en `GestorDeFicheros`: el fichero se cierra aunque la lectura falle.
- La excepción se trata **donde se puede decidir qué hacer**. `GestorDeFicheros` y `Banco`
  la propagan; `Main` decide (mostrar el mensaje y continuar). Ningún `catch` vacío ni
  `printStackTrace` haciendo de "manejo".
- **Comprobada frente a no comprobada**: `TransferenciaException` (comprobada) para el fallo
  de negocio previsible y recuperable; `IllegalArgumentException` / `IllegalStateException`
  para incumplimientos de contrato, que son errores de programación.
- **Encadenar la causa**: `TransferenciaException(String, Throwable)` conserva la excepción
  original.
- **Una sola definición de cada regla**: el original comprobaba el saldo dos veces, en
  `Banco.transferir` y en `Cuenta.retirar`. Ahora la regla vive en `Cuenta.puedeRetirar` y
  `Banco` la consulta; `Cuenta.retirar` mantiene su comprobación solo como red de seguridad.
- **Atomicidad**: si el ingreso fallara después de la retirada, el dinero desaparecería. Se
  revierte la retirada y se informa encadenando la causa.
- Mensajes de excepción con datos útiles (titular, saldo, importe) en lugar de
  `"Saldo insuficiente"` a secas.
- No se capturan `Exception` ni `Throwable` de forma genérica.

Nota: el enunciado usa `double` para el saldo y se ha mantenido. En un sistema real se
usaría `BigDecimal`: `double` arrastra errores de redondeo inaceptables en dinero.

## Compilar y ejecutar

```bash
javac -encoding UTF-8 -d bin $(find src -name '*.java')
java -cp bin banco.Main
```

Hay que ejecutarlo desde esta carpeta para que encuentre `cuentas.txt`. El programa lee ese
fichero y después intenta leer `fichero-que-no-existe.txt` a propósito, para enseñar los dos
caminos: el que funciona y el que lanza `IOException`.
