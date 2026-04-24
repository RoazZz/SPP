package pruebasgenerales;

import accesodatos.ConexionBD;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.OrganizacionVinculadaDAO;
import logica.dto.OrganizacionVinculadaDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaOrganizaciónVinculadaDAO {

    private static OrganizacionVinculadaDAO organizacionVinculadaDAO;
    private OrganizacionVinculadaDTO organizacionVinculadaValida;
    private OrganizacionVinculadaDTO organizacionVinculadaSinNombre;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        organizacionVinculadaDAO = new OrganizacionVinculadaDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE OrganizacionVinculada");
            statement.execute("INSERT INTO OrganizacionVinculada (idOrganizacion, Nombre, Direccion) " +
                    "VALUES ('ORG999', 'Organizacion Maestra', 'Direccion Maestra')");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM OrganizacionVinculada WHERE idOrganizacion != 'ORG999'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        organizacionVinculadaValida = new OrganizacionVinculadaDTO(
                "ORG001",
                "Avengers",
                "Central Park"
        );
        organizacionVinculadaSinNombre = new OrganizacionVinculadaDTO(
                "ORG002",
                null,
                "Direccion invalida"
        );
    }

    @Test
    public void pruebaAgregarOrganizacionExitoso() throws Exception {
        boolean resultado = organizacionVinculadaDAO.agregarOrganizacionVinculada(organizacionVinculadaValida);
        assertTrue(resultado);
    }

    @Test
    public void pruebaListarOrganizacionesExitoso() throws Exception {
        List<OrganizacionVinculadaDTO> listaOrganizaciones = organizacionVinculadaDAO.listarOrganizacionesVinculadas();
        assertFalse(listaOrganizaciones.isEmpty());
    }

    @Test
    public void pruebaBuscarOrganizacionNoExistente() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () ->
                        organizacionVinculadaDAO.buscarOrganizacionVinculadaPorIdProyecto("ORG99"));
    }

}
