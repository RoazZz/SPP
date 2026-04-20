package pruebasgenerales;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.AutoevaluacionDAO;
import logica.dto.AutoevaluacionDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaAutoevaluacionDAO {
    private static AutoevaluacionDAO autoevaluacionDAO;
    private AutoevaluacionDTO dtoParaAgregar;
    private AutoevaluacionDTO dtoInvalido;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        autoevaluacionDAO = new AutoevaluacionDAO();

        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE autoevaluacion");
            statement.execute("TRUNCATE TABLE practicante");
            statement.execute("TRUNCATE TABLE usuario");
            statement.execute("TRUNCATE TABLE seccion");
            statement.execute("INSERT INTO seccion VALUES (1, 'Sistemas')");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, Contrasenia, TipoUsuario) VALUES (1, 'Juan', 'Perez', 'pass', 'PRACTICANTE')");
            statement.execute("INSERT INTO practicante (Matricula, idSeccion, Semestre, Genero, Edad, idUsuario) VALUES ('S21012345', 1, 'Quinto', 'MASCULINO', 20, 1)");
            statement.execute("INSERT INTO autoevaluacion (idAutoEvaluacion, Matricula, Calificacion, Comentarios) VALUES (999, 'S21012345', 8.50, 'Maestro')");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiarTablas() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM autoevaluacion WHERE idAutoEvaluacion != 999");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }

        dtoParaAgregar = new AutoevaluacionDTO(0, "S21012345", new BigDecimal("9.00"), "Nuevo");
        dtoInvalido = new AutoevaluacionDTO(0, null, new BigDecimal("0.00"), "Error");
    }

    @Test
    public void pruebaAgregarAutoevaluacionExitoso() throws Exception {
        AutoevaluacionDTO resultado = autoevaluacionDAO.agregarAutoevalaucion(dtoParaAgregar);
        assertTrue(resultado.getIdAutoevalaucion() > 0);
    }

    @Test
    public void pruebaBuscarAutoevaluacionExitoso() throws Exception {
        AutoevaluacionDTO recuperado = autoevaluacionDAO.buscarAutoevaluacionPorMatricula("S21012345");
        assertEquals(999, recuperado.getIdAutoevalaucion());
    }

    @Test
    public void pruebaAgregarAutoevaluacionErrorDatosNulos() {
        assertThrows(DAOExcepcion.class, () -> autoevaluacionDAO.agregarAutoevalaucion(dtoInvalido));
    }

    @Test
    public void pruebaObtenerTodasLasAutoevaluacionesExcepcionConexionCerrada() throws Exception {
        ConexionBD.obtenerInstancia().obtenerConexion().close();
        assertThrows(DAOExcepcion.class, () -> autoevaluacionDAO.obtenerTodasLasAutoevaluaciones());
        ConexionBD.reset();
        autoevaluacionDAO = new AutoevaluacionDAO();
    }
}