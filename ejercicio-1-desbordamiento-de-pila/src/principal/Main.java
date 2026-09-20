package principal;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {

	public static void main(String[] args) {

		// Manejador global de errores no capturados
		Thread.setDefaultUncaughtExceptionHandler(
				new Thread.UncaughtExceptionHandler() {

					@Override
					public void uncaughtException(Thread t, Throwable e) {

						try (PrintWriter log = new PrintWriter(
								new FileWriter("errores.log", true))) {

							log.println("================================");
							log.println("ERROR EN LA APLICACIÓN");
							log.println("Hilo: " + t.getName());
							log.println("Tipo: " + e.getClass().getName());
							log.println("Mensaje: " + e.getMessage());

							e.printStackTrace(log);

						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				});

		System.out.println("Aplicación iniciada");

		provocarStackOverflow();

		System.out.println("Aplicación terminada");
	}

	public static void provocarStackOverflow() {
		provocarStackOverflow();
	}
}
