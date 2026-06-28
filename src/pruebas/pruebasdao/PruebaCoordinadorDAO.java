package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.CoordinadorDAO;
import logica.dto.CoordinadorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstadoUsuario;
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

public class PruebaCoordinadorDAO {

    private static final String NUMERO_PERSONAL_PRUEBA = "COORD903";

    private static CoordinadorDAO coordinadorDAO;
    private CoordinadorDTO coordinadorValido;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        coordinadorDAO = new CoordinadorDAO();
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM coordinador WHERE NumeroDePersonal = '" + NUMERO_PERSONAL_PRUEBA + "'");
            statement.execute("DELETE FROM usuario WHERE Nombre = 'CoordinadorPrueba'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        coordinadorValido = new CoordinadorDTO(
                0,
                "CoordinadorPrueba",
                "Apellido",
                "Materno",
                "clave12345",
                TipoEstadoUsuario.ACTIVO,
                TipoDeUsuario.COORDINADOR,
                NUMERO_PERSONAL_PRUEBA
        );
    }

    @Test
    public void pruebaAgregarCoordinadorExitoso() throws Exception {
        coordinadorDAO.agregarCoordinador(coordinadorValido);
        assertTrue(coordinadorValido.getIdUsuario() > 0);
    }

    @Test
    public void pruebaActualizarCoordinadorExitoso() throws Exception {
        coordinadorDAO.agregarCoordinador(coordinadorValido);
        coordinadorValido.setNombre("CoordinadorPrueba");
        coordinadorValido.setApellidoPaterno("Modificado");
        coordinadorDAO.actualizarCoordinador(coordinadorValido);
        CoordinadorDTO coordinadorRecuperado = coordinadorDAO.buscarCoordinadorPorNumeroDePersonal(NUMERO_PERSONAL_PRUEBA);
        assertEquals("Modificado", coordinadorRecuperado.getApellidoPaterno());
    }

    @Test
    public void pruebaBuscarCoordinadorPorNumeroDePersonalExitoso() throws Exception {
        coordinadorDAO.agregarCoordinador(coordinadorValido);
        CoordinadorDTO coordinadorRecuperado = coordinadorDAO.buscarCoordinadorPorNumeroDePersonal(NUMERO_PERSONAL_PRUEBA);
        assertEquals(NUMERO_PERSONAL_PRUEBA, coordinadorRecuperado.getNumeroPersonal());
    }

    @Test
    public void pruebaListarCoordinadorExitoso() throws Exception {
        coordinadorDAO.agregarCoordinador(coordinadorValido);
        List<CoordinadorDTO> coordinadoresRecuperados = coordinadorDAO.listarCoordinador();
        assertFalse(coordinadoresRecuperados.isEmpty());
    }

    @Test
    public void pruebaExisteCoordinadorConNumeroPersonalExitoso() throws Exception {
        coordinadorDAO.agregarCoordinador(coordinadorValido);
        boolean existe = coordinadorDAO.existeCoordinadorConNumeroPersonal(NUMERO_PERSONAL_PRUEBA, 0);
        assertTrue(existe);
    }

    @Test
    public void pruebaBuscarCoordinadorPorNumeroDePersonalExcepcionNoEncontrado() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () ->
                coordinadorDAO.buscarCoordinadorPorNumeroDePersonal("NOEXISTE"));
    }

    @Test
    public void pruebaAgregarCoordinadorExcepcionNombreNulo() {
        coordinadorValido.setNombre(null);
        assertThrows(DAOExcepcion.class, () ->
                coordinadorDAO.agregarCoordinador(coordinadorValido));
    }
}