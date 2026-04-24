package pruebasgenerales;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.ProfesorDAO;
import logica.dto.ProfesorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import logica.enums.TipoTurno;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaProfesorDAO {
    private static ProfesorDAO profesorDAO;
    private ProfesorDTO profesorValido;
    private ProfesorDTO profesorInvalidoNombreNulo;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        profesorDAO = new ProfesorDAO();

        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE profesor");
            statement.execute("TRUNCATE TABLE usuario");

            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                    "VALUES (999, 'Profesor', 'Maestro', 'Test', 'pass123', 'ACTIVO', 'PROFESOR')");

            statement.execute("INSERT INTO profesor (NumeroDePersonal, Turno, idUsuario) " +
                    "VALUES ('99999', 'MATUTINO', 999)");

            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM profesor WHERE NumeroDePersonal != '99999'");
            statement.execute("DELETE FROM usuario WHERE idUsuario != 999");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }

        profesorValido = new ProfesorDTO(0, "Roaz", "León", "M", "roaz123", TipoEstado.ACTIVO, TipoDeUsuario.PROFESOR, "12345", TipoTurno.MATUTINO);
        profesorInvalidoNombreNulo = new ProfesorDTO(0, null, "Error", "M", "123", TipoEstado.ACTIVO, TipoDeUsuario.PROFESOR, "00000", TipoTurno.VESPERTINO);
    }

    @AfterEach
    void restaurarRecursos() {
        ConexionBD.reset();
        try {
            profesorDAO = new ProfesorDAO();
        } catch (Exception e) {
            System.err.println("Error al restaurar el DAO: " + e.getMessage());
        }
    }

    @Test
    public void pruebaAgregarProfesorExitoso() throws Exception {
        ProfesorDTO resultado = profesorDAO.agregarProfesor(profesorValido);
        assertNotNull(resultado);
    }

    @Test
    public void pruebaBuscarProfesorPorNumPersonalExitoso() throws Exception {
        ProfesorDTO resultado = profesorDAO.buscarProfesorPorNumPersonal("99999");
        assertEquals("Profesor", resultado.getNombre());
    }

    @Test
    public void pruebaListarProfesoresExitoso() throws Exception {
        List<ProfesorDTO> lista = profesorDAO.listarProfesores();
        assertFalse(lista.isEmpty());
    }

    @Test
    public void pruebaAgregarProfesorExcepcionNombreNulo() {
        assertThrows(DAOExcepcion.class, () -> profesorDAO.agregarProfesor(profesorInvalidoNombreNulo));
    }

    @Test
    public void pruebaActualizarProfesorExcepcionConexionCerrada() throws Exception {
        ConexionBD.obtenerInstancia().obtenerConexion().close();
        assertThrows(DAOExcepcion.class, () -> profesorDAO.actualizarProfesor(profesorValido));
    }
}