# Tarea de gestión de errores y excepciones — respuestas

Respuestas a los cinco ejercicios, con el código completo y las salidas de consola reales
(todas obtenidas ejecutando los programas con JDK 25, no escritas a mano).

---

## Ejercicio 1 — Desbordamiento de pila y manejador global

### 1. Explica qué ocurre al ejecutarlo y por qué crees que ocurre

Al ejecutarlo se imprime una sola línea y el programa muere:

```
Aplicación iniciada
```

El proceso termina con **código de salida 1** y `Aplicación terminada` **no llega a
imprimirse nunca**. Además, en el directorio desde el que se ejecuta aparece un fichero
nuevo, `errores.log`, cuyas primeras líneas son:

```
================================
ERROR EN LA APLICACIÓN
Hilo: main
Tipo: java.lang.StackOverflowError
Mensaje: null
java.lang.StackOverflowError
	at principal.Main.provocarStackOverflow(Main.java:43)
	at principal.Main.provocarStackOverflow(Main.java:43)
	at principal.Main.provocarStackOverflow(Main.java:43)
	...
```

El fichero tiene unas 1030 líneas: la cabecera más los 1024 marcos de pila que la JVM
imprime como máximo por defecto en una traza.

**Por qué ocurre.** El método `provocarStackOverflow()` se llama a sí mismo sin ninguna
condición de parada (no tiene caso base). Cada llamada a un método reserva un *marco de
pila* (stack frame) en la pila del hilo, y ese marco no se libera hasta que el método
termina. Como ninguna llamada termina jamás, los marcos se van acumulando hasta agotar la
memoria de pila reservada para el hilo `main` (de unos cientos de KB por defecto). En ese
momento la JVM lanza un **`StackOverflowError`**.

Ese `StackOverflowError` no lo captura nadie con un `try/catch`, así que se propaga hasta
lo más alto de la pila y el hilo `main` muere. Antes de darlo por terminado, la JVM invoca
el manejador que se ha registrado con `Thread.setDefaultUncaughtExceptionHandler(...)`, que
es quien escribe el bloque de información y la traza en `errores.log`.

Dos detalles que se ven en la salida:

- `Mensaje: null` porque un `StackOverflowError` se construye sin mensaje descriptivo: la
  información útil está en la traza, no en el texto.
- El manejador registra el error, pero **no lo resuelve**: no devuelve el control al punto
  donde falló. Por eso la línea `System.out.println("Aplicación terminada")` queda
  descolgada, nunca se ejecuta, y la JVM termina con código de salida distinto de cero.

### 2. ¿Qué te llama la atención o parece extraño en este código?

- **Se está "manejando" un `Error`, no una `Exception`.** `StackOverflowError` hereda de
  `Error`, no de `Exception`. La jerarquía de Java separa ambas cosas a propósito: los
  `Error` indican fallos de la propia JVM (pila agotada, memoria agotada) de los que una
  aplicación no debe intentar recuperarse. Registrarlo y terminar de forma ordenada es
  aceptable; dar a entender que está "controlado" no lo es.

- **El problema real es un fallo de lógica, no de excepciones.** La recursión infinita sin
  caso base es un error de programación. Aquí las excepciones se están usando para tapar el
  síntoma en lugar de corregir la causa: por muy buen manejador que haya, el programa sigue
  sin hacer lo que debería.

- **El manejador escribe en un fichero justo cuando la pila acaba de agotarse.** Abrir un
  `FileWriter` y un `PrintWriter` requiere más llamadas a métodos, es decir, más pila. Un
  manejador de último recurso debería hacer lo mínimo imprescindible; si falla mientras
  maneja el primer error, se pierde toda la información.

- **Una excepción tragada.** El `catch (IOException ex) { ex.printStackTrace(); }` no hace
  nada útil: si el log no se puede escribir, la traza va a la consola —que es justo lo que
  se quería evitar— y el programa continúa como si nada.

- **`printStackTrace` y `System.out` en lugar de un registrador.** En cualquier proyecto
  real se usaría `java.util.logging`, SLF4J o Log4j, con niveles, rotación de ficheros y
  formato configurable. Además, la ruta `"errores.log"` está escrita a fuego y es relativa:
  el fichero acaba en el directorio desde el que se lanza el programa, que puede no ser el
  esperado ni tener permisos de escritura.

- **Clase anónima donde bastaba una lambda.** `Thread.UncaughtExceptionHandler` es una
  interfaz funcional (un solo método), así que todo ese bloque se reduce a
  `Thread.setDefaultUncaughtExceptionHandler((hilo, error) -> { ... });`.

- **Un manejador global no sustituye al `try/catch`.** Es una red de seguridad para lo que
  no se ha previsto. Las excepciones que sí se pueden prever deben tratarse en el punto
  donde se puede decidir qué hacer con ellas: reintentar, pedir otro dato, usar un valor por
  defecto o abortar.

- **No hay cierre ordenado.** `Aplicación terminada` sugiere que existe un final previsto,
  pero el programa nunca llega ahí. Si hubiera conexiones o ficheros abiertos, quedarían sin
  cerrar.

---

## Ejercicio 2 — Ejemplo que contemple `NullPointerException`

**Carpeta:** `ejercicio-2-referencia-nula/` · **Clase principal:** `referencianula.Main`

Una `NullPointerException` se lanza al intentar usar una referencia que vale `null` como si
apuntara a un objeto: invocar un método sobre ella, acceder a un atributo, recorrerla, etc.
Es una excepción **no comprobada** (`RuntimeException`), porque casi siempre delata un error
de programación y no una situación externa previsible.

El programa recorre tres escenarios:

1. **Referencia directamente nula**: `ciudad.length()` con `ciudad == null`.
2. **Atributo nulo dentro de una lista**: ni la lista ni los `Usuario` son nulos, pero uno
   tiene el correo a `null`. Es el caso difícil: el fallo está en el dato, no en el código
   que lo recorre.
3. **Cómo evitarla**: comprobación explícita, `Objects.requireNonNull` y `Optional`.

Desde Java 14 los mensajes son *helpful*: la JVM indica exactamente qué referencia era nula,
como se ve en la salida (`because the return value of "...getCorreoElectronico()" is null`).

### Salida real

```
--- Escenario 1: invocar un método sobre una referencia nula ---
Se ha capturado una NullPointerException.
Mensaje de la JVM: Cannot invoke "String.length()" because "<local0>" is null

--- Escenario 2: atributo nulo dentro de una lista ---
Ana -> ANA@EJEMPLO.COM
Luis -> no se puede convertir a mayúsculas un correo nulo.
Mensaje de la JVM: Cannot invoke "String.toUpperCase()" because the return value of "referencianula.Usuario.getCorreoElectronico()" is null

--- Escenario 3: cómo evitar la NullPointerException ---
a) El usuario Luis no tiene correo: no se intenta usarlo.
b) requireNonNull ha detenido la operación: El correo electrónico no puede ser nulo
c) Optional devuelve: (sin correo)
```

### Código

`src/referencianula/Usuario.java`

```java
package referencianula;

/**
 * Objeto de dominio sencillo. El atributo correoElectronico puede quedar a null
 * cuando se construye un usuario que todavía no lo ha facilitado: ese null es el
 * que provoca la NullPointerException "encubierta" del segundo escenario.
 */
public class Usuario {

	private final String nombre;
	private final String correoElectronico;

	public Usuario(String nombre, String correoElectronico) {
		this.nombre = nombre;
		this.correoElectronico = correoElectronico;
	}

	public String getNombre() {
		return nombre;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}
}
```

`src/referencianula/Main.java`

```java
package referencianula;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Ejercicio 2: ejemplo que contempla NullPointerException.
 *
 * Muestra tres escenarios:
 *   1. NullPointerException directa: se invoca un método sobre una referencia nula.
 *   2. NullPointerException encubierta: el atributo de un objeto de una lista es nulo.
 *   3. Cómo evitarla: comprobación previa, Objects.requireNonNull y Optional.
 */
public class Main {

	public static void main(String[] args) {

		escenarioUnoReferenciaDirectamenteNula();
		escenarioDosAtributoNuloDentroDeUnaLista();
		escenarioTresComoEvitarla();
	}

	/**
	 * Escenario 1: la referencia vale null, así que no hay ningún objeto sobre el
	 * que invocar length(). La JVM lanza NullPointerException en el momento de la
	 * llamada. Desde Java 14 el mensaje detalla qué referencia era nula.
	 */
	private static void escenarioUnoReferenciaDirectamenteNula() {

		System.out.println("--- Escenario 1: invocar un método sobre una referencia nula ---");

		String ciudad = null;

		try {
			System.out.println("Longitud del nombre de la ciudad: " + ciudad.length());
		} catch (NullPointerException excepcion) {
			System.out.println("Se ha capturado una NullPointerException.");
			System.out.println("Mensaje de la JVM: " + excepcion.getMessage());
		}

		System.out.println();
	}

	/**
	 * Escenario 2: la lista no es nula y los usuarios tampoco, pero uno de ellos
	 * tiene el correo a null. El fallo no está donde se escribe el bucle, sino en
	 * el dato: por eso este caso es el que más cuesta encontrar en producción.
	 */
	private static void escenarioDosAtributoNuloDentroDeUnaLista() {

		System.out.println("--- Escenario 2: atributo nulo dentro de una lista ---");

		List<Usuario> usuarios = new ArrayList<>();
		usuarios.add(new Usuario("Ana", "ana@ejemplo.com"));
		usuarios.add(new Usuario("Luis", null));

		for (Usuario usuario : usuarios) {
			try {
				System.out.println(usuario.getNombre() + " -> "
						+ usuario.getCorreoElectronico().toUpperCase());
			} catch (NullPointerException excepcion) {
				System.out.println(usuario.getNombre()
						+ " -> no se puede convertir a mayúsculas un correo nulo.");
				System.out.println("Mensaje de la JVM: " + excepcion.getMessage());
			}
		}

		System.out.println();
	}

	/**
	 * Escenario 3: las tres formas habituales de no llegar a la excepción.
	 */
	private static void escenarioTresComoEvitarla() {

		System.out.println("--- Escenario 3: cómo evitar la NullPointerException ---");

		Usuario usuarioSinCorreo = new Usuario("Luis", null);

		// a) Comprobación explícita antes de usar la referencia.
		String correo = usuarioSinCorreo.getCorreoElectronico();
		if (correo != null) {
			System.out.println("a) Correo en mayúsculas: " + correo.toUpperCase());
		} else {
			System.out.println("a) El usuario " + usuarioSinCorreo.getNombre()
					+ " no tiene correo: no se intenta usarlo.");
		}

		// b) Objects.requireNonNull: falla pronto y con un mensaje que explica el
		//    contrato incumplido, en lugar de arrastrar el null hasta otra capa.
		try {
			registrarCorreo(usuarioSinCorreo.getCorreoElectronico());
		} catch (NullPointerException excepcion) {
			System.out.println("b) requireNonNull ha detenido la operación: "
					+ excepcion.getMessage());
		}

		// c) Optional: el tipo expresa que el valor puede faltar y obliga a
		//    decidir qué hacer en ese caso.
		String correoMostrado = Optional.ofNullable(usuarioSinCorreo.getCorreoElectronico())
				.map(String::toUpperCase)
				.orElse("(sin correo)");

		System.out.println("c) Optional devuelve: " + correoMostrado);
	}

	private static void registrarCorreo(String correoElectronico) {
		Objects.requireNonNull(correoElectronico, "El correo electrónico no puede ser nulo");
		System.out.println("Correo registrado: " + correoElectronico);
	}
}
```

---

## Ejercicio 3 — Calculadora con control de excepciones

**Carpeta:** `ejercicio-3-calculadora/` · **Clase principal:** `calculadora.Main`

El programa pide `Número 1`, `Número 2` y `Operación`, realiza `+`, `-`, `*` y `/`, y
controla:

| Situación | Excepción | Quién la lanza |
|---|---|---|
| División entre cero | `ArithmeticException` | La JVM, al evaluar `numeroUno / 0` con enteros |
| Operación desconocida | `IllegalArgumentException` | `Calculadora.calcular`, en el `default` del `switch` |
| Operando no numérico | `NumberFormatException` | `Integer.parseInt` |

Dos decisiones de diseño que conviene justificar:

- **Los operandos son `int` a propósito.** Con `double`, `10 / 0` no lanzaría nada: daría
  `Infinity`, porque la aritmética en coma flotante sigue la norma IEEE 754. La
  `ArithmeticException` que pide el enunciado solo aparece con división entera.
- **El orden de los `catch` importa.** `NumberFormatException` **hereda de**
  `IllegalArgumentException`. Si el `catch (IllegalArgumentException)` fuese primero,
  atraparía también los errores de formato y el bloque específico quedaría inalcanzable: el
  compilador daría error. Por eso va del tipo más concreto al más general.
- La lógica de cálculo no imprime nada: solo calcula o lanza. Quien decide qué mensaje ver
  la persona usuaria es `Main`.

### Salidas reales

Cuatro ejecuciones (lo que aparece tras cada dos puntos es lo tecleado):

```
Número 1: 10
Número 2: 0
Operación: /
Error aritmético: no se puede dividir entre cero.
Detalle técnico: / by zero
Fin del programa.
```

```
Número 1: 10
Número 2: 3
Operación: %
Error de uso: Operación desconocida: %
Operaciones permitidas: +  -  *  /
Fin del programa.
```

```
Número 1: 10
Número 2: 5
Operación: +
Resultado: 10 + 5 = 15
Fin del programa.
```

```
Número 1: diez
Error de formato: los operandos deben ser números enteros.
Detalle técnico: For input string: "diez"
Fin del programa.
```

### Código

`src/calculadora/Calculadora.java`

```java
package calculadora;

/**
 * Lógica de la calculadora. No imprime nada por pantalla: se limita a calcular o
 * a lanzar la excepción que corresponda, y deja que sea quien la llama (la clase
 * Main) quien decida qué mensaje mostrar.
 */
public class Calculadora {

	/**
	 * @param numeroUno primer operando
	 * @param numeroDos segundo operando
	 * @param operacion  una de: + - * /
	 * @return el resultado de aplicar la operación
	 * @throws ArithmeticException      si se divide entre cero
	 * @throws IllegalArgumentException si la operación no es una de las soportadas
	 */
	public int calcular(int numeroUno, int numeroDos, String operacion) {

		switch (operacion) {

			case "+":
				return numeroUno + numeroDos;

			case "-":
				return numeroUno - numeroDos;

			case "*":
				return numeroUno * numeroDos;

			case "/":
				// Con operandos enteros, la propia JVM lanza ArithmeticException
				// ("/ by zero") al dividir entre cero. No hace falta comprobarlo
				// a mano: basta con dejar que la excepción suba.
				return numeroUno / numeroDos;

			default:
				throw new IllegalArgumentException("Operación desconocida: " + operacion);
		}
	}
}
```

`src/calculadora/Main.java`

```java
package calculadora;

import java.util.Scanner;

/**
 * Ejercicio 3: calculadora que pide dos números y una operación (+ - * /) y
 * controla ArithmeticException (división entre cero) e IllegalArgumentException
 * (operación desconocida).
 */
public class Main {

	public static void main(String[] args) {

		Calculadora calculadora = new Calculadora();

		try (Scanner teclado = new Scanner(System.in)) {

			System.out.print("Número 1: ");
			int numeroUno = Integer.parseInt(teclado.nextLine().trim());

			System.out.print("Número 2: ");
			int numeroDos = Integer.parseInt(teclado.nextLine().trim());

			System.out.print("Operación: ");
			String operacion = teclado.nextLine().trim();

			int resultado = calculadora.calcular(numeroUno, numeroDos, operacion);

			System.out.println("Resultado: " + numeroUno + " " + operacion + " "
					+ numeroDos + " = " + resultado);

		} catch (ArithmeticException excepcion) {

			// División entre cero: la lanza la propia JVM al evaluar numeroUno / 0.
			System.out.println("Error aritmético: no se puede dividir entre cero.");
			System.out.println("Detalle técnico: " + excepcion.getMessage());

		} catch (NumberFormatException excepcion) {

			// Debe ir ANTES que IllegalArgumentException: NumberFormatException
			// hereda de ella y, si se pusiera después, nunca se alcanzaría este
			// bloque (de hecho, el compilador daría error por código inalcanzable).
			System.out.println("Error de formato: los operandos deben ser números enteros.");
			System.out.println("Detalle técnico: " + excepcion.getMessage());

		} catch (IllegalArgumentException excepcion) {

			// Operación no soportada: la lanza Calculadora.calcular.
			System.out.println("Error de uso: " + excepcion.getMessage());
			System.out.println("Operaciones permitidas: +  -  *  /");
		}

		System.out.println("Fin del programa.");
	}
}
```

---

## Ejercicio 4 — El mismo programa separado por capas

**Carpeta:** `ejercicio-4-arquitectura-por-capas/` · **Clase principal:** `aplicacion.Main`

### Rehazlo separándolo por capas, con `manejarExcepcion` en una clase separada

El código del enunciado tenía las cuatro capas simuladas como métodos estáticos de una sola
clase. La refactorización las convierte en clases y paquetes de verdad:

```
src/
├─ aplicacion/Main.java                        punto de entrada
├─ presentacion/VistaConsola.java              teclado y pantalla
├─ presentacion/ControladorDeCalculo.java      recibe la petición y delega
├─ servicio/ServicioDeCalculo.java             coordina el caso de uso
├─ negocio/Calculadora.java                    la regla de cálculo
└─ excepcion/ManejadorGlobalDeExcepciones.java traduce excepción -> mensaje
```

| Capa | Responsabilidad | Por qué |
|---|---|---|
| `aplicacion` | Monta las piezas, cierra los recursos y conecta el manejador global | Es el único sitio que conoce todas las capas |
| `presentacion` | `VistaConsola` es lo único que habla con el teclado y la pantalla; `ControladorDeCalculo` recibe la petición, pide los datos y delega | Aísla la entrada/salida: cambiar la consola por una interfaz web no afecta al resto |
| `servicio` | Coordina el caso de uso "dividir 100 entre el número dado" | Orquesta sin contener reglas ni entrada/salida |
| `negocio` | La regla de cálculo, sin entrada/salida | Código puro, reutilizable y trivial de probar con tests |
| `excepcion` | `ManejadorGlobalDeExcepciones` traduce cada excepción al mensaje de usuario | Un único punto de decisión, reutilizable y testeable |

`ManejadorGlobalDeExcepciones` en su propia clase es el equivalente didáctico de un
`@ControllerAdvice` de Spring: cuando mañana haya diez controladores, todos comparten el
mismo criterio de traducción de errores sin duplicar un solo `if`.

**Ninguna capa intermedia captura excepciones.** `Calculadora`, `ServicioDeCalculo` y
`ControladorDeCalculo` las dejan subir, porque ahí no hay información suficiente para
decidir qué hacer. Solo `Main` captura, porque es quien sabe que la respuesta correcta es
mostrar un mensaje y terminar de forma ordenada.

**Un cambio de orden respecto al original.** La lectura por teclado sube a la capa de
presentación, que es donde corresponde. Como consecuencia, `Introduce un número:` aparece
ahora antes que `Servicio: voy a realizar el cálculo`, mientras que en el original aparecía
después: allí el `Scanner` estaba dentro de la capa de negocio, que es justo el defecto que
esta refactorización corrige. Los mensajes son los mismos; solo cambia ese orden.

### Salida de la versión por capas

Con entrada `0`:

```
Controlador: recibo la petición
Introduce un número: 0
Servicio: voy a realizar el cálculo

===== MANEJADOR GLOBAL =====
Error: no se puede dividir entre cero.
Aplicación terminada
```

Con entrada `4`:

```
Controlador: recibo la petición
Introduce un número: 4
Servicio: voy a realizar el cálculo
Servicio: cálculo terminado
Resultado: 25
Controlador: petición procesada
Aplicación terminada
```

### ¿Qué muestra por pantalla al ejecutar el código anterior introduciendo el valor 0?

Ejecutando el código **original** del enunciado e introduciendo `0`:

```
Controlador: recibo la petición
Servicio: voy a realizar el cálculo
Introduce un número: 0

===== MANEJADOR GLOBAL =====
Error: no se puede dividir entre cero.
Aplicación terminada
```

Recorrido: `main` llama a `controlador()`, que imprime su primer mensaje y llama a
`servicio()`, que imprime el suyo y llama a `calcular()`. Ahí se pide el número; al teclear
`0`, la expresión `100 / numero` lanza una `ArithmeticException` con el mensaje `/ by zero`.
A partir de ese punto **no se imprime nada más de las tres capas**: el control salta al
`catch (RuntimeException e)` de `main`, que llama a `manejarExcepcion(e)`. Este imprime una
línea en blanco, la cabecera del manejador y, como la excepción es una `ArithmeticException`,
el mensaje `Error: no se puede dividir entre cero.`. Por último, ya fuera del `try/catch`,
se imprime `Aplicación terminada`.

Obsérvese que `Resultado:`, `Servicio: cálculo terminado` y `Controlador: petición procesada`
**no aparecen**, y que el programa termina con normalidad (código de salida 0), porque la
excepción **sí** se ha capturado.

### ¿Por qué no se ejecuta `System.out.println("Servicio: cálculo terminado")`?

Porque esa línea está **después** de la llamada a `calcular()`, y `calcular()` no termina
nunca de forma normal: termina lanzando una `ArithmeticException`.

Cuando se lanza una excepción, el flujo normal de ejecución se interrumpe de inmediato en la
instrucción que falla. La JVM abandona el resto del método y empieza a **desandar la pila de
llamadas** (*stack unwinding*) buscando, en cada método por el que pasa, un `try/catch`
capaz de tratar ese tipo de excepción:

1. En `calcular()` no hay `try/catch` → se abandona el método (no se imprime `Resultado:`).
2. En `servicio()` tampoco → se abandona el método **sin volver al punto de la llamada**, así
   que la línea `System.out.println("Servicio: cálculo terminado")`, que era la siguiente,
   se salta por completo.
3. En `controlador()` tampoco → se abandona igual (no se imprime `Controlador: petición
   procesada`).
4. En `main()` sí hay `catch (RuntimeException e)`, y como `ArithmeticException` es una
   `RuntimeException`, la excepción se captura ahí. La ejecución **continúa dentro del
   `catch`**, no en el punto donde se produjo el fallo.

La clave es que una excepción **no es como un `return`**: no devuelve el control a la línea
siguiente de quien llamó. Ese es precisamente el mecanismo que permite que las capas
intermedias no se ensucien con comprobaciones de error y que el problema se trate en un
único punto, el que sabe qué hacer. Si se quisiera ejecutar código pase lo que pase (cerrar
un fichero, liberar una conexión), habría que ponerlo en un bloque `finally` o usar
try-with-resources.

### Código

`src/aplicacion/Main.java`

```java
package aplicacion;

import excepcion.ManejadorGlobalDeExcepciones;
import negocio.Calculadora;
import presentacion.ControladorDeCalculo;
import presentacion.VistaConsola;
import servicio.ServicioDeCalculo;

/**
 * Ejercicio 4: el mismo programa del enunciado, separado por capas.
 *
 *   aplicacion   -> punto de entrada: monta las piezas y cierra los recursos
 *   presentacion -> entrada/salida por consola (VistaConsola) y controlador
 *   servicio     -> coordinación del caso de uso
 *   negocio      -> la regla de cálculo
 *   excepcion    -> manejador global, en su propia clase
 */
public class Main {

	public static void main(String[] args) {

		ManejadorGlobalDeExcepciones manejador = new ManejadorGlobalDeExcepciones();

		try (VistaConsola vista = new VistaConsola()) {

			ControladorDeCalculo controlador = new ControladorDeCalculo(
					vista,
					new ServicioDeCalculo(new Calculadora()));

			try {
				controlador.procesarPeticion();
			} catch (RuntimeException excepcion) {
				manejador.manejar(excepcion);
			}
		}

		System.out.println("Aplicación terminada");
	}
}
```

`src/presentacion/VistaConsola.java`

```java
package presentacion;

import java.util.Scanner;

/**
 * Capa de presentación: es la única clase que habla con el teclado y la pantalla.
 * Sacar aquí el Scanner permite que el servicio y el negocio sean código puro,
 * sin entrada/salida, y por tanto reutilizables y fáciles de probar.
 */
public class VistaConsola implements AutoCloseable {

	private final Scanner teclado = new Scanner(System.in);

	/**
	 * @throws NumberFormatException si lo introducido no es un número entero
	 */
	public int pedirNumero() {

		System.out.print("Introduce un número: ");

		return Integer.parseInt(teclado.nextLine().trim());
	}

	public void mostrarResultado(int resultado) {
		System.out.println("Resultado: " + resultado);
	}

	public void mostrarMensaje(String mensaje) {
		System.out.println(mensaje);
	}

	@Override
	public void close() {
		teclado.close();
	}
}
```

`src/presentacion/ControladorDeCalculo.java`

```java
package presentacion;

import servicio.ServicioDeCalculo;

/**
 * Capa de controlador: recibe la petición, pide los datos a la vista, delega el
 * cálculo en el servicio y devuelve la respuesta. No contiene reglas de negocio
 * ni captura excepciones: las deja subir hasta el punto donde se decide qué
 * hacer con ellas (el manejador global).
 */
public class ControladorDeCalculo {

	private final VistaConsola vista;
	private final ServicioDeCalculo servicio;

	public ControladorDeCalculo(VistaConsola vista, ServicioDeCalculo servicio) {
		this.vista = vista;
		this.servicio = servicio;
	}

	public void procesarPeticion() {

		System.out.println("Controlador: recibo la petición");

		int numero = vista.pedirNumero();

		int resultado = servicio.calcular(numero);

		vista.mostrarResultado(resultado);

		System.out.println("Controlador: petición procesada");
	}
}
```

`src/servicio/ServicioDeCalculo.java`

```java
package servicio;

import negocio.Calculadora;

/**
 * Capa de servicio: coordina el caso de uso "dividir 100 entre el número que ha
 * introducido el usuario". No captura la ArithmeticException porque aquí no se
 * puede decidir qué hacer con ella: no hay forma de saber si conviene pedir otro
 * número, mostrar un mensaje o abortar. Esa decisión es de la capa superior.
 */
public class ServicioDeCalculo {

	private static final int DIVIDENDO = 100;

	private final Calculadora calculadora;

	public ServicioDeCalculo(Calculadora calculadora) {
		this.calculadora = calculadora;
	}

	public int calcular(int divisor) {

		System.out.println("Servicio: voy a realizar el cálculo");

		int resultado = calculadora.dividir(DIVIDENDO, divisor);

		System.out.println("Servicio: cálculo terminado");

		return resultado;
	}
}
```

`src/negocio/Calculadora.java`

```java
package negocio;

/**
 * Capa de negocio: la regla de cálculo, sin entrada ni salida por consola y sin
 * saber nada de las capas que la usan. Si la operación no se puede realizar,
 * deja que la excepción suba a quien pueda decidir qué hacer con ella.
 */
public class Calculadora {

	/**
	 * @throws ArithmeticException si el divisor es cero (la lanza la propia JVM)
	 */
	public int dividir(int dividendo, int divisor) {
		return dividendo / divisor;
	}
}
```

`src/excepcion/ManejadorGlobalDeExcepciones.java`

```java
package excepcion;

/**
 * Manejador global de excepciones, en su propia clase y su propio paquete.
 *
 * Es el equivalente didáctico al @ControllerAdvice de Spring: un único punto en
 * toda la aplicación que traduce una excepción técnica al mensaje que ve la
 * persona usuaria. Al estar aislado se puede reutilizar desde cualquier
 * controlador, probar con tests unitarios y modificar sin tocar el resto de capas.
 */
public class ManejadorGlobalDeExcepciones {

	public void manejar(RuntimeException excepcion) {

		System.out.println();
		System.out.println("===== MANEJADOR GLOBAL =====");

		if (excepcion instanceof NumberFormatException) {

			System.out.println("Error: debes introducir un número.");

		} else if (excepcion instanceof ArithmeticException) {

			System.out.println("Error: no se puede dividir entre cero.");

		} else {

			System.out.println("Error inesperado: " + excepcion.getMessage());
		}
	}
}
```

---

## Ejercicio 5 — Proyecto `banco`: errores de compilación y buenas prácticas

**Carpeta:** `ejercicio-5-banco/` · **Clase principal:** `banco.Main`

### A) Errores de compilación

El código del enunciado **no compila**. Al intentarlo, `javac` informa de dos errores:

```
banco\GestorFicheros.java:7: error: unreported exception FileNotFoundException;
                                   must be caught or declared to be thrown
FileReader lector = new FileReader("cuentas.txt");
                    ^
banco\GestorFicheros.java:9: error: unreported exception IOException;
                                   must be caught or declared to be thrown
lector.close();
            ^
2 errors
```

Y en cuanto se corrige `GestorFicheros`, aparece un tercero que antes quedaba oculto (el
análisis de flujo del compilador se detiene en el fichero que ya ha fallado):

```
banco\Main.java:8: error: unreported exception TransferenciaException;
                         must be caught or declared to be thrown
 banco.transferir(cuenta1, cuenta2, 300);
                 ^
```

Los tres son **el mismo problema**: excepciones **comprobadas** (*checked*) que nadie captura
ni declara. Java obliga a que toda excepción que herede de `Exception` sin heredar de
`RuntimeException` esté, en cada método por el que pasa, o bien capturada con `try/catch`, o
bien declarada con `throws`. Si no, el programa no compila.

| # | Dónde | Causa | Solución aplicada |
|---|---|---|---|
| 1 | `Main.main`, al llamar a `banco.transferir(...)` | `Banco.transferir` declara `throws TransferenciaException`, que es comprobada | `try/catch` en `Main`, que es la capa que puede decidir qué hacer: informar y continuar |
| 2 | `GestorFicheros`, `new FileReader("cuentas.txt")` | El constructor de `FileReader` lanza `FileNotFoundException`, comprobada | El método declara `throws IOException` y propaga; `Main` la captura |
| 3 | `GestorFicheros`, `lector.close()` | `close()` lanza `IOException`, comprobada | `try-with-resources`: el cierre se hace solo y de forma segura |

El error 3 esconde además un fallo de diseño: aunque se hubiera rodeado el `close()` de un
`try/catch`, si la lectura fallase antes, **el fichero quedaría abierto**. Por eso la
solución correcta no es capturar, sino usar try-with-resources.

### B) Buenas prácticas aplicadas

- **`try-with-resources` para el fichero.** El recurso se declara en el paréntesis del `try`
  y Java garantiza su cierre al salir del bloque, haya excepción o no. Sustituye al patrón
  `try/finally` con un `close()` dentro y elimina la posibilidad de dejar un descriptor
  abierto.

- **La excepción se trata donde se puede decidir qué hacer.** `GestorDeFicheros` no sabe si
  ante un fichero que falta conviene avisar, reintentar o usar otro fichero, así que declara
  `throws IOException` y propaga. `Banco` tampoco sabe qué hacer si falta saldo, así que
  lanza `TransferenciaException`. Solo `Main`, que conoce el contexto, captura y decide:
  mostrar un mensaje comprensible y seguir con el resto del programa.

- **Ni un solo `catch` vacío ni un `printStackTrace` como forma de "manejar".** Cada `catch`
  hace algo útil: informar con un mensaje pensado para la persona usuaria.

- **Comprobada frente a no comprobada, con criterio.** `TransferenciaException` es
  comprobada porque representa una situación de negocio previsible y recuperable: quien
  llama debe plantearse qué hacer. En cambio, cuentas nulas, importes negativos o transferir
  una cuenta a sí misma son incumplimientos del contrato del método, es decir, errores de
  programación: para eso se usan `IllegalArgumentException` e `IllegalStateException`, que
  son no comprobadas y no deben capturarse en el flujo normal, sino corregirse en el código.

- **Encadenar la causa.** `TransferenciaException` gana un constructor
  `(String mensaje, Throwable causa)`. Al envolver una excepción de nivel más bajo se
  conserva la original, y la traza muestra el `Caused by:` con el punto real del fallo. Sin
  eso, se pierde la información que hace falta para depurar.

- **Cada regla, en un único sitio.** El original comprobaba el saldo **dos veces**: en
  `Banco.transferir` (`origen.getSaldo() < cantidad`) y en `Cuenta.retirar`
  (`cantidad > saldo`). Duplicar una regla es garantía de que algún día las dos versiones
  dejarán de coincidir. Ahora la regla vive en `Cuenta.puedeRetirar(cantidad)`, que es de
  quien es el saldo, y `Banco` la consulta antes de actuar. `Cuenta.retirar` mantiene su
  comprobación, pero ya solo como red de seguridad ante un uso incorrecto.

- **Preguntar antes de actuar, en vez de usar la excepción como estructura de control.**
  Que la falta de saldo sea una situación esperable no significa que haya que descubrirla
  atrapando una excepción: `puedeRetirar` permite comprobarlo con una condición normal.

- **Atomicidad de la transferencia.** En el original, si `destino.ingresar(cantidad)` fallara
  después de `origen.retirar(cantidad)`, el dinero habría salido de una cuenta sin llegar a
  la otra: **desaparecería**. Ahora el ingreso va dentro de un `try` y, si falla, se revierte
  la retirada antes de lanzar la excepción, de modo que las cuentas nunca quedan en un
  estado inconsistente.

- **Mensajes de excepción con datos.** `"Saldo insuficiente"` no ayuda a nadie.
  `"Saldo insuficiente en la cuenta de Ana: saldo 700.0, se intentó retirar 5000.0"` permite
  entender el problema sin abrir el depurador.

- **No se captura `Exception` ni `Throwable` de forma genérica**, que taparía errores de
  programación junto con los previstos.

- **Estado encapsulado e inmutable donde se puede.** `titular` es `final` y el constructor
  valida sus argumentos, de modo que no puede existir una `Cuenta` en estado inválido.

- Nota: el enunciado usa `double` para el saldo y se ha mantenido para no desviarse. En un
  sistema real se usaría `BigDecimal`, porque `double` arrastra errores de redondeo
  inaceptables cuando se trata de dinero.

### Salida real

```
Transferencia realizada: 300.0 de Ana a Luis
No se ha podido transferir: No hay saldo suficiente para transferir 5000.0 desde la cuenta de Ana (saldo actual: 700.0)
Saldo Ana: 700.0
Saldo Luis: 800.0
Fichero abierto: cuentas.txt (2 líneas)
  Ana;1000.0
  Luis;500.0
No se ha podido leer el fichero fichero-que-no-existe.txt: fichero-que-no-existe.txt (The system cannot find the file specified)
```

Las cuatro líneas finales demuestran los dos caminos del `GestorDeFicheros`: el fichero que
existe y el que no. El programa **no se rompe** en ninguno de los dos casos.

### Código

`src/banco/Main.java`

```java
package banco;

import java.io.IOException;
import java.util.List;

/**
 * Ejercicio 5: proyecto del enunciado con los errores de compilación corregidos
 * y las buenas prácticas aplicadas.
 *
 * Main es la capa que trata las excepciones, porque es la única que sabe qué
 * hacer con ellas: mostrar un mensaje comprensible y seguir con el resto del
 * programa. Las capas de abajo (Banco, Cuenta, GestorDeFicheros) se limitan a
 * lanzarlas o a propagarlas.
 */
public class Main {

	public static void main(String[] args) {

		Cuenta cuentaDeAna = new Cuenta("Ana", 1000);
		Cuenta cuentaDeLuis = new Cuenta("Luis", 500);

		Banco banco = new Banco();

		// Transferencia que sí se puede realizar.
		realizarTransferencia(banco, cuentaDeAna, cuentaDeLuis, 300);

		// Transferencia que no cabe en el saldo: demuestra el tratamiento de la
		// excepción comprobada TransferenciaException.
		realizarTransferencia(banco, cuentaDeAna, cuentaDeLuis, 5000);

		System.out.println("Saldo Ana: " + cuentaDeAna.getSaldo());
		System.out.println("Saldo Luis: " + cuentaDeLuis.getSaldo());

		GestorDeFicheros gestor = new GestorDeFicheros();

		// Fichero que existe y fichero que no: demuestra el tratamiento de la
		// excepción comprobada IOException.
		mostrarContenidoDelFichero(gestor, "cuentas.txt");
		mostrarContenidoDelFichero(gestor, "fichero-que-no-existe.txt");
	}

	private static void realizarTransferencia(Banco banco, Cuenta origen,
			Cuenta destino, double cantidad) {

		try {

			banco.transferir(origen, destino, cantidad);

			System.out.println("Transferencia realizada: " + cantidad + " de "
					+ origen.getTitular() + " a " + destino.getTitular());

		} catch (TransferenciaException excepcion) {

			// Aquí sí se puede decidir: se informa y el programa continúa.
			System.out.println("No se ha podido transferir: " + excepcion.getMessage());
		}
	}

	private static void mostrarContenidoDelFichero(GestorDeFicheros gestor,
			String nombreFichero) {

		try {

			List<String> lineas = gestor.leerLineas(nombreFichero);

			System.out.println("Fichero abierto: " + nombreFichero
					+ " (" + lineas.size() + " líneas)");

			for (String linea : lineas) {
				System.out.println("  " + linea);
			}

		} catch (IOException excepcion) {

			System.out.println("No se ha podido leer el fichero " + nombreFichero
					+ ": " + excepcion.getMessage());
		}
	}
}
```

`src/banco/Cuenta.java`

```java
package banco;

import java.util.Objects;

/**
 * Cuenta bancaria. Es la dueña de su propia invariante: el saldo nunca puede
 * quedar negativo. La regla de "¿se puede retirar esta cantidad?" vive aquí y
 * solo aquí; el resto de clases la consultan con puedeRetirar en lugar de
 * reimplementarla.
 *
 * Nota: el enunciado usa double para el saldo y se ha mantenido. En un sistema
 * real se usaría BigDecimal, porque double arrastra errores de redondeo que en
 * dinero son inaceptables.
 */
public class Cuenta {

	private final String titular;
	private double saldo;

	public Cuenta(String titular, double saldoInicial) {

		// Contrato de construcción: incumplirlo es un error de programación, así
		// que se señala con una excepción no comprobada.
		this.titular = Objects.requireNonNull(titular, "El titular no puede ser nulo");

		if (saldoInicial < 0) {
			throw new IllegalArgumentException(
					"El saldo inicial no puede ser negativo: " + saldoInicial);
		}

		this.saldo = saldoInicial;
	}

	/**
	 * Única definición de la regla de negocio "se puede retirar esta cantidad".
	 * Permite preguntar antes de actuar, en lugar de usar una excepción como si
	 * fuese una estructura de control.
	 */
	public boolean puedeRetirar(double cantidad) {
		return cantidad > 0 && cantidad <= saldo;
	}

	/**
	 * @throws IllegalArgumentException si la cantidad no es positiva
	 * @throws IllegalStateException    si no hay saldo suficiente; es una red de
	 *                                  seguridad: quien llama debería haberlo
	 *                                  comprobado antes con puedeRetirar
	 */
	public void retirar(double cantidad) {

		if (cantidad <= 0) {
			throw new IllegalArgumentException(
					"La cantidad debe ser mayor que 0, pero se recibió: " + cantidad);
		}

		if (cantidad > saldo) {
			throw new IllegalStateException(
					"Saldo insuficiente en la cuenta de " + titular
							+ ": saldo " + saldo + ", se intentó retirar " + cantidad);
		}

		saldo -= cantidad;
	}

	/**
	 * @throws IllegalArgumentException si la cantidad no es positiva
	 */
	public void ingresar(double cantidad) {

		if (cantidad <= 0) {
			throw new IllegalArgumentException(
					"La cantidad debe ser mayor que 0, pero se recibió: " + cantidad);
		}

		saldo += cantidad;
	}

	public double getSaldo() {
		return saldo;
	}

	public String getTitular() {
		return titular;
	}
}
```

`src/banco/Banco.java`

```java
package banco;

/**
 * Operaciones entre cuentas. No captura sus propias excepciones ni imprime nada
 * por pantalla: aquí no hay información suficiente para decidir qué hacer si la
 * transferencia falla, así que se propaga a quien sí puede decidirlo (Main).
 */
public class Banco {

	/**
	 * @throws IllegalArgumentException si las cuentas son nulas, son la misma o
	 *                                  la cantidad no es positiva: son errores de
	 *                                  programación en quien llama
	 * @throws TransferenciaException   si no hay saldo suficiente o si la
	 *                                  operación no se puede completar: situación
	 *                                  de negocio previsible y recuperable
	 */
	public void transferir(Cuenta origen, Cuenta destino, double cantidad)
			throws TransferenciaException {

		if (origen == null || destino == null) {
			throw new IllegalArgumentException("Las cuentas no pueden ser null");
		}

		if (origen == destino) {
			throw new IllegalArgumentException(
					"La cuenta de origen y la de destino no pueden ser la misma");
		}

		if (cantidad <= 0) {
			throw new IllegalArgumentException(
					"La cantidad debe ser positiva, pero se recibió: " + cantidad);
		}

		// Se pregunta a la cuenta, que es la dueña de la regla, en lugar de
		// repetir aquí la comparación con el saldo.
		if (!origen.puedeRetirar(cantidad)) {
			throw new TransferenciaException(
					"No hay saldo suficiente para transferir " + cantidad
							+ " desde la cuenta de " + origen.getTitular()
							+ " (saldo actual: " + origen.getSaldo() + ")");
		}

		origen.retirar(cantidad);

		try {
			destino.ingresar(cantidad);
		} catch (RuntimeException causa) {
			// Si el ingreso falla, el dinero ya ha salido de la cuenta de origen:
			// se deshace la retirada para que las cuentas no queden en un estado
			// inconsistente, y se informa encadenando la causa original.
			origen.ingresar(cantidad);
			throw new TransferenciaException(
					"No se pudo completar el ingreso en la cuenta de "
							+ destino.getTitular() + "; se ha revertido la retirada",
					causa);
		}
	}
}
```

`src/banco/TransferenciaException.java`

```java
package banco;

/**
 * Excepción comprobada (extiende Exception, no RuntimeException) porque
 * representa una situación de negocio previsible y recuperable: quien llama a
 * transferir puede y debe decidir qué hacer si no se puede realizar.
 *
 * Incluye el constructor con causa para no perder la excepción original cuando
 * se envuelve una excepción de nivel más bajo.
 */
public class TransferenciaException extends Exception {

	private static final long serialVersionUID = 1L;

	public TransferenciaException(String mensaje) {
		super(mensaje);
	}

	public TransferenciaException(String mensaje, Throwable causa) {
		super(mensaje, causa);
	}
}
```

`src/banco/GestorDeFicheros.java`

```java
package banco;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso al fichero de cuentas.
 *
 * El try-with-resources garantiza que el fichero se cierra aunque la lectura
 * falle. La IOException no se captura aquí: esta clase no sabe si conviene
 * avisar al usuario, reintentar o usar un fichero alternativo, así que la
 * declara y deja que decida la capa que llama.
 */
public class GestorDeFicheros {

	/**
	 * @throws IOException si el fichero no existe o no se puede leer
	 */
	public List<String> leerLineas(String nombreFichero) throws IOException {

		List<String> lineas = new ArrayList<>();

		try (BufferedReader lector = new BufferedReader(
				new FileReader(nombreFichero, StandardCharsets.UTF_8))) {

			String linea = lector.readLine();

			while (linea != null) {
				lineas.add(linea);
				linea = lector.readLine();
			}
		}

		return lineas;
	}
}
```
