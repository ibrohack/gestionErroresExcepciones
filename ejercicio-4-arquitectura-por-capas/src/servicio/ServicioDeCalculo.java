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
