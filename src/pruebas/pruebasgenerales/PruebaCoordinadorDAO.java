package pruebasgenerales;

import accesodatos.ConexionBD;
import logica.dao.CoordinadorDAO;
import logica.dto.CoordinadorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaCoordinadorDAO {

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
        conexion.createStatement().execute("TRUNCATE TABLE Coordinador");
        conexion.createStatement().execute("TRUNCATE TABLE Usuario");
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    private CoordinadorDTO crearCoordinadorEjemplo() {
        return new CoordinadorDTO(
                0,
                "Jared",
                "Morales",
                "Tirado",
                "123",
                TipoEstado.ACTIVO,
                TipoDeUsuario.COORDINADOR,
                "25110"
        );
    }

    @Test
    public void pruebaAgregarCoordinador() throws Exception {
        CoordinadorDAO coordinadorDAO = new CoordinadorDAO();
        CoordinadorDTO coordinadorDTO = crearCoordinadorEjemplo();

        coordinadorDAO.agregarCoordinador(coordinadorDTO);

        assertTrue(coordinadorDTO.getIdUsuario() > 0);
    }

    @Test
    public void pruebaListarCoordinadores() throws Exception {
        CoordinadorDAO coordinadorDAO = new CoordinadorDAO();
        coordinadorDAO.agregarCoordinador(crearCoordinadorEjemplo());

        List<CoordinadorDTO> lista = coordinadorDAO.listarCoordinador();

        assertFalse(lista.isEmpty());
    }

    @Test
    public void pruebaActualizarCoordinador() throws Exception {
        CoordinadorDAO coordinadorDAO = new CoordinadorDAO();
        CoordinadorDTO coordinadorDTO = crearCoordinadorEjemplo();
        coordinadorDAO.agregarCoordinador(coordinadorDTO);

        coordinadorDTO.setNumeroPersonal("00000");
        coordinadorDAO.actualizarCoordinador(coordinadorDTO);

        List<CoordinadorDTO> lista = coordinadorDAO.listarCoordinador();

        assertEquals("00000", lista.getFirst().getNumeroPersonal());
    }
}
