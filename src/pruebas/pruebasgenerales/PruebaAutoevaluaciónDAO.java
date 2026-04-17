package pruebasgenerales;

import accesodatos.ConexionBD;
import logica.dao.AutoevaluacionDAO;
import logica.dto.AutoevaluacionDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PruebaAutoevaluaciónDAO {


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
        insertarDatosNecesarios();
    }

    @AfterEach
    void limpiarDespues() throws Exception {
        limpiarTablas();
        System.out.println("Limpieza DESPUÉS de prueba (aunque falle)");
    }

    void limpiarTablas() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();

        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0");
        conexion.createStatement().execute("TRUNCATE TABLE autoevaluacion");
        conexion.createStatement().execute("TRUNCATE TABLE practicante");
        conexion.createStatement().execute("TRUNCATE TABLE usuario");
        conexion.createStatement().execute("TRUNCATE TABLE seccion");
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    private void insertarDatosNecesarios() throws Exception {
        Connection con = ConexionBD.obtenerInstancia().obtenerConexion();
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO seccion (idSeccion, Nombre) VALUES (1, 'Sistemas')")) {
            ps.executeUpdate();
        }
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, Contrasenia, TipoUsuario) VALUES (1, 'Juan', 'Perez', 'pass', 'PRACTICANTE')")) {
            ps.executeUpdate();
        }
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO practicante (Matricula, idSeccion, Semestre, Genero, Edad, idUsuario) VALUES ('S21012345', 1, 'Quinto', 'MASCULINO', 20, 1)")) {
            ps.executeUpdate();
        }
    }

    @Test
    public void pruebaAgregarBuscarAutoevaluacion() throws Exception {
        AutoevaluacionDAO autoevaluacionDAO = new AutoevaluacionDAO();
        AutoevaluacionDTO autoevaluacionDTO = new AutoevaluacionDTO(0, "S21012345", new java.math.BigDecimal("8.50"), "Buen inicio");
        autoevaluacionDAO.agregarAutoevalaucion(autoevaluacionDTO);
        AutoevaluacionDTO recuperado = autoevaluacionDAO.buscarAutoevaluacionPorMatricula("S21012345");

        assertEquals(autoevaluacionDTO.getMatricula(), recuperado.getMatricula(), "La autoevaluación buscada debe coincidir con la agregada");
    }

    @Test
    public void pruebaActualizarBuscarAutoevaluacion() throws Exception {
        AutoevaluacionDAO autoevaluacionDAO = new AutoevaluacionDAO();
        AutoevaluacionDTO autoevaluacionDTO = new AutoevaluacionDTO(0, "S21012345", new java.math.BigDecimal("7.00"), "Regular");
        autoevaluacionDAO.agregarAutoevalaucion(autoevaluacionDTO);

        autoevaluacionDTO.setCalificacion(new java.math.BigDecimal("10.00"));
        autoevaluacionDTO.setComentarios("Excelente mejora");
        autoevaluacionDAO.actualizarAutoevaluacion(autoevaluacionDTO);

        AutoevaluacionDTO actualizado = autoevaluacionDAO.buscarAutoevaluacionPorMatricula("S21012345");

        assertEquals(new java.math.BigDecimal("10.00"), actualizado.getCalificacion(), "La calificación debe haberse actualizado en la base de datos");
    }

    @Test
    public void pruebaObtenerTodasLasAutoevaluaciones() throws Exception {
        AutoevaluacionDAO dao = new AutoevaluacionDAO();
        dao.agregarAutoevalaucion(new AutoevaluacionDTO(0, "S21012345", new java.math.BigDecimal("9.00"), "Nota 1"));

        java.util.List<AutoevaluacionDTO> lista = dao.obtenerTodasLasAutoevaluaciones();

        assertFalse(lista.isEmpty(), "La lista de autoevaluaciones no debería estar vacía");
    }

}
