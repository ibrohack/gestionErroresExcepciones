package banco;

import java.util.Objects;

/**
 * Cuenta bancaria. Es la dueña de su propia invariante: el saldo nunca puede
 * quedar negativo. La regla de "¿se puede retirar esta cantidad?" vive aquí y
 * solo aquí; el resto de clases la consultan con puedeRetirar en lugar de
 * reimplementarla.
 *
 * Nota: el enunciado usa double para el saldo y se ha mantenido. En un sistema
 * real se usaría BigDecimal, porque double arrastra errores de redondeo que en
 * dinero son inaceptables.
 */
public class Cuenta {

	private final String titular;
	private double saldo;

	public Cuenta(String titular, double saldoInicial) {

		// Contrato de construcción: incumplirlo es un error de programación, así
		// que se señala con una excepción no comprobada.
		this.titular = Objects.requireNonNull(titular, "El titular no puede ser nulo");

		if (saldoInicial < 0) {
			throw new IllegalArgumentException(
					"El saldo inicial no puede ser negativo: " + saldoInicial);
		}

		this.saldo = saldoInicial;
	}

	/**
	 * Única definición de la regla de negocio "se puede retirar esta cantidad".
	 * Permite preguntar antes de actuar, en lugar de usar una excepción como si
	 * fuese una estructura de control.
	 */
	public boolean puedeRetirar(double cantidad) {
		return cantidad > 0 && cantidad <= saldo;
	}

	/**
	 * @throws IllegalArgumentException si la cantidad no es positiva
	 * @throws IllegalStateException    si no hay saldo suficiente; es una red de
	 *                                  seguridad: quien llama debería haberlo
	 *                                  comprobado antes con puedeRetirar
	 */
	public void retirar(double cantidad) {

		if (cantidad <= 0) {
			throw new IllegalArgumentException(
					"La cantidad debe ser mayor que 0, pero se recibió: " + cantidad);
		}

		if (cantidad > saldo) {
			throw new IllegalStateException(
					"Saldo insuficiente en la cuenta de " + titular
							+ ": saldo " + saldo + ", se intentó retirar " + cantidad);
		}

		saldo -= cantidad;
	}

	/**
	 * @throws IllegalArgumentException si la cantidad no es positiva
	 */
	public void ingresar(double cantidad) {

		if (cantidad <= 0) {
			throw new IllegalArgumentException(
					"La cantidad debe ser mayor que 0, pero se recibió: " + cantidad);
		}

		saldo += cantidad;
	}

	public double getSaldo() {
		return saldo;
	}

	public String getTitular() {
		return titular;
	}
}
