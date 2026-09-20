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
