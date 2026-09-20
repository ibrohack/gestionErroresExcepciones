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
