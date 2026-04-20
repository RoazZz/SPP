package pruebasgenerales;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.PlanDeActividadesDAO;
import logica.dto.PlanDeActividadesDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaPlanDeActividadesDAO {
    private static PlanDeActividadesDAO planDeActividadesDAO;
    private PlanDeActividadesDTO dtoParaAgregar;
    private PlanDeActividadesDTO dtoInvalido;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        planDeActividadesDAO = new PlanDeActividadesDAO();

        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE plandeactividades");
            statement.execute("TRUNCATE TABLE proyecto");
            statement.execute("TRUNCATE TABLE practicante");
            statement.execute("TRUNCATE TABLE usuario");
            statement.execute("TRUNCATE TABLE seccion");
            statement.execute("TRUNCATE TABLE profesor");
            statement.execute("TRUNCATE TABLE organizacionvinculada");

            statement.execute("INSERT INTO seccion VALUES (1, 'Sistemas')");

            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                    "VALUES (1, 'Juan', 'Perez', 'Admin', 'pass', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("INSERT INTO practicante VALUES ('S21012345', 1, '7', 'MASCULINO', 21, 0, 1)");

            statement.execute("INSERT INTO organizacionvinculada VALUES ('ORG01', 'Tech Corp', 'Av. Siempre Viva')");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                    "VALUES (2, 'Maria', 'Gomez', 'Admin', 'pass', 'ACTIVO', 'PROFESOR')");
            statement.execute("INSERT INTO profesor VALUES ('123456789012', 'MATUTINO', 2)");

            statement.execute("INSERT INTO proyecto (idProyecto, idOrganizacion, NumeroDePersonal, Nombre, Descripcion) " +
                    "VALUES (1, 'ORG01', '123456789012', 'Proyecto Maestro', 'Desc')");
            statement.execute("INSERT INTO proyecto (idProyecto, idOrganizacion, NumeroDePersonal, Nombre, Descripcion) " +
                    "VALUES (2, 'ORG01', '123456789012', 'Proyecto Insertar', 'Desc')");

            statement.execute("INSERT INTO plandeactividades (idPlanActividades, Matricula, idProyecto, Descripcion) " +
                    "VALUES (999, 'S21012345', 1, 'Plan Maestro Inicial')");

            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM plandeactividades WHERE idPlanActividades != 999");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }

        dtoParaAgregar = new PlanDeActividadesDTO(0, "S21012345", 2, "Plan de actividades nuevo");
        dtoInvalido = new PlanDeActividadesDTO(0, null, 1, "Error");
    }

    @Test
    public void pruebaAgregarPlanDeActividadesExitoso() throws Exception {
        PlanDeActividadesDTO resultado = planDeActividadesDAO.agregarPlanDeActividades(dtoParaAgregar);
        assertNotNull(resultado);
    }

    @Test
    public void pruebaObtenerPlanDeActividadesPorIdExitoso() throws Exception {
        PlanDeActividadesDTO recuperado = planDeActividadesDAO.obtenerPlanDeActividadesPorId(999);
        assertEquals("Plan Maestro Inicial", recuperado.getDescripcion());
    }

    @Test
    public void pruebaObtenerPlanesDeActividadesPorMatriculaExitoso() throws Exception {
        List<PlanDeActividadesDTO> lista = planDeActividadesDAO.obtenerPlanesDeActividadesPorMatricula("S21012345");
        assertFalse(lista.isEmpty());
    }

    @Test
    public void pruebaAgregarPlanExcepcionMatriculaNula() {
        assertThrows(DAOExcepcion.class, () -> planDeActividadesDAO.agregarPlanDeActividades(dtoInvalido));
    }

    @Test
    public void pruebaActualizarPlanExcepcionConexionCerrada() throws Exception {
        ConexionBD.obtenerInstancia().obtenerConexion().close();
        assertThrows(DAOExcepcion.class, () -> planDeActividadesDAO.actualizarPlanDeActividades(dtoParaAgregar));
        ConexionBD.reset();
        planDeActividadesDAO = new PlanDeActividadesDAO();
    }
}