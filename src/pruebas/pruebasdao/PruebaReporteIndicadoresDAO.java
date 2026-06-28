package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.ConsultaIndicadoresExcepcion;
import logica.dao.ReporteIndicadoresDAO;
import logica.dto.ReporteIndicadoresDTO;
import logica.enums.FiltrosIndicadores;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PruebaReporteIndicadoresDAO {

    private static ReporteIndicadoresDAO reporteIndicadoresDAO;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        reporteIndicadoresDAO = new ReporteIndicadoresDAO();

        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE Practicante");
            statement.execute("INSERT INTO Practicante (idUsuario, Matricula, idSeccion, Semestre, Genero, Edad, LenguaIndigena) " +
                    "VALUES (101, 'S21011111', 1, '5', 'MASCULINO', 21, 0)");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void limpiar() {
        // Al ser consultas agregadas de estadísticas del sistema de solo lectura, basta con los datos estables de BeforeAll
    }

    @Test
    public void pruebaContarPracticantesPorGeneroFlujoExitoso() throws ConsultaIndicadoresExcepcion {
        List<ReporteIndicadoresDTO> resultado = reporteIndicadoresDAO.contarPracticantesPor(FiltrosIndicadores.GENERO);
        assertNotNull(resultado);
    }

    @Test
    public void pruebaContarPracticantesPorEdadFlujoExitoso() throws ConsultaIndicadoresExcepcion {
        List<ReporteIndicadoresDTO> resultado = reporteIndicadoresDAO.contarPracticantesPor(FiltrosIndicadores.EDAD);
        assertNotNull(resultado);
    }

    @Test
    public void pruebaContarPracticantesPorSemestreFlujoExitoso() throws ConsultaIndicadoresExcepcion {
        List<ReporteIndicadoresDTO> resultado = reporteIndicadoresDAO.contarPracticantesPor(FiltrosIndicadores.SEMESTRE);
        assertNotNull(resultado);
    }

    @Test
    public void pruebaContarPracticantesPorFlujoFallido() {
        assertThrows(IllegalArgumentException.class, () -> {
            reporteIndicadoresDAO.contarPracticantesPor(null);
        });
    }
}