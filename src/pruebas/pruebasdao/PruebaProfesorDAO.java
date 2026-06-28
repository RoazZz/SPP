package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.ProfesorDAO;
import logica.dto.ProfesorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstadoUsuario;
import logica.enums.TipoTurno;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaProfesorDAO {

    private static final int ID_SECCION_PRUEBA = 902;
    private static final String NUMERO_PERSONAL_PRUEBA = "PROF902";

    private static ProfesorDAO profesorDAO;
    private ProfesorDTO profesorValido;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        profesorDAO = new ProfesorDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM seccion WHERE idSeccion = " + ID_SECCION_PRUEBA);
            statement.execute("INSERT INTO seccion (idSeccion, Nombre) VALUES (" + ID_SECCION_PRUEBA + ", 'Seccion Profesor')");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM profesor WHERE NumeroDePersonal = '" + NUMERO_PERSONAL_PRUEBA + "'");
            statement.execute("DELETE FROM usuario WHERE Nombre = 'ProfesorPrueba'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        profesorValido = new ProfesorDTO(
                0,
                "ProfesorPrueba",
                "Apellido",
                "Materno",
                "clave12345",
                TipoEstadoUsuario.ACTIVO,
                TipoDeUsuario.PROFESOR,
                NUMERO_PERSONAL_PRUEBA,
                TipoTurno.MATUTINO,
                ID_SECCION_PRUEBA
        );
    }

    @Test
    public void pruebaAgregarProfesorExitoso() throws Exception {
        ProfesorDTO profesorGuardado = profesorDAO.agregarProfesor(profesorValido);
        assertTrue(profesorGuardado.getIdUsuario() > 0);
    }

    @Test
    public void pruebaActualizarProfesorExitoso() throws Exception {
        profesorDAO.agregarProfesor(profesorValido);
        profesorValido.setTurno(TipoTurno.VESPERTINO);
        profesorDAO.actualizarProfesor(profesorValido);
        ProfesorDTO profesorRecuperado = profesorDAO.buscarProfesorPorNumPersonal(NUMERO_PERSONAL_PRUEBA);
        assertEquals(TipoTurno.VESPERTINO, profesorRecuperado.getTurno());
    }

    @Test
    public void pruebaBuscarProfesorPorNumPersonalExitoso() throws Exception {
        profesorDAO.agregarProfesor(profesorValido);
        ProfesorDTO profesorRecuperado = profesorDAO.buscarProfesorPorNumPersonal(NUMERO_PERSONAL_PRUEBA);
        assertEquals(NUMERO_PERSONAL_PRUEBA, profesorRecuperado.getNumeroDePersonal());
    }

    @Test
    public void pruebaListarProfesoresExitoso() throws Exception {
        profesorDAO.agregarProfesor(profesorValido);
        List<ProfesorDTO> profesoresRecuperados = profesorDAO.listarProfesores();
        assertFalse(profesoresRecuperados.isEmpty());
    }

    @Test
    public void pruebaExisteProfesorConNumeroPersonalExitoso() throws Exception {
        profesorDAO.agregarProfesor(profesorValido);
        boolean existe = profesorDAO.existeProfesorConNumeroPersonal(NUMERO_PERSONAL_PRUEBA, 0);
        assertTrue(existe);
    }

    @Test
    public void pruebaBuscarProfesorPorNumPersonalExcepcionNoEncontrado() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () ->
                profesorDAO.buscarProfesorPorNumPersonal("NOEXISTE"));
    }

    @Test
    public void pruebaAgregarProfesorExcepcionNombreNulo() {
        profesorValido.setNombre(null);
        assertThrows(DAOExcepcion.class, () ->
                profesorDAO.agregarProfesor(profesorValido));
    }
}