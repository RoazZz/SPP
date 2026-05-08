package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.PracticanteDAO;
import logica.dto.PracticanteDTO;
import logica.enums.GeneroDelPracticante;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaPracticanteDAO {

    private static PracticanteDAO practicanteDAO;
    private PracticanteDTO practicanteValido;
    private PracticanteDTO practicanteSinMatricula;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        practicanteDAO = new PracticanteDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE Practicante");
            statement.execute("TRUNCATE TABLE Usuario");
            statement.execute("TRUNCATE TABLE Seccion");
            statement.execute("INSERT INTO Seccion (idSeccion, Nombre) VALUES (1, 'Matutino')");
            statement.execute("INSERT INTO Usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                    "VALUES (999, 'Practicante', 'Maestro', 'Test', '123', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("INSERT INTO Practicante (idUsuario, Matricula, idSeccion, Semestre, Genero, Edad, LenguaIndigena) " +
                    "VALUES (999, 'S99999', 1, '5', 'MASCULINO', 20, false)");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM Practicante WHERE Matricula != 'S99999'");
            statement.execute("DELETE FROM Usuario WHERE idUsuario != 999");
            statement.execute("SET FOREIGN_KEY_ CHECKS = 1");
        }
        practicanteValido = new PracticanteDTO(
                0,
                "Jared",
                "Morales",
                "Tirado",
                "123",
                TipoEstado.ACTIVO,
                TipoDeUsuario.PRACTICANTE,
                "S24021",
                1,
                "5",
                GeneroDelPracticante.MASCULINO,
                20,
                false
        );
        practicanteSinMatricula = new PracticanteDTO(
                0,
                "Jared",
                "Morales",
                "Tirado",
                "123",
                TipoEstado.ACTIVO,
                TipoDeUsuario.PRACTICANTE,
                null,
                1,
                "5",
                GeneroDelPracticante.MASCULINO,
                20,
                false
        );
    }

    @Test
    public void pruebaAgregarPracticanteExitoso() throws Exception {
        practicanteDAO.agregarPracticante(practicanteValido);
        assertTrue(practicanteValido.getIdUsuario() > 0);
    }

    @Test
    public void pruebaListarPracticantesExitoso() throws Exception {
        List<PracticanteDTO> listaPracticantes = practicanteDAO.listarPracticantes();
        assertFalse(listaPracticantes.isEmpty());
    }

    @Test
    public void pruebaAgregarPracticanteExcepcionMatriculaNula() {
        assertThrows(DAOExcepcion.class, () -> practicanteDAO.agregarPracticante(practicanteSinMatricula));
    }
}
