package pruebasgenerales;

import accesodatos.ConexionBD;
import logica.dao.ProyectoDAO;
import logica.dto.ProyectoDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaProyectoDAO {

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
        conexion.createStatement().execute("TRUNCATE TABLE Proyecto");
        conexion.createStatement().execute("TRUNCATE TABLE Profesor");
        conexion.createStatement().execute("TRUNCATE TABLE OrganizacionVinculada");
        conexion.createStatement().execute("TRUNCATE TABLE Usuario");
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @BeforeEach
    void insertarDatosPrevios() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0");
        conexion.createStatement().execute(
                "INSERT INTO Usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                        "VALUES (1, 'Jared', 'Morales', 'Tirado', '123', 'ACTIVO', 'COORDINADOR')"
        );
        conexion.createStatement().execute(
                "INSERT INTO Profesor (idUsuario, NumeroDePersonal, Turno) " +
                        "VALUES (1, '00000', 'MATUTINO')"
        );
        conexion.createStatement().execute(
                "INSERT INTO OrganizacionVinculada (idOrganizacion, Nombre, Direccion) " +
                        "VALUES ('ORG001', 'Ejemplooo', 'Calle FEI 12')"
        );
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    private ProyectoDTO crearProyectoEjemplo() {
        return new ProyectoDTO(
                0,
                "ORG001",
                "00000",
                "Proyecto FEI",
                "Esta descripcion es un ejmploooo."
        );
    }

    @Test
    public void pruebaAgregarProyecto() throws Exception {
        ProyectoDAO proyectoDAO = new ProyectoDAO();
        ProyectoDTO proyectoDTO = crearProyectoEjemplo();

        proyectoDAO.agregarProyecto(proyectoDTO);

        assertTrue(proyectoDTO.getIdProyecto() > 0);
    }

    @Test
    public void pruebaBuscarProyectoPorId() throws Exception {
        ProyectoDAO proyectoDAO = new ProyectoDAO();
        ProyectoDTO proyectoDTO = crearProyectoEjemplo();
        proyectoDAO.agregarProyecto(proyectoDTO);

        ProyectoDTO resultado = proyectoDAO.buscarProyectoPorIdProyecto(proyectoDTO.getIdProyecto());

        assertEquals("Proyecto FEI", resultado.getNombre());
    }

    @Test
    public void pruebaActualizarProyecto() throws Exception {
        ProyectoDAO proyectoDAO = new ProyectoDAO();
        ProyectoDTO proyectoDTO = crearProyectoEjemplo();
        proyectoDAO.agregarProyecto(proyectoDTO);

        proyectoDTO.setNombre("Proyecto FEI 2");
        proyectoDAO.actualizarProyecto(proyectoDTO);

        ProyectoDTO resultado = proyectoDAO.buscarProyectoPorIdProyecto(proyectoDTO.getIdProyecto());

        assertEquals("Proyecto FEI 2", resultado.getNombre());
    }

    @Test
    public void pruebaListarProyectos() throws Exception {
        ProyectoDAO proyectoDAO = new ProyectoDAO();
        proyectoDAO.agregarProyecto(crearProyectoEjemplo());

        List<ProyectoDTO> lista = proyectoDAO.listarProyectos();

        assertFalse(lista.isEmpty());
    }








}
