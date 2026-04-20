package pruebasgenerales;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.SolicitaProyectoDAO;
import logica.dto.SolicitaProyectoDTO;
import logica.enums.TipoEstadoSolicitud;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaSolicitaProyectoDAO {
    private static SolicitaProyectoDAO solicitaProyectoDAO;
    private SolicitaProyectoDTO dtoParaAgregar;
    private SolicitaProyectoDTO dtoInvalido;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        solicitaProyectoDAO = new SolicitaProyectoDAO();

        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE solicita");
            statement.execute("TRUNCATE TABLE proyecto");
            statement.execute("TRUNCATE TABLE practicante");
            statement.execute("TRUNCATE TABLE usuario");
            statement.execute("TRUNCATE TABLE seccion");
            statement.execute("TRUNCATE TABLE profesor");
            statement.execute("TRUNCATE TABLE organizacionvinculada");

            statement.execute("INSERT INTO seccion VALUES (1, 'Sistemas')");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (1, 'Pepe', 'Grillo', 'Admin', 'pass', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("INSERT INTO practicante VALUES ('S123', 1, '7', 'MASCULINO', 21, 0, 1)");
            statement.execute("INSERT INTO organizacionvinculada VALUES ('ORG01', 'UV', 'Calle 1')");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (2, 'Dra', 'Z', 'Admin', 'pass', 'ACTIVO', 'PROFESOR')");
            statement.execute("INSERT INTO profesor VALUES ('999', 'MIXTO', 2)");

            // Proyecto 50 para consultas (Maestro)
            statement.execute("INSERT INTO proyecto (idProyecto, idOrganizacion, NumeroDePersonal, Nombre, Descripcion) VALUES (50, 'ORG01', '999', 'Proyecto Maestro', 'Desc')");
            // PROYECTO 60 para que el test de insertar no choque con la llave primaria
            statement.execute("INSERT INTO proyecto (idProyecto, idOrganizacion, NumeroDePersonal, Nombre, Descripcion) VALUES (60, 'ORG01', '999', 'Proyecto Para Insertar', 'Desc')");

            statement.execute("INSERT INTO solicita (Matricula, idProyecto, EstadoProyecto, Periodo) VALUES ('S123', 50, 'PENDIENTE', 'MAESTRO-2026')");

            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM solicita WHERE Periodo != 'MAESTRO-2026'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }

        // Ahora usamos el Proyecto 60, así no hay duplicados con el 50
        dtoParaAgregar = new SolicitaProyectoDTO("S123", 60, TipoEstadoSolicitud.PENDIENTE, "NUEVO-2026");
        dtoInvalido = new SolicitaProyectoDTO(null, 50, TipoEstadoSolicitud.PENDIENTE, "ERROR");
    }

    @Test
    public void pruebaInsertarSolicitudProyectoExitoso() throws Exception {
        SolicitaProyectoDTO resultado = solicitaProyectoDAO.insertarSolicitudProyecto(dtoParaAgregar);
        assertNotNull(resultado);
    }

    @Test
    public void pruebaObtenerSolicitudesPorMatriculaExitoso() throws Exception {
        List<SolicitaProyectoDTO> lista = solicitaProyectoDAO.obtenerSolicitudesProyectoPorMatricula("S123");
        assertEquals(50, lista.get(0).getIdProyecto());
    }

    @Test
    public void pruebaInsertarSolicitudExcepcionMatriculaNula() {
        assertThrows(DAOExcepcion.class, () -> solicitaProyectoDAO.insertarSolicitudProyecto(dtoInvalido));
    }

    @Test
    public void pruebaActualizarSolicitudExcepcionConexionCerrada() throws Exception {
        ConexionBD.obtenerInstancia().obtenerConexion().close();
        assertThrows(DAOExcepcion.class, () -> solicitaProyectoDAO.actualizarSolicitudProyecto(dtoParaAgregar));
        ConexionBD.reset();
        solicitaProyectoDAO = new SolicitaProyectoDAO();
    }
}