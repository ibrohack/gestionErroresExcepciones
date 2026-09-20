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
