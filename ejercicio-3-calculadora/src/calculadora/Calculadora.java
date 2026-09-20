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
