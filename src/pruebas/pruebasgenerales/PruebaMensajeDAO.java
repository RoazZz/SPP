package pruebasgenerales;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.MensajeDAO;
import logica.dto.MensajeDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaMensajeDAO {
    private static MensajeDAO mensajeDAO;
    private MensajeDTO dtoParaAgregar;
    private MensajeDTO dtoInvalido;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        mensajeDAO = new MensajeDAO();

        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE mensaje");

            statement.execute("INSERT INTO mensaje (idMensaje, Remitente, Destinatario, Asunto, Contenido, Fecha) " +
                    "VALUES (999, 'maestro@test.com', 'alumno@test.com', 'Asunto Maestro', 'Contenido Maestro', '2026-04-20 10:00:00')");

            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM mensaje WHERE idMensaje != 999");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }

        dtoParaAgregar = new MensajeDTO(0, "rem@test.com", "des@test.com", "Test", "Nuevo Contenido", LocalDateTime.now());
        dtoInvalido = new MensajeDTO(0, null, "des@test.com", "Error", "Contenido", LocalDateTime.now());
    }

    @Test
    public void pruebaInsertarMensajeExitoso() throws Exception {
        MensajeDTO resultado = mensajeDAO.insertarMensaje(dtoParaAgregar);
        assertNotNull(resultado);
    }

    @Test
    public void pruebaObtenerMensajeExitoso() throws Exception {
        String contenidoRecuperado = mensajeDAO.obtenerMensaje("999");
        assertEquals("Contenido Maestro", contenidoRecuperado);
    }

    @Test
    public void pruebaObtenerMensajesPorDestinatarioExitoso() throws Exception {
        List<MensajeDTO> lista = mensajeDAO.obtenerMensajesPorDestinatario("alumno@test.com");
        assertFalse(lista.isEmpty());
    }

    @Test
    public void pruebaInsertarMensajeExcepcionRemitenteNulo() {
        assertThrows(DAOExcepcion.class, () -> mensajeDAO.insertarMensaje(dtoInvalido));
    }

    @Test
    public void pruebaActualizarMensajeExcepcionConexionCerrada() throws Exception {
        ConexionBD.obtenerInstancia().obtenerConexion().close();
        assertThrows(DAOExcepcion.class, () -> mensajeDAO.actualizarMensaje(dtoParaAgregar));
        ConexionBD.reset();
        mensajeDAO = new MensajeDAO();
    }
}