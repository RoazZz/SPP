package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.AutoevaluacionDAO;
import logica.dto.AutoevaluacionDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaAutoevaluacionDAO {

    private static final String MATRICULA_PRUEBA = "S20009110";

    private static AutoevaluacionDAO autoevaluacionDAO;
    private AutoevaluacionDTO autoevaluacionValida;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        autoevaluacionDAO = new AutoevaluacionDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM seccion WHERE idSeccion = 911");
            statement.execute("INSERT INTO seccion (idSeccion, Nombre) VALUES (911, 'Seccion Base')");
            statement.execute("DELETE FROM usuario WHERE idUsuario = 911");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (911, 'PracBase', 'Ap', 'Am', 'clave', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("DELETE FROM practicante WHERE Matricula = 'S20009110'");
            statement.execute("INSERT INTO practicante (Matricula, idSeccion, Semestre, Genero, Edad, LenguaIndigena, idUsuario) VALUES ('S20009110', 911, '5', 'MASCULINO', 22, 0, 911)");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM autoevaluacion WHERE Matricula = '" + MATRICULA_PRUEBA + "'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        autoevaluacionValida = new AutoevaluacionDTO(0, MATRICULA_PRUEBA, new BigDecimal("9.5"), "Buen desempeno", "ruta/auto.pdf");
    }

    @Test
    public void pruebaAgregarAutoevaluacionExitoso() throws Exception {
        AutoevaluacionDTO autoevaluacionGuardada = autoevaluacionDAO.agregarAutoevaluacion(autoevaluacionValida);
        assertTrue(autoevaluacionGuardada.getIdAutoevaluacion() > 0);
    }

    @Test
    public void pruebaActualizarAutoevaluacionExitoso() throws Exception {
        AutoevaluacionDTO autoevaluacionGuardada = autoevaluacionDAO.agregarAutoevaluacion(autoevaluacionValida);
        autoevaluacionGuardada.setComentarios("Comentario modificado");
        boolean resultado = autoevaluacionDAO.actualizarAutoevaluacion(autoevaluacionGuardada);
        assertTrue(resultado);
    }

    @Test
    public void pruebaCalificarAutoevaluacionExitoso() throws Exception {
        autoevaluacionDAO.agregarAutoevaluacion(autoevaluacionValida);
        boolean resultado = autoevaluacionDAO.calificarAutoevaluacion(MATRICULA_PRUEBA, 8.0);
        assertTrue(resultado);
    }

    @Test
    public void pruebaObtenerTodasLasAutoevaluacionesExitoso() throws Exception {
        autoevaluacionDAO.agregarAutoevaluacion(autoevaluacionValida);
        List<AutoevaluacionDTO> autoevaluacionesRecuperadas = autoevaluacionDAO.obtenerTodasLasAutoevaluaciones();
        assertFalse(autoevaluacionesRecuperadas.isEmpty());
    }

    @Test
    public void pruebaExisteAutoevaluacionPorMatriculaExitoso() throws Exception {
        autoevaluacionDAO.agregarAutoevaluacion(autoevaluacionValida);
        boolean existe = autoevaluacionDAO.existeAutoevaluacionPorMatricula(MATRICULA_PRUEBA);
        assertTrue(existe);
    }

    @Test
    public void pruebaExisteAutoevaluacionPorMatriculaFallidoNoExiste() throws Exception {
        boolean existe = autoevaluacionDAO.existeAutoevaluacionPorMatricula("NOEXISTE00");
        assertFalse(existe);
    }

    @Test
    public void pruebaAgregarAutoevaluacionExcepcionMatriculaNula() {
        autoevaluacionValida.setMatricula(null);
        assertThrows(DAOExcepcion.class, () ->
                autoevaluacionDAO.agregarAutoevaluacion(autoevaluacionValida));
    }
}