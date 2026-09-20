package banco;

/**
 * Operaciones entre cuentas. No captura sus propias excepciones ni imprime nada
 * por pantalla: aquí no hay información suficiente para decidir qué hacer si la
 * transferencia falla, así que se propaga a quien sí puede decidirlo (Main).
 */
public class Banco {

	/**
	 * @throws IllegalArgumentException si las cuentas son nulas, son la misma o
	 *                                  la cantidad no es positiva: son errores de
	 *                                  programación en quien llama
	 * @throws TransferenciaException   si no hay saldo suficiente o si la
	 *                                  operación no se puede completar: situación
	 *                                  de negocio previsible y recuperable
	 */
	public void transferir(Cuenta origen, Cuenta destino, double cantidad)
			throws TransferenciaException {

		if (origen == null || destino == null) {
			throw new IllegalArgumentException("Las cuentas no pueden ser null");
		}

		if (origen == destino) {
			throw new IllegalArgumentException(
					"La cuenta de origen y la de destino no pueden ser la misma");
		}

		if (cantidad <= 0) {
			throw new IllegalArgumentException(
					"La cantidad debe ser positiva, pero se recibió: " + cantidad);
		}

		// Se pregunta a la cuenta, que es la dueña de la regla, en lugar de
		// repetir aquí la comparación con el saldo.
		if (!origen.puedeRetirar(cantidad)) {
			throw new TransferenciaException(
					"No hay saldo suficiente para transferir " + cantidad
							+ " desde la cuenta de " + origen.getTitular()
							+ " (saldo actual: " + origen.getSaldo() + ")");
		}

		origen.retirar(cantidad);

		try {
			destino.ingresar(cantidad);
		} catch (RuntimeException causa) {
			// Si el ingreso falla, el dinero ya ha salido de la cuenta de origen:
			// se deshace la retirada para que las cuentas no queden en un estado
			// inconsistente, y se informa encadenando la causa original.
			origen.ingresar(cantidad);
			throw new TransferenciaException(
					"No se pudo completar el ingreso en la cuenta de "
							+ destino.getTitular() + "; se ha revertido la retirada",
					causa);
		}
	}
}
