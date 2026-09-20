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
