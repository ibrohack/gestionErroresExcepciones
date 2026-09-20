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
