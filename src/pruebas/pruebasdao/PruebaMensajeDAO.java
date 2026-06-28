package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.MensajeDAO;
import logica.dao.BuzonDAO;
import logica.dto.BuzonDTO;
import logica.dto.MensajeDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaMensajeDAO {

    private static final int ID_USUARIO_ORIGEN = 918;
    private static final int ID_USUARIO_DESTINO = 919;
    private static int idBuzonOrigen;
    private static int idBuzonDestino;

    private static MensajeDAO mensajeDAO;
    private MensajeDTO mensajeValido;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        mensajeDAO = new MensajeDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM usuario WHERE idUsuario IN (" + ID_USUARIO_ORIGEN + ", " + ID_USUARIO_DESTINO + ")");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (" + ID_USUARIO_ORIGEN + ", 'Origen', 'Ap', 'Am', 'clave', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (" + ID_USUARIO_DESTINO + ", 'Destino', 'Ap', 'Am', 'clave', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        BuzonDAO buzonDAO = new BuzonDAO();
        BuzonDTO buzonOrigen = new BuzonDTO(ID_USUARIO_ORIGEN);
        BuzonDTO buzonDestino = new BuzonDTO(ID_USUARIO_DESTINO);
        buzonDAO.agregarBuzon(buzonOrigen);
        buzonDAO.agregarBuzon(buzonDestino);
        idBuzonOrigen = buzonDAO.obtenerBuzonPorIdUsuario(ID_USUARIO_ORIGEN).getIdBuzon();
        idBuzonDestino = buzonDAO.obtenerBuzonPorIdUsuario(ID_USUARIO_DESTINO).getIdBuzon();
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM mensaje WHERE idBuzonDestino = " + idBuzonDestino);
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        mensajeValido = new MensajeDTO(idBuzonOrigen, idBuzonDestino, "Asunto de prueba", "Contenido del mensaje");
    }

    @Test
    public void pruebaInsertarMensajeExitoso() throws Exception {
        boolean resultado = mensajeDAO.insertarMensaje(mensajeValido);
        assertTrue(resultado);
    }

    @Test
    public void pruebaObtenerMensajesPorDestinatarioExitoso() throws Exception {
        mensajeDAO.insertarMensaje(mensajeValido);
        List<MensajeDTO> mensajesRecuperados = mensajeDAO.obtenerMensajesPorDestinatario(idBuzonDestino);
        assertFalse(mensajesRecuperados.isEmpty());
    }

    @Test
    public void pruebaObtenerMensajesConRemitenteExitoso() throws Exception {
        mensajeDAO.insertarMensaje(mensajeValido);
        List<MensajeDTO> mensajesRecuperados = mensajeDAO.obtenerMensajesConRemitente(idBuzonDestino);
        assertFalse(mensajesRecuperados.isEmpty());
    }

    @Test
    public void pruebaMarcarComoLeidoExcepcionNoEncontrado() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () ->
                mensajeDAO.marcarComoLeido(-1));
    }
}