package banco;

/**
 * Excepción comprobada (extiende Exception, no RuntimeException) porque
 * representa una situación de negocio previsible y recuperable: quien llama a
 * transferir puede y debe decidir qué hacer si no se puede realizar.
 *
 * Incluye el constructor con causa para no perder la excepción original cuando
 * se envuelve una excepción de nivel más bajo.
 */
public class TransferenciaException extends Exception {

	private static final long serialVersionUID = 1L;

	public TransferenciaException(String mensaje) {
		super(mensaje);
	}

	public TransferenciaException(String mensaje, Throwable causa) {
		super(mensaje, causa);
	}
}
