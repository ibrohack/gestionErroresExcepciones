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
