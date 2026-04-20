package pruebasgenerales;

import accesodatos.ConexionBD;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.BitacoraDAO;
import logica.dto.BitacoraDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaBitacoraDAO {

    private static BitacoraDAO bitacoraDAO;
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
        conexion.createStatement().execute("TRUNCATE TABLE Bitacora");
        conexion.createStatement().execute("TRUNCATE TABLE Practicante");
        conexion.createStatement().execute("TRUNCATE TABLE Usuario");
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

    private BitacoraDTO crearBitacoraEjemplo() {
        return new BitacoraDTO(
                0,
                "S24021",
                "LOGIN",
                LocalDateTime.of(2026, 4, 17, 10, 30, 0),
                "El practicante inició sesión"
        );
    }

    @Nested
    class PruebasDeFlujoExitoso {

        private BitacoraDTO bitacoraDTO;

        @BeforeEach
        void prepararPrueba() throws Exception{
            limpiarTablas();
            insertarDatosPrevios();
            bitacoraDAO = new BitacoraDAO();
            bitacoraDTO = crearBitacoraEjemplo();
            bitacoraDAO.agregarBitacora(bitacoraDTO);
        }

        @Test
        public void pruebaGuardarActividadExitoso() throws Exception {
            assertTrue(bitacoraDTO.getIdRegistro() > 0);
        }

    }

    @Nested
    class PruebasDeFlujoFallido {

        @BeforeEach
        void prepararPrueba() throws Exception {
            limpiarTablas();
            insertarDatosPrevios();
            bitacoraDAO = new BitacoraDAO();
        }

        @AfterEach
        void limpiarDespues() throws Exception {
            limpiarTablas();
        }

        @Test
        public void pruebaListarActividadFallido() throws Exception {
            List<BitacoraDTO> lista = bitacoraDAO.listarBitacoras();
            assertTrue(lista.isEmpty(), "Lista de Bitacoras Vacia");
        }

    }

    @Nested
    class PruebasDeFlujoExcepciones {

        @BeforeEach
        void prepararPrueba() throws Exception {
            limpiarTablas();
            insertarDatosPrevios();
            bitacoraDAO = new BitacoraDAO();
        }

        @AfterEach
        void limpiarDespues() throws Exception {
            limpiarTablas();
        }

        @Test
        public void pruebaBuscarActividadNoExistente() {
            assertThrows(EntidadNoEncontradaExcepcion.class, () -> { bitacoraDAO.buscarBitacoraPorMatricula("00000"); });
        }

    }
}
