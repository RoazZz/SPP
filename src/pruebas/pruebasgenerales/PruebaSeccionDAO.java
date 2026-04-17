package pruebasgenerales;

import accesodatos.ConexionBD;
import logica.dao.SeccionDAO;
import logica.dto.SeccionDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PruebaSeccionDAO {

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
            comandoControl.execute("TRUNCATE TABLE seccion");
            comandoControl.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @Test
    public void pruebaAgregarBuscarSeccion() throws Exception {
        SeccionDAO seccionDAO = new SeccionDAO();
        SeccionDTO seccionDTO = new SeccionDTO(10, "Ingeniería de Software");

        seccionDAO.agregarSeccion(seccionDTO);
        SeccionDTO seccionRecuperada = seccionDAO.obtenerSeccionPorId(10);

        assertEquals("Ingeniería de Software", seccionRecuperada.getNombre(),
                "El nombre de la sección recuperada debe coincidir con el original");
    }

    @Test
    public void pruebaActualizarBuscarSeccion() throws Exception {
        SeccionDAO seccionDAO = new SeccionDAO();
        SeccionDTO seccionDTO = new SeccionDTO(1, "Nombre Original");
        seccionDAO.agregarSeccion(seccionDTO);

        String nombreNuevo = "Nombre Actualizado";
        seccionDTO.setNombre(nombreNuevo);
        seccionDAO.actualizarSeccion(seccionDTO);

        SeccionDTO seccionActualizada = seccionDAO.obtenerSeccionPorId(1);
        assertEquals(nombreNuevo, seccionActualizada.getNombre(),
                "El nombre de la sección debe haberse actualizado correctamente");
    }

    @Test
    public void pruebaObtenerTodasLasSecciones() throws Exception {
        SeccionDAO seccionDAO = new SeccionDAO();
        seccionDAO.agregarSeccion(new SeccionDTO(1, "Seccion A"));
        seccionDAO.agregarSeccion(new SeccionDTO(2, "Seccion B"));

        List<SeccionDTO> listaSecciones = seccionDAO.obtenerTodasLasSecciones();

        assertEquals(2, listaSecciones.size(), "La lista debería contener exactamente 2 secciones");
    }
}