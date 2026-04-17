package pruebasgenerales;

import accesodatos.ConexionBD;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaReporteDAO {
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
            comandoControl.execute("TRUNCATE TABLE reporte");
            comandoControl.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @Test
    public void pruebaAgregarBuscarReporte() throws Exception {
        ReporteDAO reporteDAO = new ReporteDAO();
        ReporteDTO reporteDTO = new ReporteDTO(0, TipoReporte.PARCIAL, LocalDate.now(), "/rutas/reporte1.pdf");

        reporteDAO.agregarReporte(reporteDTO);

        // Verificamos que el ID ya no sea 0
        assertTrue(reporteDTO.getIdReporte() > 0, "El ID del reporte debe haber sido generado por la base de datos");

        ReporteDTO recuperado = reporteDAO.buscarReportePorId(reporteDTO.getIdReporte());
        assertEquals(TipoReporte.PARCIAL, recuperado.getTipoReporte(), "El tipo de reporte debe coincidir");
        assertEquals("/rutas/reporte1.pdf", recuperado.getRuta(), "La ruta debe ser la misma");
    }

    @Test
    public void pruebaActualizarBuscarReporte() throws Exception {
        ReporteDAO reporteDAO = new ReporteDAO();
        ReporteDTO reporteDTO = new ReporteDTO(0, TipoReporte.MENSUAL, LocalDate.now(), "/vieja/ruta.pdf");
        reporteDAO.agregarReporte(reporteDTO);

        // Modificamos
        reporteDTO.setTipoReporte(TipoReporte.PARCIAL);
        reporteDTO.setRuta("/nueva/ruta.pdf");
        reporteDAO.actualizarReporte(reporteDTO);

        ReporteDTO actualizado = reporteDAO.buscarReportePorId(reporteDTO.getIdReporte());
        assertEquals(TipoReporte.PARCIAL, actualizado.getTipoReporte());
        assertEquals("/nueva/ruta.pdf", actualizado.getRuta());
    }

    @Test
    public void pruebaObtenerTodosLosReportes() throws Exception {
        ReporteDAO reporteDAO = new ReporteDAO();
        reporteDAO.agregarReporte(new ReporteDTO(0, TipoReporte.PARCIAL, LocalDate.now(), "r1"));
        reporteDAO.agregarReporte(new ReporteDTO(0, TipoReporte.MENSUAL, LocalDate.now(), "r2"));

        List<ReporteDTO> lista = reporteDAO.listarTodosReporte();

        assertEquals(2, lista.size(), "Deberían existir 2 reportes en la lista");
    }
}
