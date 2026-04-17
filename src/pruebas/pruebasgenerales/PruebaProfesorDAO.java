package pruebasgenerales;

import accesodatos.ConexionBD;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaProfesorDAO {

    @BeforeAll
    static void configurarConexion() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        System.out.println("Conexión reiniciada");
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

    @BeforeEach
    void limpiarTablas() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();

        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0");
        conexion.createStatement().execute("TRUNCATE TABLE profesor");
        conexion.createStatement().execute("TRUNCATE TABLE usuario");
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");

        System.out.println("Tablas limpiadas para prueba");
    }

    private ProfesorDTO crearProfesorEjemplo() {
        return new ProfesorDTO(
                0,
                "Roaz",
                "Maestro",
                "León",
                "roaz123",
                TipoEstado.ACTIVO,
                TipoDeUsuario.PROFESOR,
                "12345",
                TipoTurno.MATUTINO
        );
    }

    @Test
    public void pruebaGuardarProfesor() throws Exception {
        ProfesorDAO profesorDAO = new ProfesorDAO();
        ProfesorDTO profesorDTO = crearProfesorEjemplo();

        profesorDAO.agregarProfesor(profesorDTO);
        ProfesorDTO resultado = profesorDAO.buscarProfesorPorNumPersonal("12345");

        assertEquals("Roaz", resultado.getNombre());
        System.out.println("Prueba de guardar profesor pasó exitosamente");
    }

    @Test
    public void pruebaListarProfesores() throws Exception {
        ProfesorDAO profesorDAO = new ProfesorDAO();
        ProfesorDTO profesorDTO = crearProfesorEjemplo();

        profesorDAO.agregarProfesor(profesorDTO);
        List<ProfesorDTO> lista = profesorDAO.listarProfesores();

        assertFalse(lista.isEmpty());
        System.out.println("Prueba de listar profesores pasó exitosamente");
    }

    @Test
    public void pruebaActualizarProfesor() throws Exception {
        ProfesorDAO profesorDAO = new ProfesorDAO();
        ProfesorDTO profesorDTO = crearProfesorEjemplo();

        profesorDAO.agregarProfesor(profesorDTO);

        profesorDTO.setTurno(TipoTurno.VESPERTINO);
        profesorDAO.actualizarProfesor(profesorDTO);

        ProfesorDTO resultado = profesorDAO.buscarProfesorPorNumPersonal("12345");

        assertEquals(TipoTurno.VESPERTINO, resultado.getTurno());
        System.out.println("Prueba de actualizar profesor pasó exitosamente");
    }
}