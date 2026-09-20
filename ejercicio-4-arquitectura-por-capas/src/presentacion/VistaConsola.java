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
