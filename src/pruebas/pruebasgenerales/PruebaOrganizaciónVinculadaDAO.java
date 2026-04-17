package pruebasgenerales;

import accesodatos.ConexionBD;
import logica.dao.OrganizacionVinculadaDAO;
import logica.dto.OrganizacionVinculadaDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PruebaOrganizaciónVinculadaDAO {

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
        conexion.createStatement().execute("TRUNCATE TABLE OrganizacionVinculada");
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    private OrganizacionVinculadaDTO crearOrganizacionEjemplo() {
        return new OrganizacionVinculadaDTO(
                "ORG001",
                "Avengers",
                "Central Park"
        );
    }

    @Test
    public void pruebaAgregarOrganizacion() throws Exception {
        OrganizacionVinculadaDAO organizacionDAO = new OrganizacionVinculadaDAO();
        OrganizacionVinculadaDTO organizacionDTO = crearOrganizacionEjemplo();

        organizacionDAO.agregarOrganizacionVinculada(organizacionDTO);

        OrganizacionVinculadaDTO resultado = organizacionDAO.buscarOrganizacionVinculadaPorIdProyecto("ORG001");

        assertEquals("ORG001", resultado.getidOrganizacion());
    }

    @Test
    public void pruebaBuscarOrganizacionPorId() throws Exception {
        OrganizacionVinculadaDAO organizacionDAO = new OrganizacionVinculadaDAO();
        organizacionDAO.agregarOrganizacionVinculada(crearOrganizacionEjemplo());

        OrganizacionVinculadaDTO resultado = organizacionDAO.buscarOrganizacionVinculadaPorIdProyecto("ORG001");

        assertEquals("ORG001", resultado.getidOrganizacion());
    }


    @Test
    public void pruebaActualizarOrganizacion() throws Exception {
        OrganizacionVinculadaDAO organizacionDAO = new OrganizacionVinculadaDAO();
        OrganizacionVinculadaDTO organizacionDTO = crearOrganizacionEjemplo();
        organizacionDAO.agregarOrganizacionVinculada(organizacionDTO);

        organizacionDTO.setNombre("Justice League");
        organizacionDAO.actualizarOrganizacionVinculada(organizacionDTO);

        OrganizacionVinculadaDTO resultado = organizacionDAO.buscarOrganizacionVinculadaPorIdProyecto("ORG001");

        assertEquals("Justice League", resultado.getNombre());
    }

    @Test
    public void pruebaListarOrganizaciones() throws Exception {
        OrganizacionVinculadaDAO organizacionDAO = new OrganizacionVinculadaDAO();
        organizacionDAO.agregarOrganizacionVinculada(crearOrganizacionEjemplo());

        List<OrganizacionVinculadaDTO> lista = organizacionDAO.listarOrganizacionesVinculadas();

        assertFalse(lista.isEmpty());
    }


}
