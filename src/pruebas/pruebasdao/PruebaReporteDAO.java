package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.ReporteDAO;
import logica.dto.ReporteDTO;
import logica.enums.EstadoReporte;
import logica.enums.TipoReporte;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaReporteDAO {

    private static final int ID_USUARIO_PRUEBA = 914;
    private static final int ID_SECCION_PRUEBA = 914;
    private static final String MATRICULA_PRUEBA = "S20009140";

    private static ReporteDAO reporteDAO;
    private ReporteDTO reporteValido;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        reporteDAO = new ReporteDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM seccion WHERE idSeccion = 914");
            statement.execute("INSERT INTO seccion (idSeccion, Nombre) VALUES (914, 'Seccion Base')");
            statement.execute("DELETE FROM usuario WHERE idUsuario = 914");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (914, 'PracBase', 'Ap', 'Am', 'clave', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("DELETE FROM practicante WHERE Matricula = 'S20009140'");
            statement.execute("INSERT INTO practicante (Matricula, idSeccion, Semestre, Genero, Edad, LenguaIndigena, idUsuario) VALUES ('S20009140', 914, '5', 'MASCULINO', 22, 0, 914)");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM reporte WHERE idUsuario = " + ID_USUARIO_PRUEBA);
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        reporteValido = new ReporteDTO(0, ID_USUARIO_PRUEBA, TipoReporte.PARCIAL, LocalDate.of(2026, 4, 17),
                "ruta/reporte.pdf", EstadoReporte.ENTREGADO, "Abril", null);
    }

    @Test
    public void pruebaAgregarReporteExitoso() throws Exception {
        ReporteDTO reporteGuardado = reporteDAO.agregarReporte(reporteValido);
        assertTrue(reporteGuardado.getIdReporte() > 0);
    }

    @Test
    public void pruebaActualizarReporteExitoso() throws Exception {
        ReporteDTO reporteGuardado = reporteDAO.agregarReporte(reporteValido);
        reporteGuardado.setMes("Mayo");
        boolean resultado = reporteDAO.actualizarReporte(reporteGuardado);
        assertTrue(resultado);
    }

    @Test
    public void pruebaCalificarReporteExitoso() throws Exception {
        ReporteDTO reporteGuardado = reporteDAO.agregarReporte(reporteValido);
        boolean resultado = reporteDAO.calificarReporte(reporteGuardado.getIdReporte(), 9.0);
        assertTrue(resultado);
    }

    @Test
    public void pruebaBuscarReportePorIdExitoso() throws Exception {
        ReporteDTO reporteGuardado = reporteDAO.agregarReporte(reporteValido);
        ReporteDTO reporteRecuperado = reporteDAO.buscarReportePorId(reporteGuardado.getIdReporte());
        assertEquals(reporteGuardado.getIdReporte(), reporteRecuperado.getIdReporte());
    }

    @Test
    public void pruebaListarTodosReporteExitoso() throws Exception {
        reporteDAO.agregarReporte(reporteValido);
        List<ReporteDTO> reportesRecuperados = reporteDAO.listarTodosReporte();
        assertFalse(reportesRecuperados.isEmpty());
    }

    @Test
    public void pruebaListarReportesPorUsuarioExitoso() throws Exception {
        reporteDAO.agregarReporte(reporteValido);
        List<ReporteDTO> reportesRecuperados = reporteDAO.listarReportesPorUsuario(ID_USUARIO_PRUEBA);
        assertFalse(reportesRecuperados.isEmpty());
    }

    @Test
    public void pruebaExisteDuplicadoExitoso() throws Exception {
        reporteDAO.agregarReporte(reporteValido);
        boolean existe = reporteDAO.existeDuplicado(ID_USUARIO_PRUEBA, TipoReporte.PARCIAL, "Abril", EstadoReporte.ENTREGADO);
        assertTrue(existe);
    }

    @Test
    public void pruebaBuscarReportePorIdExcepcionNoEncontrado() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () ->
                reporteDAO.buscarReportePorId(-1));
    }

    @Test
    public void pruebaAgregarReporteExcepcionRutaNula() {
        reporteValido.setRuta(null);
        assertThrows(DAOExcepcion.class, () ->
                reporteDAO.agregarReporte(reporteValido));
    }
}