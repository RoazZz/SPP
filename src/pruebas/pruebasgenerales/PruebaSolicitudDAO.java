package pruebasgenerales;

import accesodatos.ConexionBD;
import logica.dao.SolicitudProyectoDAO;
import logica.dto.SolicitaProyectoDTO;
import logica.enums.TipoEstadoSolicitud;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class PruebaSolicitudDAO {

    @BeforeAll
    static void configurarConexion() {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
    }

    @BeforeEach
    void prepararEntorno() throws Exception {
        limpiarTablas();
        insertarDatosNecesarios();
    }

    @AfterEach
    void finalizarPrueba() throws Exception {
        limpiarTablas();
    }

    void limpiarTablas() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement comandoControl = conexion.createStatement()) {
            comandoControl.execute("SET FOREIGN_KEY_CHECKS = 0");
            comandoControl.execute("TRUNCATE TABLE solicita");
            comandoControl.execute("TRUNCATE TABLE proyecto");
            comandoControl.execute("TRUNCATE TABLE profesor");
            comandoControl.execute("TRUNCATE TABLE organizacionvinculada");
            comandoControl.execute("TRUNCATE TABLE practicante");
            comandoControl.execute("TRUNCATE TABLE usuario");
            comandoControl.execute("TRUNCATE TABLE seccion");
            comandoControl.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    private void insertarDatosNecesarios() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();

        // 1. Alumno
        ejecutarSentencia(conexion, "INSERT INTO seccion VALUES (1, 'Sistemas')");
        ejecutarSentencia(conexion, "INSERT INTO usuario VALUES (1, 'Pepe', 'Grillo', 'L', 'pass', 'ACTIVO', 'PRACTICANTE')");
        ejecutarSentencia(conexion, "INSERT INTO practicante VALUES ('S123', 1, '7', 'MASCULINO', 21, 0, 1)");

        // 2. Profesor y Proyecto
        ejecutarSentencia(conexion, "INSERT INTO organizacionvinculada VALUES ('ORG01', 'UV', 'Calle 1')");
        ejecutarSentencia(conexion, "INSERT INTO usuario VALUES (2, 'Dra', 'Z', 'X', 'pass', 'ACTIVO', 'PROFESOR')");
        ejecutarSentencia(conexion, "INSERT INTO profesor VALUES ('999', 'MIXTO', 2)");
        ejecutarSentencia(conexion, "INSERT INTO proyecto (idProyecto, idOrganizacion, NumeroDePersonal, Nombre, Descripcion) " +
                "VALUES (50, 'ORG01', '999', 'Proyecto Prueba', 'Desc')");
    }

    private void ejecutarSentencia(Connection conexion, String sql) throws Exception {
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.executeUpdate();
        }
    }

    @Test
    public void pruebaInsertarObtenerSolicitudPorMatricula() throws Exception {
        SolicitudProyectoDAO solicitudDAO = new SolicitudProyectoDAO();
        SolicitaProyectoDTO solicitudDTO = new SolicitaProyectoDTO("S123", 50, TipoEstadoSolicitud.PENDIENTE, "FEB-JUN 2026");

        solicitudDAO.insertarSolicitudProyecto(solicitudDTO);
        List<SolicitaProyectoDTO> lista = solicitudDAO.obtenerSolicitudesProyectoPorMatricula("S123");

        assertEquals(50, lista.get(0).getIdProyecto(), "El ID del proyecto solicitado debe coincidir");
    }

    @Test
    public void pruebaActualizarObtenerSolicitudPorIdProyecto() throws Exception {
        SolicitudProyectoDAO solicitudDAO = new SolicitudProyectoDAO();
        SolicitaProyectoDTO solicitudDTO = new SolicitaProyectoDTO("S123", 50, TipoEstadoSolicitud.PENDIENTE, "2026");
        solicitudDAO.insertarSolicitudProyecto(solicitudDTO);

        // Cambiamos estado
        solicitudDTO.setEstadoProyecto(TipoEstadoSolicitud.ACEPTADO);
        solicitudDAO.actualizarSolicitudProyecto(solicitudDTO);

        List<SolicitaProyectoDTO> lista = solicitudDAO.obtenerSolicitudesProyectoPorIdProyecto(50);
        assertEquals(TipoEstadoSolicitud.ACEPTADO, lista.get(0).getEstadoProyecto(), "El estado debe haber cambiado a ACEPTADO");
    }

    @Test
    public void pruebaObtenerSolicitudesPorPeriodo() throws Exception {
        SolicitudProyectoDAO solicitudDAO = new SolicitudProyectoDAO();
        solicitudDAO.insertarSolicitudProyecto(new SolicitaProyectoDTO("S123", 50, TipoEstadoSolicitud.PENDIENTE, "VERANO"));

        List<SolicitaProyectoDTO> lista = solicitudDAO.obtenerSolicitudesProyectoPorPeriodo("VERANO");
        assertEquals("VERANO", lista.get(0).getPeriodo());
    }
}