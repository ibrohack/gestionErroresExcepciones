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
