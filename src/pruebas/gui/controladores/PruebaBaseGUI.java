package gui.controladores;

import accesodatos.ConexionBD;
import org.junit.jupiter.api.BeforeAll;
import org.testfx.framework.junit5.ApplicationTest;

/**
 * Clase base para pruebas de interfaz con TestFX.
 * Configura la base de datos de pruebas antes de arrancar cualquier GUI.
 */
public abstract class PruebaBaseGUI extends ApplicationTest {

    @BeforeAll
    public static void configurarBaseDeDatosDePrueba() {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
    }

    protected void pausarMilisegundos(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}