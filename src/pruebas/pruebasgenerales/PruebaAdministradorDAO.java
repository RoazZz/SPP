package pruebasgenerales;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.AdministradorDAO;
import logica.dto.AdministradorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaAdministradorDAO {
    private static AdministradorDAO administradorDAO;
    private AdministradorDTO administradorValido;
    private AdministradorDTO administradorInvalidoNombreNulo;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        administradorDAO = new AdministradorDAO();

        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE administrador");
            statement.execute("TRUNCATE TABLE usuario");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");

            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                    "VALUES (999, 'AdminGlobal', 'Test', 'Admin', '123', 'ACTIVO', 'ADMIN')");
            statement.execute("INSERT INTO administrador (idAdministrador, idUsuario) VALUES (999, 999)");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiarTablas() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM administrador WHERE idAdministrador != 999");
            statement.execute("DELETE FROM usuario WHERE idUsuario != 999");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }

        administradorValido = new AdministradorDTO(0, "Ignacio", "Calixto", "León", "pass123", TipoEstado.ACTIVO, TipoDeUsuario.ADMIN, 0);
        administradorInvalidoNombreNulo = new AdministradorDTO(0, null, "Error", "M", "123", TipoEstado.ACTIVO, TipoDeUsuario.ADMIN, 0);
    }

    @Test
    public void pruebaAgregarAdministradorExitoso() throws Exception {
        AdministradorDTO resultado = administradorDAO.agregarAdministrador(administradorValido);
        assertTrue(resultado.getIdAdministrador() > 0);
    }

    @Test
    public void pruebaAgregarAdministradorErrorNombreNulo() {
        assertThrows(DAOExcepcion.class, () -> administradorDAO.agregarAdministrador(administradorInvalidoNombreNulo));
    }

    @Test
    public void pruebaBuscarAdministradorPorIdExitoso() throws Exception {
        AdministradorDTO encontrado = administradorDAO.buscarAdministradorPorId(999);
        assertEquals("AdminGlobal", encontrado.getNombre());
    }

    @Test
    public void pruebaBuscarAdministradorErrorIdNoExistente() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> administradorDAO.buscarAdministradorPorId(404));
    }

    @Test
    public void pruebaBuscarAdministradorPorIdExcepcionConexionCerrada() throws Exception {
        ConexionBD.obtenerInstancia().obtenerConexion().close();
        assertThrows(DAOExcepcion.class, () -> administradorDAO.buscarAdministradorPorId(999));
        ConexionBD.reset();
        administradorDAO = new AdministradorDAO();
    }
}

