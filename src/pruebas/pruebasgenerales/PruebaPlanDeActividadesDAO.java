package pruebasgenerales;

import accesodatos.ConexionBD;
import logica.dao.PlanDeActividadesDAO;
import logica.dto.PlanDeActividadesDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class PruebaPlanDeActividadesDAO {

    @BeforeAll
    static void configurarConexion() {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
    }

    @BeforeEach
    void prepararEntorno() throws Exception {
        limpiarTablas();
        insertarDatosNecesarios();
    }

    @AfterEach
    void finalizarPrueba() throws Exception {
        limpiarTablas();
    }

    void limpiarTablas() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement comandoControl = conexion.createStatement()) {
            comandoControl.execute("SET FOREIGN_KEY_CHECKS = 0");
            comandoControl.execute("TRUNCATE TABLE plandeactividades");
            comandoControl.execute("TRUNCATE TABLE proyecto");
            comandoControl.execute("TRUNCATE TABLE profesor");
            comandoControl.execute("TRUNCATE TABLE organizacionvinculada");
            comandoControl.execute("TRUNCATE TABLE practicante");
            comandoControl.execute("TRUNCATE TABLE usuario");
            comandoControl.execute("TRUNCATE TABLE seccion");
            comandoControl.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    private void insertarDatosNecesarios() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();

        ejecutarUpdate(conexion, "INSERT INTO seccion VALUES (1, 'Sistemas')");
        ejecutarUpdate(conexion, "INSERT INTO usuario VALUES (1, 'Juan', 'Perez', 'M', 'pass', 'ACTIVO', 'PRACTICANTE')");
        ejecutarUpdate(conexion, "INSERT INTO practicante VALUES ('S21012345', 1, '7', 'MASCULINO', 21, 0, 1)");

        ejecutarUpdate(conexion, "INSERT INTO organizacionvinculada VALUES ('ORG01', 'Tech Corp', 'Av. Siempre Viva')");
        ejecutarUpdate(conexion, "INSERT INTO usuario VALUES (2, 'Maria', 'Gomez', 'L', 'pass', 'ACTIVO', 'PROFESOR')");
        ejecutarUpdate(conexion, "INSERT INTO profesor VALUES ('123456789012', 'MATUTINO', 2);");

        ejecutarUpdate(conexion, "INSERT INTO proyecto (idProyecto, idOrganizacion, NumeroDePersonal, Nombre, Descripcion) VALUES (1, 'ORG01', '123456789012', 'Sistema SPP', 'Desarrollo de software')");
    }

    private void ejecutarUpdate(Connection conexion, String sql) throws Exception {
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.executeUpdate();
        }
    }

    @Test
    public void pruebaAgregarBuscarPlanDeActividades() throws Exception {
        PlanDeActividadesDAO planDAO = new PlanDeActividadesDAO();
        PlanDeActividadesDTO planDTO = new PlanDeActividadesDTO(0, "S21012345", 1, "Plan de desarrollo Java");

        planDAO.agregarPlanDeActividades(planDTO);
        PlanDeActividadesDTO recuperado = planDAO.obtenerPlanDeActividadesPorId(planDTO.getIdplanActividades());

        assertNotNull(recuperado, "El plan debería existir en la base de datos");
        assertEquals(planDTO.getDescripcion(), recuperado.getDescripcion(), "La descripción debe coincidir");
    }

    @Test
    public void pruebaActualizarBuscarPlanDeActividades() throws Exception {
        PlanDeActividadesDAO planDAO = new PlanDeActividadesDAO();
        PlanDeActividadesDTO planDTO = new PlanDeActividadesDTO(0, "S21012345", 1, "Original");
        planDAO.agregarPlanDeActividades(planDTO);

        planDTO.setDescripcion("Actualizada");
        planDAO.actualizarPlanDeActividades(planDTO);

        PlanDeActividadesDTO actualizado = planDAO.obtenerPlanDeActividadesPorId(planDTO.getIdplanActividades());
        assertEquals("Actualizada", actualizado.getDescripcion());
    }

    @Test
    public void pruebaObtenerPlanesPorMatricula() throws Exception {
        PlanDeActividadesDAO planDAO = new PlanDeActividadesDAO();
        planDAO.agregarPlanDeActividades(new PlanDeActividadesDTO(0, "S21012345", 1, "Plan 1"));

        List<PlanDeActividadesDTO> lista = planDAO.obtenerPlanesDeActividadesPorMatricula("S21012345");
        assertFalse(lista.isEmpty(), "Debería encontrar al menos un plan para esta matrícula");
    }
}