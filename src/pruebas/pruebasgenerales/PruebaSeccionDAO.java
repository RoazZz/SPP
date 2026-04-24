package pruebasgenerales;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.ProfesorDAO;
import logica.dao.SeccionDAO;
import logica.dto.SeccionDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaSeccionDAO {
    private static SeccionDAO seccionDAO;
    private SeccionDTO seccionValida;
    private SeccionDTO seccionInvalidaNombreNulo;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        seccionDAO = new SeccionDAO();

        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE seccion");

            statement.execute("INSERT INTO seccion (idSeccion, Nombre) " +
                    "VALUES (999, 'Seccion Maestra')");

            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM seccion WHERE idSeccion != 999");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }

        seccionValida = new SeccionDTO(10, "Ingeniería de Software");
        seccionInvalidaNombreNulo = new SeccionDTO(0, null);
    }

    @AfterEach
    void restaurarRecursos() {
        ConexionBD.reset();
        try {
            seccionDAO = new SeccionDAO();
        } catch (Exception e) {
            System.err.println("Error al restaurar el DAO: " + e.getMessage());
        }
    }

    @Test
    public void pruebaAgregarSeccionExitoso() throws Exception {
        SeccionDTO resultado = seccionDAO.agregarSeccion(seccionValida);
        assertNotNull(resultado);
    }

    @Test
    public void pruebaObtenerSeccionPorIdExitoso() throws Exception {
        SeccionDTO recuperada = seccionDAO.obtenerSeccionPorId(999);
        assertEquals("Seccion Maestra", recuperada.getNombre());
    }

    @Test
    public void pruebaObtenerTodasLasSeccionesExitoso() throws Exception {
        List<SeccionDTO> listaSecciones = seccionDAO.obtenerTodasLasSecciones();
        assertFalse(listaSecciones.isEmpty());
    }

    @Test
    public void pruebaAgregarSeccionExcepcionNombreNulo() {
        assertThrows(DAOExcepcion.class, () -> seccionDAO.agregarSeccion(seccionInvalidaNombreNulo));
    }

    @Test
    public void pruebaActualizarSeccionExcepcionConexionCerrada() throws Exception {
        ConexionBD.obtenerInstancia().obtenerConexion().close();
        assertThrows(DAOExcepcion.class, () -> seccionDAO.actualizarSeccion(seccionValida));
    }
}