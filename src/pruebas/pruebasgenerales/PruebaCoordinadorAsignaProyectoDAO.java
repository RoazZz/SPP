package pruebasgenerales;

import accesodatos.ConexionBD;
import logica.dto.CoordinadorAsignaProyectoDTO;
import logica.enums.TipoEstado;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;

public class PruebaCoordinadorAsignaProyectoDAO {

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
    }

    // private CoordinadorAsignaProyectoDTO crearAsignacionProyecto() {
        // return new CoordinadorAsignaProyectoDTO(
                // "00000",
               //"",
                // TipoEstado.ACTIVO
       // );
   // }
}
