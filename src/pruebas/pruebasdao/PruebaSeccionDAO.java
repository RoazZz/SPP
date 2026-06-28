package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.SeccionDAO;
import logica.dto.SeccionDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PruebaSeccionDAO {

    private static final int ID_SECCION_PRUEBA = 901;

    private static SeccionDAO seccionDAO;
    private SeccionDTO seccionValida;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        seccionDAO = new SeccionDAO();
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM seccion WHERE idSeccion = " + ID_SECCION_PRUEBA);
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        seccionValida = new SeccionDTO(ID_SECCION_PRUEBA, "Seccion de Prueba");
    }

    @Test
    public void pruebaAgregarSeccionExitoso() throws Exception {
        SeccionDTO seccionGuardada = seccionDAO.agregarSeccion(seccionValida);
        assertEquals(ID_SECCION_PRUEBA, seccionGuardada.getIdSeccion());
    }

    @Test
    public void pruebaActualizarSeccionExitoso() throws Exception {
        seccionDAO.agregarSeccion(seccionValida);
        seccionValida.setNombre("Seccion Modificada");
        seccionDAO.actualizarSeccion(seccionValida);
        SeccionDTO seccionRecuperada = seccionDAO.obtenerSeccionPorId(ID_SECCION_PRUEBA);
        assertEquals("Seccion Modificada", seccionRecuperada.getNombre());
    }

    @Test
    public void pruebaObtenerSeccionPorIdExitoso() throws Exception {
        seccionDAO.agregarSeccion(seccionValida);
        SeccionDTO seccionRecuperada = seccionDAO.obtenerSeccionPorId(ID_SECCION_PRUEBA);
        assertEquals(ID_SECCION_PRUEBA, seccionRecuperada.getIdSeccion());
    }

    @Test
    public void pruebaObtenerTodasLasSeccionesExitoso() throws Exception {
        seccionDAO.agregarSeccion(seccionValida);
        List<SeccionDTO> seccionesRecuperadas = seccionDAO.obtenerTodasLasSecciones();
        assertFalse(seccionesRecuperadas.isEmpty());
    }

    @Test
    public void pruebaObtenerSeccionPorIdExcepcionNoEncontrado() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () ->
                seccionDAO.obtenerSeccionPorId(-1));
    }

    @Test
    public void pruebaAgregarSeccionExcepcionNombreNulo() {
        seccionValida.setNombre(null);
        assertThrows(DAOExcepcion.class, () ->
                seccionDAO.agregarSeccion(seccionValida));
    }
}