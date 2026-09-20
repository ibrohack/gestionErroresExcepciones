package negocio;

/**
 * Capa de negocio: la regla de cálculo, sin entrada ni salida por consola y sin
 * saber nada de las capas que la usan. Si la operación no se puede realizar,
 * deja que la excepción suba a quien pueda decidir qué hacer con ella.
 */
public class Calculadora {

	/**
	 * @throws ArithmeticException si el divisor es cero (la lanza la propia JVM)
	 */
	public int dividir(int dividendo, int divisor) {
		return dividendo / divisor;
	}
}
