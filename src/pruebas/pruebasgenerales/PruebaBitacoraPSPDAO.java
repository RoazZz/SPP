package pruebasgenerales;

import accesodatos.ConexionBD;
import logica.dao.BitacoraPSPDAO;
import logica.dto.BitacoraPSPDTO;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaBitacoraPSPDAO {

    @BeforeAll
    static void configurarConexion() {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
    }


    @BeforeEach
    void limpiarAntes() throws Exception {
        limpiarTablas();
        System.out.println("Limpieza ANTES de prueba");
        insertarDatosNecesarios();
    }

    @AfterEach
    void limpiarDespues() throws Exception {
        limpiarTablas();
        System.out.println("Limpieza DESPUÉS de prueba (aunque falle)");
    }

    void limpiarTablas() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement comandoControl = conexion.createStatement()) {
            comandoControl.execute("SET FOREIGN_KEY_CHECKS = 0");
            comandoControl.execute("TRUNCATE TABLE bitacorapsp");
            comandoControl.execute("TRUNCATE TABLE practicante");
            comandoControl.execute("TRUNCATE TABLE usuario");
            comandoControl.execute("TRUNCATE TABLE seccion");
            comandoControl.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    private void insertarDatosNecesarios() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();

        String sqlSeccion = "INSERT INTO seccion (idSeccion, Nombre) VALUES (?, ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sqlSeccion)) {
            sentencia.setInt(1, 1);
            sentencia.setString(2, "Sistemas");
            sentencia.executeUpdate();
        }

        String sqlUsuario = "INSERT INTO usuario (idUsuario, Nombre, ApellidoP, Contrasenia, TipoUsuario) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sqlUsuario)) {
            sentencia.setInt(1, 1);
            sentencia.setString(2, "Juan");
            sentencia.setString(3, "Perez");
            sentencia.setString(4, "pass123");
            sentencia.setString(5, "PRACTICANTE");
            sentencia.executeUpdate();
        }

        String sqlPracticante = "INSERT INTO practicante (Matricula, idSeccion, Semestre, Genero, Edad, idUsuario) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sqlPracticante)) {
            sentencia.setString(1, "S21012345");
            sentencia.setInt(2, 1);
            sentencia.setString(3, "7");
            sentencia.setString(4, "MASCULINO");
            sentencia.setInt(5, 21);
            sentencia.setInt(6, 1);
            sentencia.executeUpdate();
        }
    }

    @Test
    public void pruebaAgregarBuscarBitacoraPSP() throws Exception {
        BitacoraPSPDAO bitacoraDAO = new BitacoraPSPDAO();
        BitacoraPSPDTO bitacoraDTO = new BitacoraPSPDTO(0, "S21012345", LocalDate.now());

        bitacoraDAO.agregarBitacoraPSP(bitacoraDTO);
        BitacoraPSPDTO bitacoraRecuperada = bitacoraDAO.buscarBitacoraPSPPorId(bitacoraDTO.getIdBBitacora());

        assertEquals(bitacoraDTO.getMatricula(), bitacoraRecuperada.getMatricula(),
                "La matrícula de la bitácora recuperada debe ser idéntica a la guardada");
    }

    @Test
    public void pruebaActualizarBuscarBitacoraPSP() throws Exception {
        BitacoraPSPDAO bitacoraDAO = new BitacoraPSPDAO();
        BitacoraPSPDTO bitacoraDTO = new BitacoraPSPDTO(0, "S21012345", LocalDate.now());
        bitacoraDAO.agregarBitacoraPSP(bitacoraDTO);

        LocalDate nuevaFecha = LocalDate.now().minusDays(5);
        bitacoraDTO.setFecha(nuevaFecha);
        bitacoraDAO.actualizarBitacoraPSP(bitacoraDTO);

        BitacoraPSPDTO bitacoraActualizada = bitacoraDAO.buscarBitacoraPSPPorId(bitacoraDTO.getIdBBitacora());

        assertEquals(nuevaFecha, bitacoraActualizada.getFecha(),
                "La fecha debe haberse actualizado correctamente en la base de datos");
    }

    @Test
    public void pruebaObtenerTodasLasBitacorasPSP() throws Exception {
        BitacoraPSPDAO bitacoraDAO = new BitacoraPSPDAO();
        bitacoraDAO.agregarBitacoraPSP(new BitacoraPSPDTO(0, "S21012345", LocalDate.now()));

        List<BitacoraPSPDTO> listaBitacoras = bitacoraDAO.listarBitacorasPSP();

        assertFalse(listaBitacoras.isEmpty(), "La lista de bitácoras no debería estar vacía tras una inserción");
    }
}