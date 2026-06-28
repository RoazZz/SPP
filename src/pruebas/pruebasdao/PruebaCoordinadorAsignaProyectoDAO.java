package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.CoordinadorAsignaProyectoDAO;
import logica.dto.CoordinadorAsignaProyectoDTO;
import logica.dao.ProyectoDAO;
import logica.dto.ProyectoDTO;
import logica.enums.EstadoAsignacionProyecto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PruebaCoordinadorAsignaProyectoDAO {

    private static final String ID_ORGANIZACION_PRUEBA = "ORG920";
    private static final String NUMERO_PERSONAL_PRUEBA = "COORD920";
    private static int idProyectoPrueba;

    private static CoordinadorAsignaProyectoDAO asignaDAO;
    private CoordinadorAsignaProyectoDTO asignaValida;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        asignaDAO = new CoordinadorAsignaProyectoDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM organizacionvinculada WHERE idOrganizacion = '" + ID_ORGANIZACION_PRUEBA + "'");
            statement.execute("INSERT INTO organizacionvinculada (idOrganizacion, Nombre, Direccion) VALUES ('" + ID_ORGANIZACION_PRUEBA + "', 'Org Asig', 'Dir')");
            statement.execute("DELETE FROM usuario WHERE idUsuario = 9920");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (9920, 'CoordAsig', 'Ap', 'Am', 'clave', 'ACTIVO', 'COORDINADOR')");
            statement.execute("DELETE FROM coordinador WHERE NumeroDePersonal = '" + NUMERO_PERSONAL_PRUEBA + "'");
            statement.execute("INSERT INTO coordinador (NumeroDePersonal, idUsuario) VALUES ('" + NUMERO_PERSONAL_PRUEBA + "', 9920)");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        ProyectoDTO proyectoSemilla = new ProyectoDTO(0, ID_ORGANIZACION_PRUEBA, NUMERO_PERSONAL_PRUEBA, "ProyectoAsig", "Desc");
        new ProyectoDAO().agregarProyecto(proyectoSemilla);
        idProyectoPrueba = proyectoSemilla.getIdProyecto();
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM asigna WHERE NumeroDePersonal = '" + NUMERO_PERSONAL_PRUEBA + "'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        asignaValida = new CoordinadorAsignaProyectoDTO(NUMERO_PERSONAL_PRUEBA, idProyectoPrueba, EstadoAsignacionProyecto.EN_REVISION);
    }

    @Test
    public void pruebaInsertarAsignacionDeProyectoExitoso() throws Exception {
        asignaDAO.insertarAsignacionDeProyecto(asignaValida);
        List<CoordinadorAsignaProyectoDTO> asignacionesRecuperadas = asignaDAO.obtenerAsignacionDeProyectoPorNumeroDePersonal(NUMERO_PERSONAL_PRUEBA);
        assertFalse(asignacionesRecuperadas.isEmpty());
    }

    @Test
    public void pruebaActualizarAsignacionDeProyectoExitoso() throws Exception {
        asignaDAO.insertarAsignacionDeProyecto(asignaValida);
        asignaValida.setTipoEstado(EstadoAsignacionProyecto.VALIDADO);
        asignaDAO.actualizarAsignacionDeProyecto(asignaValida);
        List<CoordinadorAsignaProyectoDTO> asignacionesRecuperadas = asignaDAO.obtenerAsignacionDeProyectoPorNumeroDePersonal(NUMERO_PERSONAL_PRUEBA);
        assertFalse(asignacionesRecuperadas.isEmpty());
    }

    @Test
    public void pruebaObtenerTodasLasAsignacionesDeProyectoExitoso() throws Exception {
        asignaDAO.insertarAsignacionDeProyecto(asignaValida);
        List<CoordinadorAsignaProyectoDTO> asignacionesRecuperadas = asignaDAO.obtenerTodasLasAsignacionesDeProyecto();
        assertFalse(asignacionesRecuperadas.isEmpty());
    }

    @Test
    public void pruebaInsertarAsignacionDeProyectoExcepcionNumeroPersonalNulo() {
        asignaValida.setNumeroDePersonal(null);
        assertThrows(DAOExcepcion.class, () ->
                asignaDAO.insertarAsignacionDeProyecto(asignaValida));
    }
}