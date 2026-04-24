package pruebasgenerales;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.ActividadDAO;
import logica.dto.ActividadDTO;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class PruebaActividadDAO {

    private static ActividadDAO actividadDAO;
    private ActividadDTO actividadValida;
    private ActividadDTO actividadSinMatricula;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        actividadDAO = new ActividadDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE Actividad");
            statement.execute("TRUNCATE TABLE practicante");
            statement.execute("TRUNCATE TABLE usuario");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                    "VALUES (1, 'Ana', 'Perez', 'Lopez', '123', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("INSERT INTO practicante (idUsuario, Matricula, idSeccion, Semestre, Genero, Edad, LenguaIndigena) " +
                    "VALUES (1, 'S24021', 1, '5', 'FEMENINO', 20, false)");
            statement.execute("INSERT INTO Actividad (idActividad, Matricula, Nombre, Descripcion, Fecha) " +
                    "VALUES (999, 'S24021', 'Actividad Maestra', 'Descripcion maestra', '2026-04-17')");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }


    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM Actividad WHERE idActividad != 999");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        actividadValida = new ActividadDTO(
                0,
                "S24021",
                "Actividad Nueva",
                "Descripcion de prueba",
                java.sql.Date.valueOf("2026-04-17")
        );
        actividadSinMatricula = new ActividadDTO(
                0,
                null,
                "Actividad Invalida",
                "Descripcion invalida",
                java.sql.Date.valueOf("2026-04-17")
        );
    }

    @Test
    public void pruebaAgregarActividadExitoso() throws Exception {
        boolean resultado = actividadDAO.agregarActividad(actividadValida);
        assertTrue(resultado);
    }

    @Test
    public void pruebaAgregarActividadExcepcionMatriculaNula() {
        assertThrows(DAOExcepcion.class, () -> actividadDAO.agregarActividad(actividadSinMatricula));
    }

    @Test
    public void pruebaBuscarActividadNoExistente() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> actividadDAO.buscarActividadPorIdActividad(10));
    }
}
