package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.CoordinadorAsignaProyectoDAO;
import logica.dto.CoordinadorAsignaProyectoDTO;
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

    private static CoordinadorAsignaProyectoDAO coordinadorAsignaProyectoDAO;
    private CoordinadorAsignaProyectoDTO asignacionValida;
    private CoordinadorAsignaProyectoDTO asignacionSinNumeroDePersonal;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        coordinadorAsignaProyectoDAO = new CoordinadorAsignaProyectoDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE asigna");
            statement.execute("TRUNCATE TABLE proyecto");
            statement.execute("TRUNCATE TABLE coordinador");
            statement.execute("TRUNCATE TABLE organizacionvinculada");
            statement.execute("TRUNCATE TABLE profesor");
            statement.execute("TRUNCATE TABLE usuario");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                    "VALUES (1, 'Coordinador', 'Maestro', 'Test', '123', 'ACTIVO', 'COORDINADOR')");
            statement.execute("INSERT INTO coordinador (NumeroDePersonal, idUsuario) " +
                    "VALUES ('COORD001', 1)");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                    "VALUES (2, 'Profesor', 'Maestro', 'Test', '123', 'ACTIVO', 'PROFESOR')");
            statement.execute("INSERT INTO profesor (NumeroDePersonal, Turno, idUsuario) " +
                    "VALUES ('PROF001', 'MATUTINO', 2)");
            statement.execute("INSERT INTO organizacionvinculada (idOrganizacion, Nombre, Direccion) " +
                    "VALUES ('ORG001', 'Organizacion Maestra', 'Direccion Maestra')");
            statement.execute("INSERT INTO proyecto (idProyecto, idOrganizacion, NumeroDePersonal, Nombre, Descripcion) " +
                    "VALUES (999, 'ORG001', 'PROF001', 'Proyecto Maestro', 'Descripcion maestra')");
            statement.execute("INSERT INTO proyecto (idProyecto, idOrganizacion, NumeroDePersonal, Nombre, Descripcion) " +
                    "VALUES (998, 'ORG001', 'PROF001', 'Proyecto Para Insertar', 'Descripcion insertar')");
            statement.execute("INSERT INTO asigna (NumeroDePersonal, idProyecto, Estado) " +
                    "VALUES ('COORD001', 999, 'EN REVISION')");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM asigna WHERE idProyecto != 999");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        asignacionValida = new CoordinadorAsignaProyectoDTO(
                "COORD001",
                998,
                EstadoAsignacionProyecto.valueOf("VALIDADO")
        );
        asignacionSinNumeroDePersonal = new CoordinadorAsignaProyectoDTO(
                null,
                998,
                EstadoAsignacionProyecto.valueOf("VALIDADO")
        );
    }

    @Test
    public void pruebaInsertarAsignacionExitoso() throws Exception {
        coordinadorAsignaProyectoDAO.insertarAsignacionDeProyecto(asignacionValida);
        List<CoordinadorAsignaProyectoDTO> listaProyectosAsignados = coordinadorAsignaProyectoDAO.obtenerAsignacionDeProyectoPorNumeroDePersonal("COORD001");
        assertFalse(listaProyectosAsignados.isEmpty());
    }

    @Test
    public void pruebaObtenerTodasLasAsignacionesExitoso() throws Exception {
        List<CoordinadorAsignaProyectoDTO> listaProyectosAsignados = coordinadorAsignaProyectoDAO
                .obtenerTodasLasAsignacionesDeProyecto();
        assertFalse(listaProyectosAsignados.isEmpty());
    }

    @Test
    public void pruebaInsertarAsignacionExcepcionNumeroDePersonalNulo() {
        assertThrows(DAOExcepcion.class, () ->
                        coordinadorAsignaProyectoDAO.insertarAsignacionDeProyecto(asignacionSinNumeroDePersonal));
    }




}
