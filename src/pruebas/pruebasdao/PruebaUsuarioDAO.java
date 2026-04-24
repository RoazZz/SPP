package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.UsuarioDAO;
import logica.dto.UsuarioDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PruebaUsuarioDAO {

    private static UsuarioDAO usuarioDAO;
    private UsuarioDTO usuarioValido;
    private UsuarioDTO usuarioSinNombre;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        usuarioDAO = new UsuarioDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE Usuario");
            statement.execute("INSERT INTO Usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                    "VALUES (999, 'Usuario', 'Maestro', 'Test', '123', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM Usuario WHERE idUsuario != 999");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        usuarioValido = new UsuarioDTO(
                0,
                "Jared",
                "Morales",
                "Tirado",
                "123",
                TipoEstado.ACTIVO,
                TipoDeUsuario.PRACTICANTE
        );
        usuarioSinNombre = new UsuarioDTO(
                0,
                null,
                "Morales",
                "Tirado",
                "123",
                TipoEstado.ACTIVO,
                TipoDeUsuario.PRACTICANTE
        );
    }

    @Test
    public void pruebaAgregarUsuarioExitoso() throws Exception {
        usuarioDAO.agregarUsuario(usuarioValido);
        assertTrue(usuarioValido.getIdUsuario() > 0);
    }

    @Test
    public void pruebaBuscarUsuarioNoExistente() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> usuarioDAO.buscarUsuarioPorIdUsuario(10));
    }

    @Test
    public void pruebaAgregarUsuarioExcepcionNombreNulo() {
        assertThrows(DAOExcepcion.class, () -> usuarioDAO.agregarUsuario(usuarioSinNombre));
    }

}
