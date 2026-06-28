package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.OrganizacionVinculadaDAO;
import logica.dto.OrganizacionVinculadaDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaOrganizacionVinculadaDAO {

    private static final String ID_ORGANIZACION_PRUEBA = "ORG901";

    private static OrganizacionVinculadaDAO organizacionDAO;
    private OrganizacionVinculadaDTO organizacionValida;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        organizacionDAO = new OrganizacionVinculadaDAO();
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM organizacionvinculada WHERE idOrganizacion = '" + ID_ORGANIZACION_PRUEBA + "'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        organizacionValida = new OrganizacionVinculadaDTO(ID_ORGANIZACION_PRUEBA, "Organizacion Prueba", "Calle Falsa 123");
    }

    @Test
    public void pruebaAgregarOrganizacionVinculadaExitoso() throws Exception {
        boolean resultado = organizacionDAO.agregarOrganizacionVinculada(organizacionValida);
        assertTrue(resultado);
    }

    @Test
    public void pruebaActualizarOrganizacionVinculadaExitoso() throws Exception {
        organizacionDAO.agregarOrganizacionVinculada(organizacionValida);
        organizacionValida.setNombre("Organizacion Modificada");
        organizacionDAO.actualizarOrganizacionVinculada(organizacionValida);
        OrganizacionVinculadaDTO organizacionRecuperada = organizacionDAO.buscarOrganizacionVinculadaPorIdProyecto(ID_ORGANIZACION_PRUEBA);
        assertEquals("Organizacion Modificada", organizacionRecuperada.getNombre());
    }

    @Test
    public void pruebaBuscarOrganizacionVinculadaPorIdExitoso() throws Exception {
        organizacionDAO.agregarOrganizacionVinculada(organizacionValida);
        OrganizacionVinculadaDTO organizacionRecuperada = organizacionDAO.buscarOrganizacionVinculadaPorIdProyecto(ID_ORGANIZACION_PRUEBA);
        assertEquals(ID_ORGANIZACION_PRUEBA, organizacionRecuperada.getIdOrganizacion());
    }

    @Test
    public void pruebaListarOrganizacionesVinculadasExitoso() throws Exception {
        organizacionDAO.agregarOrganizacionVinculada(organizacionValida);
        List<OrganizacionVinculadaDTO> organizacionesRecuperadas = organizacionDAO.listarOrganizacionesVinculadas();
        assertFalse(organizacionesRecuperadas.isEmpty());
    }

    @Test
    public void pruebaBuscarOrganizacionVinculadaPorIdExcepcionNoEncontrado() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () ->
                organizacionDAO.buscarOrganizacionVinculadaPorIdProyecto("NOEXISTE"));
    }

    @Test
    public void pruebaAgregarOrganizacionVinculadaExcepcionNombreNulo() {
        organizacionValida.setNombre(null);
        assertThrows(DAOExcepcion.class, () ->
                organizacionDAO.agregarOrganizacionVinculada(organizacionValida));
    }
}