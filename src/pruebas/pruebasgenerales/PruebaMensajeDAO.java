package pruebasgenerales;

import accesodatos.ConexionBD;
import logica.dao.MensajeDAO;
import logica.dto.MensajeDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaMensajeDAO {

    @BeforeAll
    static void configurarConexion() {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
    }

    @BeforeEach
    void limpiarAntes() throws Exception {
        limpiarTablas();
        System.out.println("Limpieza ANTES de prueba");
    }

    @AfterEach
    void limpiarDespues() throws Exception {
        limpiarTablas();
        System.out.println("Limpieza DESPUÉS de prueba (aunque falle)");
    }

    void limpiarTablas() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement comandoControl = conexion.createStatement()) {
            comandoControl.execute("SET FOREIGN_KEY_CHECKS = 0");
            comandoControl.execute("TRUNCATE TABLE mensaje");
            comandoControl.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @Test
    public void pruebaInsertarObtenerMensaje() throws Exception {
        MensajeDAO mensajeDAO = new MensajeDAO();
        MensajeDTO mensajeDTO = new MensajeDTO(
                0,
                "remitente@correo.com",
                "destinatario@correo.com",
                "Asunto de Prueba",
                "Contenido del mensaje de prueba",
                LocalDateTime.now()
        );

        mensajeDAO.insertarMensaje(mensajeDTO);

        String contenidoRecuperado = mensajeDAO.obtenerMensaje(String.valueOf(mensajeDTO.getIdMensaje()));

        assertEquals(mensajeDTO.getContenido(), contenidoRecuperado,
                "El contenido recuperado debe ser idéntico al insertado");
    }

    @Test
    public void pruebaActualizarObtenerMensaje() throws Exception {
        MensajeDAO mensajeDAO = new MensajeDAO();
        MensajeDTO mensajeDTO = new MensajeDTO(0, "rem@test.com", "des@test.com", "Test", "Original", LocalDateTime.now());
        mensajeDAO.insertarMensaje(mensajeDTO);

        String nuevoContenido = "Contenido Actualizado";
        mensajeDTO.setContenido(nuevoContenido);
        mensajeDAO.actualizarMensaje(mensajeDTO);

        String contenidoRecuperado = mensajeDAO.obtenerMensaje(String.valueOf(mensajeDTO.getIdMensaje()));

        assertEquals(nuevoContenido, contenidoRecuperado,
                "El contenido del mensaje debe haberse actualizado en la base de datos");
    }

    @Test
    public void pruebaObtenerMensajesPorDestinatario() throws Exception {
        MensajeDAO mensajeDAO = new MensajeDAO();
        String destinatario = "alumno@universidad.com";

        mensajeDAO.insertarMensaje(new MensajeDTO(0, "profesor@test.com", destinatario, "Aviso 1", "Hola 1", LocalDateTime.now()));
        mensajeDAO.insertarMensaje(new MensajeDTO(0, "admin@test.com", destinatario, "Aviso 2", "Hola 2", LocalDateTime.now()));

        List<MensajeDTO> bandejaEntrada = mensajeDAO.obtenerMensajesPorDestinatario(destinatario);

        assertEquals(2, bandejaEntrada.size(), "Deberían encontrarse 2 mensajes para el destinatario especificado");
    }
}