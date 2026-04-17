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


public class PruebaActividadDAO {

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
        conexion.createStatement().execute("TRUNCATE TABLE practicante");
        conexion.createStatement().execute("TRUNCATE TABLE usuario");
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @BeforeEach
    void insertarDatosPrevios() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0");
        conexion.createStatement().execute(
                "INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                        "VALUES (1, 'Ana', 'Perez', 'Lopez', '123', 'ACTIVO', 'PRACTICANTE')"
        );
        conexion.createStatement().execute(
                "INSERT INTO practicante (idUsuario, Matricula, idSeccion, Semestre, Genero, Edad, LenguaIndigena) " +
                        "VALUES (1, 'S24021', 1, '5', 'FEMENINO', 20, false)"
        );
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
        ActividadDTO resultado = actividadDAO.buscarActividadPorIdActividad(1);

        assertEquals("Actividad 1", resultado.getNombre());
    }

    @Test
    public void pruebaListarActividad() throws Exception {
        ActividadDAO actividadDAO = new ActividadDAO();
        ActividadDTO actividadDTO = crearActividadEjemplo();

        actividadDAO.agregarActividad(actividadDTO);
        List<ActividadDTO> lista = actividadDAO.listarActividades();

        assertFalse(lista.isEmpty());
    }

    @Test
    public void pruebaActualizarActividad() throws Exception {
        ActividadDAO actividadDAO = new ActividadDAO();
        ActividadDTO actividadDTO = crearActividadEjemplo();

        actividadDAO.agregarActividad(actividadDTO);

        actividadDTO.setNombre("Actividad 1.1");
        actividadDAO.actualizarActividad(actividadDTO);

        ActividadDTO resultado = actividadDAO.buscarActividadPorIdActividad(actividadDTO.getIdActividad());

        assertEquals("Actividad 1.1", resultado.getNombre());
    }
}
