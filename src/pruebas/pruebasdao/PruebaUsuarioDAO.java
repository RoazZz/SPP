package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.UsuarioDAO;
import logica.dto.UsuarioDTO;
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

public class PruebaUsuarioDAO {

    private static final int ID_USUARIO_MAESTRO = 900;

    private static UsuarioDAO usuarioDAO;
    private UsuarioDTO usuarioValido;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        usuarioDAO = new UsuarioDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM usuario WHERE idUsuario = " + ID_USUARIO_MAESTRO);
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) "
                    + "VALUES (" + ID_USUARIO_MAESTRO + ", 'Maestro', 'Prueba', 'Base', 'clave123', 'ACTIVO', 'ADMIN')");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM usuario WHERE idUsuario != " + ID_USUARIO_MAESTRO + " AND Nombre = 'NuevoUsuario'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        usuarioValido = new UsuarioDTO(
                0,
                "NuevoUsuario",
                "Apellido",
                "Materno",
                "contrasenia123",
                TipoEstadoUsuario.ACTIVO,
                TipoDeUsuario.ADMIN
        );
    }

    @Test
    public void pruebaAgregarUsuarioExitoso() throws Exception {
        UsuarioDTO usuarioGuardado = usuarioDAO.agregarUsuario(usuarioValido);
        assertTrue(usuarioGuardado.getIdUsuario() > 0);
    }

    @Test
    public void pruebaActualizarUsuarioExitoso() throws Exception {
        UsuarioDTO usuarioGuardado = usuarioDAO.agregarUsuario(usuarioValido);
        usuarioGuardado.setNombre("NuevoUsuario");
        usuarioGuardado.setApellidoPaterno("Modificado");
        usuarioDAO.actualizarUsuario(usuarioGuardado);
        UsuarioDTO usuarioRecuperado = usuarioDAO.buscarUsuarioPorIdUsuario(usuarioGuardado.getIdUsuario());
        assertEquals("Modificado", usuarioRecuperado.getApellidoPaterno());
    }

    @Test
    public void pruebaBuscarUsuarioPorIdUsuarioExitoso() throws Exception {
        UsuarioDTO usuarioRecuperado = usuarioDAO.buscarUsuarioPorIdUsuario(ID_USUARIO_MAESTRO);
        assertEquals(ID_USUARIO_MAESTRO, usuarioRecuperado.getIdUsuario());
    }

    @Test
    public void pruebaListarUsuariosExitoso() throws Exception {
        List<UsuarioDTO> usuariosRecuperados = usuarioDAO.listarUsuarios();
        assertFalse(usuariosRecuperados.isEmpty());
    }

    @Test
    public void pruebaListarUsuariosPorTiposExitoso() throws Exception {
        List<UsuarioDTO> usuariosRecuperados = usuarioDAO.listarUsuariosPorTipos(List.of(TipoDeUsuario.ADMIN));
        assertFalse(usuariosRecuperados.isEmpty());
    }

    @Test
    public void pruebaBuscarUsuarioPorIdUsuarioExcepcionNoEncontrado() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () ->
                usuarioDAO.buscarUsuarioPorIdUsuario(-1));
    }

    @Test
    public void pruebaAgregarUsuarioExcepcionNombreNulo() {
        usuarioValido.setNombre(null);
        assertThrows(DAOExcepcion.class, () ->
                usuarioDAO.agregarUsuario(usuarioValido));
    }
}
