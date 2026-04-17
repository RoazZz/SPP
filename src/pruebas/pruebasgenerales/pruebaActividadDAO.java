package pruebasgenerales;

import accesodatos.ConexionBD;
import logica.dao.ActividadDAO;
import logica.dto.ActividadDTO;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


public class pruebaActividadDAO {

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
    }

    @AfterEach
    void limpiarDespues() throws Exception {
        limpiarTablas();
    }

    void limpiarTablas() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();

        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0");
        conexion.createStatement().execute("TRUNCATE TABLE Actividad");
        conexion.createStatement().execute("TRUNCATE TABLE Practicante");
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    private ActividadDTO crearActividadEjemplo() {
        return new ActividadDTO(
                0,
                "S24021",
                "Actividad 1",
                "Esto es una actividad de prueba",
                java.sql.Date.valueOf("2026-04-17")
        );
    }

    @Test
    public void pruebaGuardarActividad() throws Exception {
        ActividadDAO actividadDAO = new ActividadDAO();
        ActividadDTO actividadDTO = crearActividadEjemplo();

        actividadDAO.agregarActividad(actividadDTO);
        ActividadDTO resultado = actividadDAO.buscarActividadPorIdActividad(0);

        assertEquals("Actividad 1", resultado.getNombre());
    }

    @Test
    public void pruebaListarProfesores() throws Exception {
        ActividadDAO actividadDAO = new ActividadDAO();
        ActividadDTO actividadDTO = crearActividadEjemplo();

        actividadDAO.agregarActividad(actividadDTO);
        List<ActividadDTO> lista = actividadDAO.listarActividades();

        assertFalse(lista.isEmpty());
    }

    @Test
    public void pruebaActualizarProfesor() throws Exception {
        ActividadDAO actividadDAO = new ActividadDAO();
        ActividadDTO actividadDTO = crearActividadEjemplo();

        actividadDAO.agregarActividad(actividadDTO);

        actividadDTO.setNombre("Actividad 1.1");
        actividadDAO.actualizarActividad(actividadDTO);

        ActividadDTO resultado = actividadDAO.buscarActividadPorIdActividad(actividadDTO.getIdActividad());

        assertEquals("Actividad 1.1", resultado.getNombre());
    }


}
