package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.SolicitaProyectoDAO;
import logica.dto.SolicitaProyectoDTO;
import logica.dao.ProyectoDAO;
import logica.dto.ProyectoDTO;
import logica.enums.TipoEstadoSolicitud;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaSolicitaProyectoDAO {

    private static final String MATRICULA_PRUEBA = "S20009150";
    private static final String ID_ORGANIZACION_PRUEBA = "ORG915";
    private static final String NUMERO_PERSONAL_PRUEBA = "COORD915";
    private static final String PERIODO_PRUEBA = "FEB-JUL 2026";
    private static int idProyectoPrueba;

    private static SolicitaProyectoDAO solicitaDAO;
    private SolicitaProyectoDTO solicitaValida;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        solicitaDAO = new SolicitaProyectoDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM seccion WHERE idSeccion = 915");
            statement.execute("INSERT INTO seccion (idSeccion, Nombre) VALUES (915, 'Seccion Base')");
            statement.execute("DELETE FROM usuario WHERE idUsuario = 915");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (915, 'PracBase', 'Ap', 'Am', 'clave', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("DELETE FROM practicante WHERE Matricula = 'S20009150'");
            statement.execute("INSERT INTO practicante (Matricula, idSeccion, Semestre, Genero, Edad, LenguaIndigena, idUsuario) VALUES ('S20009150', 915, '5', 'MASCULINO', 22, 0, 915)");
            statement.execute("DELETE FROM organizacionvinculada WHERE idOrganizacion = '" + ID_ORGANIZACION_PRUEBA + "'");
            statement.execute("INSERT INTO organizacionvinculada (idOrganizacion, Nombre, Direccion) VALUES ('" + ID_ORGANIZACION_PRUEBA + "', 'Org Sol', 'Dir')");
            statement.execute("DELETE FROM usuario WHERE idUsuario = 9915");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (9915, 'CoordSol', 'Ap', 'Am', 'clave', 'ACTIVO', 'COORDINADOR')");
            statement.execute("DELETE FROM coordinador WHERE NumeroDePersonal = '" + NUMERO_PERSONAL_PRUEBA + "'");
            statement.execute("INSERT INTO coordinador (NumeroDePersonal, idUsuario) VALUES ('" + NUMERO_PERSONAL_PRUEBA + "', 9915)");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        ProyectoDTO proyectoSemilla = new ProyectoDTO(0, ID_ORGANIZACION_PRUEBA, NUMERO_PERSONAL_PRUEBA, "ProyectoSol", "Desc");
        new ProyectoDAO().agregarProyecto(proyectoSemilla);
        idProyectoPrueba = proyectoSemilla.getIdProyecto();
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM solicita WHERE Matricula = '" + MATRICULA_PRUEBA + "'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        solicitaValida = new SolicitaProyectoDTO(MATRICULA_PRUEBA, idProyectoPrueba, TipoEstadoSolicitud.PENDIENTE, PERIODO_PRUEBA, 1);
    }

    @Test
    public void pruebaInsertarSolicitudProyectoExitoso() throws Exception {
        SolicitaProyectoDTO solicitudGuardada = solicitaDAO.insertarSolicitudProyecto(solicitaValida);
        assertTrue(solicitudGuardada.getPrioridad() > 0);
    }

    @Test
    public void pruebaActualizarSolicitudProyectoExitoso() throws Exception {
        solicitaDAO.insertarSolicitudProyecto(solicitaValida);
        solicitaValida.setTipoEstadoSolicitud(TipoEstadoSolicitud.ACEPTADO);
        boolean resultado = solicitaDAO.actualizarSolicitudProyecto(solicitaValida);
        assertTrue(resultado);
    }

    @Test
    public void pruebaObtenerSolicitudesProyectoPorMatriculaExitoso() throws Exception {
        solicitaDAO.insertarSolicitudProyecto(solicitaValida);
        List<SolicitaProyectoDTO> solicitudesRecuperadas = solicitaDAO.obtenerSolicitudesProyectoPorMatricula(MATRICULA_PRUEBA);
        assertFalse(solicitudesRecuperadas.isEmpty());
    }

    @Test
    public void pruebaObtenerSolicitudesProyectoPorPeriodoExitoso() throws Exception {
        solicitaDAO.insertarSolicitudProyecto(solicitaValida);
        List<SolicitaProyectoDTO> solicitudesRecuperadas = solicitaDAO.obtenerSolicitudesProyectoPorPeriodo(PERIODO_PRUEBA);
        assertFalse(solicitudesRecuperadas.isEmpty());
    }

    @Test
    public void pruebaObtenerTodasLasSolicitudesProyectoExitoso() throws Exception {
        solicitaDAO.insertarSolicitudProyecto(solicitaValida);
        List<SolicitaProyectoDTO> solicitudesRecuperadas = solicitaDAO.obtenerTodasLasSolicitudesProyecto();
        assertFalse(solicitudesRecuperadas.isEmpty());
    }

    @Test
    public void pruebaInsertarSolicitudProyectoExcepcionMatriculaNula() {
        solicitaValida.setMatricula(null);
        assertThrows(DAOExcepcion.class, () ->
                solicitaDAO.insertarSolicitudProyecto(solicitaValida));
    }
}