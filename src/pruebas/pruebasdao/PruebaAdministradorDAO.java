package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.AdministradorDAO;
import logica.dto.AdministradorDTO;
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

public class PruebaAdministradorDAO {

    private static AdministradorDAO administradorDAO;
    private AdministradorDTO administradorValido;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        administradorDAO = new AdministradorDAO();
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM administrador");
            statement.execute("DELETE FROM usuario WHERE TipoUsuario = 'ADMIN'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        administradorValido = new AdministradorDTO(
                0,
                "Administrador",
                "Sistema",
                "Principal",
                "clave12345",
                TipoEstadoUsuario.ACTIVO,
                TipoDeUsuario.ADMIN,
                0
        );
    }

    @Test
    public void pruebaAgregarAdministradorExitoso() throws Exception {
        AdministradorDTO administradorGuardado = administradorDAO.agregarAdministrador(administradorValido);
        assertTrue(administradorGuardado.getIdUsuario() > 0);
    }

    @Test
    public void pruebaListarAdministradoresExitoso() throws Exception {
        administradorDAO.agregarAdministrador(administradorValido);
        List<AdministradorDTO> administradoresRecuperados = administradorDAO.listarAdministradores();
        assertFalse(administradoresRecuperados.isEmpty());
    }

    @Test
    public void pruebaExisteAlgunAdministradorExitoso() throws Exception {
        administradorDAO.agregarAdministrador(administradorValido);
        boolean existe = administradorDAO.existeAlgunAdministrador();
        assertTrue(existe);
    }

    @Test
    public void pruebaBuscarAdministradorPorNombreExitoso() throws Exception {
        administradorDAO.agregarAdministrador(administradorValido);
        AdministradorDTO administradorRecuperado = administradorDAO.buscarAdministradorPorNombre("Administrador");
        assertEquals("Administrador", administradorRecuperado.getNombre());
    }

    @Test
    public void pruebaBuscarAdministradorPorIdExcepcionNoEncontrado() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () ->
                administradorDAO.buscarAdministradorPorId(-1));
    }

    @Test
    public void pruebaBuscarAdministradorPorNombreExcepcionNoEncontrado() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () ->
                administradorDAO.buscarAdministradorPorNombre("NoExiste"));
    }

    @Test
    public void pruebaAgregarAdministradorExcepcionNombreNulo() {
        administradorValido.setNombre(null);
        assertThrows(DAOExcepcion.class, () ->
                administradorDAO.agregarAdministrador(administradorValido));
    }
}