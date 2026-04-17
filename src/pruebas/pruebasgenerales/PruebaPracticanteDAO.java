package pruebasgenerales;

import accesodatos.ConexionBD;
import logica.dao.PracticanteDAO;
import logica.dto.PracticanteDTO;
import logica.enums.GeneroDelPracticante;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaPracticanteDAO {

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

    private void limpiarTablas() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0");
        conexion.createStatement().execute("TRUNCATE TABLE Practicante");
        conexion.createStatement().execute("TRUNCATE TABLE Usuario");
        conexion.createStatement().execute("TRUNCATE TABLE Seccion");
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @BeforeEach
    void insertarDatosPrevios() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0");
        conexion.createStatement().execute(
                "INSERT INTO Seccion (idSeccion, Nombre) " +
                        "VALUES (1, 'Matutino')"
        );
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    private PracticanteDTO crearPracticanteEjemplo() {
        return new PracticanteDTO(
                0,
                "Jared",
                "Morales",
                "Tirado",
                "123",
                TipoEstado.ACTIVO,
                TipoDeUsuario.PRACTICANTE,
                "S24021",
                1,
                "5",
                GeneroDelPracticante.MASCULINO,
                20,
                false
        );
    }

    @Test
    public void pruebaAgregarPracticante() throws Exception {
        PracticanteDAO practicanteDAO = new PracticanteDAO();
        PracticanteDTO practicanteDTO = crearPracticanteEjemplo();

        practicanteDAO.agregarPracticante(practicanteDTO);

        assertTrue(practicanteDTO.getIdUsuario() > 0);
    }

    @Test
    public void pruebaBuscarPracticantePorMatricula() throws Exception {
        PracticanteDAO practicanteDAO = new PracticanteDAO();
        PracticanteDTO practicanteDTO = crearPracticanteEjemplo();
        practicanteDAO.agregarPracticante(practicanteDTO);

        PracticanteDTO resultado = practicanteDAO.buscarPracticantePorMatricula("S24021");

        assertEquals("S24021", resultado.getMatricula());
    }

    @Test
    public void pruebaActualizarPracticante() throws Exception {
        PracticanteDAO practicanteDAO = new PracticanteDAO();
        PracticanteDTO practicanteDTO = crearPracticanteEjemplo();
        practicanteDAO.agregarPracticante(practicanteDTO);

        practicanteDTO.setSemestre("4");
        practicanteDAO.actualizarPracticante(practicanteDTO);

        PracticanteDTO resultado = practicanteDAO.buscarPracticantePorMatricula("S24021");

        assertEquals("4", resultado.getSemestre());
    }

    @Test
    public void pruebaListarPracticantes() throws Exception {
        PracticanteDAO practicanteDAO = new PracticanteDAO();
        practicanteDAO.agregarPracticante(crearPracticanteEjemplo());

        List<PracticanteDTO> lista = practicanteDAO.listarPracticantes();

        assertFalse(lista.isEmpty());

    }


















}
