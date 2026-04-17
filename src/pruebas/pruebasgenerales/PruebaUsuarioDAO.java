package pruebasgenerales;

import accesodatos.ConexionBD;
import logica.dao.UsuarioDAO;
import logica.dto.UsuarioDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PruebaUsuarioDAO {

    @BeforeAll
    static void configurarConexion() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        System.out.println("Conexión reiniciada");
    }

    @BeforeEach
    void limpiarAntes() throws Exception {
        limpiarTablas();
    }

    @AfterEach
    void limpiarDespues() throws Exception {
        limpiarTablas();
    }

    private void limpiarTablas() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0");
        conexion.createStatement().execute("TRUNCATE TABLE Usuario");
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");
    }


    private UsuarioDTO crearUsuarioEjemplo() {
        return new UsuarioDTO(
                0,
                "Jared",
                "Morales",
                "Tirado",
                "123",
                TipoEstado.ACTIVO,
                TipoDeUsuario.PRACTICANTE
        );
    }

    @Test
    public void pruebaAgregarUsuario() throws Exception {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioDTO usuarioDTO = crearUsuarioEjemplo();

        usuarioDAO.agregarUsuario(usuarioDTO);

        assertTrue(usuarioDTO.getIdUsuario() > 0,
                "El id generado debe ser mayor a 0");
    }

    @Test
    public void pruebaBuscarUsuarioPorId() throws Exception {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioDTO usuarioDTO = crearUsuarioEjemplo();

        usuarioDAO.agregarUsuario(usuarioDTO);

        UsuarioDTO resultado = usuarioDAO.buscarUsuarioPorIdUsuario(usuarioDTO.getIdUsuario());

        assertEquals("Jared", resultado.getNombre());
    }

    @Test
    public void pruebaActualizarUsuario() throws Exception {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioDTO usuarioDTO = crearUsuarioEjemplo();
        usuarioDAO.agregarUsuario(usuarioDTO);

        usuarioDTO.setNombre("Jesús");
        usuarioDAO.actualizarUsuario(usuarioDTO);

        UsuarioDTO resultado = usuarioDAO.buscarUsuarioPorIdUsuario(usuarioDTO.getIdUsuario());

        assertEquals("Jesús", resultado.getNombre());
    }

    @Test
    public void pruebaListarUsuarios() throws Exception {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        usuarioDAO.agregarUsuario(crearUsuarioEjemplo());

        List<UsuarioDTO> lista = usuarioDAO.listarUsuarios();

        assertFalse(lista.isEmpty());
    }
}
