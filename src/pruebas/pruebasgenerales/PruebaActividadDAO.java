package pruebasgenerales;

import accesodatos.ConexionBD;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.ActividadDAO;
import logica.dto.ActividadDTO;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PruebaActividadDAO {

    private static ActividadDAO actividadDAO;
    private static Connection conexion;

    @BeforeAll
    static void configurarConexion() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        conexion = ConexionBD.obtenerInstancia().obtenerConexion();

    }

    void limpiarTablas() throws Exception {
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0");
        conexion.createStatement().execute("TRUNCATE TABLE Actividad");
        conexion.createStatement().execute("TRUNCATE TABLE practicante");
        conexion.createStatement().execute("TRUNCATE TABLE usuario");
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");
    }

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

    @Nested
    class PruebasDeFlujoExitoso {

        private ActividadDTO actividadDTO;

        @BeforeEach
        void prepararPrueba() throws Exception {
            limpiarTablas();
            insertarDatosPrevios();
            actividadDAO = new ActividadDAO();
            actividadDTO = crearActividadEjemplo();
            actividadDAO.agregarActividad(actividadDTO);
        }

        @Test
        public void pruebaGuardarActividadExitoso() throws Exception {
            assertTrue(actividadDTO.getIdActividad() > 0);
        }
    }

    @Nested
    class PruebasDeFlujoFallido {

        @BeforeEach
        void prepararPrueba() throws Exception {
            limpiarTablas();
            insertarDatosPrevios();
            actividadDAO = new ActividadDAO();
        }

        @AfterEach
        void limpiarDespues() throws Exception {
            limpiarTablas();
        }

        @Test
        public void pruebaListarActividadFallido() throws Exception {
            List<ActividadDTO> lista = actividadDAO.listarActividades();
            assertTrue(lista.isEmpty(), "Lista de actividades Vacia");
        }
    }

    @Nested
    class PruebasDeFlujoExcepciones{

        @BeforeEach
        void prepararPrueba() throws Exception {
            limpiarTablas();
            insertarDatosPrevios();
            actividadDAO = new ActividadDAO();
        }

        @AfterEach
        void limpiarDespues() throws Exception {
            limpiarTablas();
        }

        @Test
        public void pruebaBuscarActividadNoExistente() {
            assertThrows(EntidadNoEncontradaExcepcion.class, () -> { actividadDAO.buscarActividadPorIdActividad(7); });
        }

    }
}
