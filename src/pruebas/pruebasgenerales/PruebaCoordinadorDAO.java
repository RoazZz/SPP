package pruebasgenerales;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.CoordinadorDAO;
import logica.dto.CoordinadorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaCoordinadorDAO {

    private static CoordinadorDAO coordinadorDAO;
    private CoordinadorDTO coordinadorValido;
    private CoordinadorDTO coordinadorSinNumeroPersonal;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        coordinadorDAO = new CoordinadorDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE Coordinador");
            statement.execute("TRUNCATE TABLE Usuario");
            statement.execute("INSERT INTO Usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                    "VALUES (999, 'Coordinador', 'Maestro', 'Test', '123', 'ACTIVO', 'COORDINADOR')");
            statement.execute("INSERT INTO Coordinador (NumeroDePersonal, idUsuario) " +
                    "VALUES ('COORD999', 999)");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM Coordinador WHERE NumeroDePersonal != 'COORD999'");
            statement.execute("DELETE FROM Usuario WHERE idUsuario != 999");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        coordinadorValido = new CoordinadorDTO(
                0,
                "Jared",
                "Morales",
                "Tirado",
                "123",
                TipoEstado.ACTIVO,
                TipoDeUsuario.COORDINADOR,
                "25110"
        );
        coordinadorSinNumeroPersonal = new CoordinadorDTO(
                0,
                "Jared",
                "Morales",
                "Tirado",
                "123",
                TipoEstado.ACTIVO,
                TipoDeUsuario.COORDINADOR,
                null
        );
    }


    @Test
    public void pruebaAgregarCoordinadorExitoso() throws Exception {
        coordinadorDAO.agregarCoordinador(coordinadorValido);
        assertTrue(coordinadorValido.getIdUsuario() > 0);
    }

    @Test
    public void pruebaListarCoordinadoresExitoso() throws Exception {
        List<CoordinadorDTO> lista = coordinadorDAO.listarCoordinador();
        assertFalse(lista.isEmpty());
    }

    @Test
    public void pruebaAgregarCoordinadorExcepcionNumeroPersonalNulo() {
        assertThrows(DAOExcepcion.class, () ->
                        coordinadorDAO.agregarCoordinador(coordinadorSinNumeroPersonal));
    }
}
