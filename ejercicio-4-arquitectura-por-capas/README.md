# Ejercicio 4 — El mismo programa separado por capas

Refactorización del código del enunciado, que tenía las cuatro capas metidas en una sola
clase como métodos estáticos. El `manejarExcepcion` pasa a ser una clase propia en su
propio paquete, tal y como pide el enunciado.

## Capas

| Paquete | Clase | Responsabilidad |
|---|---|---|
| `aplicacion` | `Main` | Punto de entrada: monta las piezas, cierra los recursos y conecta el manejador global |
| `presentacion` | `VistaConsola` | Lo único que habla con el teclado y la pantalla |
| `presentacion` | `ControladorDeCalculo` | Recibe la petición, pide los datos y delega en el servicio |
| `servicio` | `ServicioDeCalculo` | Coordina el caso de uso (dividir 100 entre el número dado) |
| `negocio` | `Calculadora` | La regla de cálculo, sin entrada/salida |
| `excepcion` | `ManejadorGlobalDeExcepciones` | Traduce la excepción al mensaje que ve el usuario |

`ManejadorGlobalDeExcepciones` es el equivalente didáctico de un `@ControllerAdvice` de
Spring: un único punto donde se decide qué mensaje corresponde a cada excepción. Al estar
aislado se puede reutilizar desde cualquier controlador y probar con tests unitarios.

Ninguna capa intermedia captura excepciones: las dejan subir hasta `Main`, que es donde se
puede decidir qué hacer con ellas.

## Diferencia de orden respecto al original

La lectura por teclado sube a la capa de presentación, que es donde corresponde la
entrada/salida. Como consecuencia, el mensaje `Introduce un número:` aparece **antes** que
`Servicio: voy a realizar el cálculo`, mientras que en el original aparecía después (allí el
`Scanner` estaba dentro de la capa de negocio, que es justo lo que esta refactorización
corrige). Los mensajes son los mismos; solo cambia ese orden.

## Compilar y ejecutar

```bash
javac -encoding UTF-8 -d bin $(find src -name '*.java')
java -cp bin aplicacion.Main
```

Prueba con `0` (división entre cero), con texto (`NumberFormatException`) y con un número
válido como `4`.
