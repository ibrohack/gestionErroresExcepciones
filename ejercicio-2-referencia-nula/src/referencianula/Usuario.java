package referencianula;

/**
 * Objeto de dominio sencillo. El atributo correoElectronico puede quedar a null
 * cuando se construye un usuario que todavía no lo ha facilitado: ese null es el
 * que provoca la NullPointerException "encubierta" del segundo escenario.
 */
public class Usuario {

	private final String nombre;
	private final String correoElectronico;

	public Usuario(String nombre, String correoElectronico) {
		this.nombre = nombre;
		this.correoElectronico = correoElectronico;
	}

	public String getNombre() {
		return nombre;
	}

	public String getCorreoElectronico() {
		return correoElectronico;
	}
}
