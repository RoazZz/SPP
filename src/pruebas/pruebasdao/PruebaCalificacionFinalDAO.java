package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.CalificacionFinalDAO;
import logica.dto.CalificacionFinalDTO;
import logica.enums.EstadoCalificacionFinal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaCalificacionFinalDAO {

    private static final String MATRICULA_PRUEBA = "S20009160";

    private static CalificacionFinalDAO calificacionFinalDAO;
    private CalificacionFinalDTO calificacionValida;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        calificacionFinalDAO = new CalificacionFinalDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM seccion WHERE idSeccion = 916");
            statement.execute("INSERT INTO seccion (idSeccion, Nombre) VALUES (916, 'Seccion Base')");
            statement.execute("DELETE FROM usuario WHERE idUsuario = 916");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (916, 'PracBase', 'Ap', 'Am', 'clave', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("DELETE FROM practicante WHERE Matricula = 'S20009160'");
            statement.execute("INSERT INTO practicante (Matricula, idSeccion, Semestre, Genero, Edad, LenguaIndigena, idUsuario) VALUES ('S20009160', 916, '5', 'MASCULINO', 22, 0, 916)");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM calificacionfinal WHERE Matricula = '" + MATRICULA_PRUEBA + "'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        calificacionValida = new CalificacionFinalDTO(
                0, MATRICULA_PRUEBA,
                5.0, 20.0, 10.0, 10.0, 10.0, 15.0, 15.0, 10.0, 5.0,
                100.0, 10.0, EstadoCalificacionFinal.CALCULADA
        );
    }

    @Test
    public void pruebaGuardarCalificacionFinalExitoso() throws Exception {
        boolean resultado = calificacionFinalDAO.guardarCalificacionFinal(calificacionValida);
        assertTrue(resultado);
    }

    @Test
    public void pruebaGuardarCalificacionFinalExcepcionMatriculaNula() {
        calificacionValida.setMatricula(null);
        assertThrows(DAOExcepcion.class, () ->
                calificacionFinalDAO.guardarCalificacionFinal(calificacionValida));
    }
}