package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.PlanDeActividadesDAO;
import logica.dto.PlanDeActividadesDTO;
import logica.dao.ProyectoDAO;
import logica.dto.ProyectoDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaPlanDeActividadesDAO {

    private static final String MATRICULA_PRUEBA = "S20009130";
    private static final String ID_ORGANIZACION_PRUEBA = "ORG913";
    private static final String NUMERO_PERSONAL_PRUEBA = "COORD913";
    private static int idProyectoPrueba;

    private static PlanDeActividadesDAO planDAO;
    private PlanDeActividadesDTO planValido;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        planDAO = new PlanDeActividadesDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM seccion WHERE idSeccion = 913");
            statement.execute("INSERT INTO seccion (idSeccion, Nombre) VALUES (913, 'Seccion Base')");
            statement.execute("DELETE FROM usuario WHERE idUsuario = 913");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (913, 'PracBase', 'Ap', 'Am', 'clave', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("DELETE FROM practicante WHERE Matricula = 'S20009130'");
            statement.execute("INSERT INTO practicante (Matricula, idSeccion, Semestre, Genero, Edad, LenguaIndigena, idUsuario) VALUES ('S20009130', 913, '5', 'MASCULINO', 22, 0, 913)");
            statement.execute("DELETE FROM organizacionvinculada WHERE idOrganizacion = '" + ID_ORGANIZACION_PRUEBA + "'");
            statement.execute("INSERT INTO organizacionvinculada (idOrganizacion, Nombre, Direccion) VALUES ('" + ID_ORGANIZACION_PRUEBA + "', 'Org Plan', 'Dir')");
            statement.execute("DELETE FROM usuario WHERE idUsuario = 9913");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (9913, 'CoordPlan', 'Ap', 'Am', 'clave', 'ACTIVO', 'COORDINADOR')");
            statement.execute("DELETE FROM coordinador WHERE NumeroDePersonal = '" + NUMERO_PERSONAL_PRUEBA + "'");
            statement.execute("INSERT INTO coordinador (NumeroDePersonal, idUsuario) VALUES ('" + NUMERO_PERSONAL_PRUEBA + "', 9913)");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        ProyectoDTO proyectoSemilla = new ProyectoDTO(0, ID_ORGANIZACION_PRUEBA, NUMERO_PERSONAL_PRUEBA, "ProyectoPlan", "Desc");
        new ProyectoDAO().agregarProyecto(proyectoSemilla);
        idProyectoPrueba = proyectoSemilla.getIdProyecto();
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM plandeactividades WHERE Matricula = '" + MATRICULA_PRUEBA + "'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        planValido = new PlanDeActividadesDTO(0, MATRICULA_PRUEBA, idProyectoPrueba, "Descripcion del plan");
    }

    @Test
    public void pruebaAgregarPlanDeActividadesExitoso() throws Exception {
        PlanDeActividadesDTO planGuardado = planDAO.agregarPlanDeActividades(planValido);
        assertTrue(planGuardado.getIdPlanActividades() > 0);
    }

    @Test
    public void pruebaActualizarPlanDeActividadesExitoso() throws Exception {
        PlanDeActividadesDTO planGuardado = planDAO.agregarPlanDeActividades(planValido);
        planGuardado.setDescripcion("Descripcion modificada");
        boolean resultado = planDAO.actualizarPlanDeActividades(planGuardado);
        assertTrue(resultado);
    }

    @Test
    public void pruebaObtenerPlanesDeActividadesPorMatriculaExitoso() throws Exception {
        planDAO.agregarPlanDeActividades(planValido);
        List<PlanDeActividadesDTO> planesRecuperados = planDAO.obtenerPlanesDeActividadesPorMatricula(MATRICULA_PRUEBA);
        assertFalse(planesRecuperados.isEmpty());
    }

    @Test
    public void pruebaObtenerTodosLosPlanesDeActividadesExitoso() throws Exception {
        planDAO.agregarPlanDeActividades(planValido);
        List<PlanDeActividadesDTO> planesRecuperados = planDAO.obtenerTodosLosPlanesDeActividades();
        assertFalse(planesRecuperados.isEmpty());
    }

    @Test
    public void pruebaObtenerPlanDeActividadesPorIdExitoso() throws Exception {
        PlanDeActividadesDTO planGuardado = planDAO.agregarPlanDeActividades(planValido);
        PlanDeActividadesDTO planRecuperado = planDAO.obtenerPlanDeActividadesPorId(planGuardado.getIdPlanActividades());
        assertTrue(planRecuperado.getIdPlanActividades() > 0);
    }

    @Test
    public void pruebaAgregarPlanDeActividadesExcepcionMatriculaNula() {
        planValido.setMatricula(null);
        assertThrows(DAOExcepcion.class, () ->
                planDAO.agregarPlanDeActividades(planValido));
    }
}