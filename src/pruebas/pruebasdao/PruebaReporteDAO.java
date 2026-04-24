package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.ReporteDAO;
import logica.dto.ReporteDTO;
import logica.enums.TipoReporte;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaReporteDAO {
    private static ReporteDAO reporteDAO;
    private ReporteDTO reporteValido;
    private ReporteDTO reporteInvalidoDatosNulos;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        reporteDAO = new ReporteDAO();

        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE reporte");

            statement.execute("INSERT INTO reporte (idReporte, TipoReporte, Fecha, Ruta) " +
                    "VALUES (999, 'PARCIAL', '2026-04-20', '/rutas/maestro.pdf')");

            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM reporte WHERE idReporte != 999");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }

        reporteValido = new ReporteDTO(0, TipoReporte.MENSUAL, LocalDate.now(), "/rutas/nuevo.pdf");
        reporteInvalidoDatosNulos = new ReporteDTO(0, null, LocalDate.now(), null);
    }

    @AfterEach
    void restaurarRecursos() {
        ConexionBD.reset();
        try {
            reporteDAO = new ReporteDAO();
        } catch (Exception e) {
            System.err.println("Error al restaurar el DAO: " + e.getMessage());
        }
    }

    @Test
    public void pruebaAgregarReporteExitoso() throws Exception {
        ReporteDTO resultado = reporteDAO.agregarReporte(reporteValido);
        assertNotNull(resultado);
    }

    @Test
    public void pruebaBuscarReportePorIdExitoso() throws Exception {
        ReporteDTO recuperado = reporteDAO.buscarReportePorId(999);
        assertEquals("/rutas/maestro.pdf", recuperado.getRuta());
    }

    @Test
    public void pruebaListarTodosReporteExitoso() throws Exception {
        List<ReporteDTO> lista = reporteDAO.listarTodosReporte();
        assertFalse(lista.isEmpty());
    }

    @Test
    public void pruebaAgregarReporteErrorDatosNulos() {
        assertThrows(DAOExcepcion.class, () -> reporteDAO.agregarReporte(reporteInvalidoDatosNulos));
    }

    @Test
    public void pruebaActualizarReporteExcepcionConexionCerrada() throws Exception {
        ConexionBD.obtenerInstancia().obtenerConexion().close();
        assertThrows(DAOExcepcion.class, () -> reporteDAO.actualizarReporte(reporteValido));
    }
}