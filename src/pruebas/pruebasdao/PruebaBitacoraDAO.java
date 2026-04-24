package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.BitacoraDAO;
import logica.dto.BitacoraDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PruebaBitacoraDAO {

    private static BitacoraDAO bitacoraDAO;
    private BitacoraDTO bitacoraValida;
    private BitacoraDTO bitacoraSinMatricula;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        bitacoraDAO = new BitacoraDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE Bitacora");
            statement.execute("TRUNCATE TABLE practicante");
            statement.execute("TRUNCATE TABLE usuario");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                    "VALUES (1, 'Ana', 'Perez', 'Lopez', '123', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("INSERT INTO practicante (idUsuario, Matricula, idSeccion, Semestre, Genero, Edad, LenguaIndigena) " +
                    "VALUES (1, 'S24021', 1, '5', 'FEMENINO', 20, false)");
            statement.execute("INSERT INTO Bitacora (idRegistro, Matricula, Fecha_Hora, TipoEvento, Descripcion) " +
                    "VALUES (999, 'S24021', '2026-04-17 10:30:00', 'LOGIN', 'Bitacora maestra')");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM Bitacora WHERE idRegistro != 999");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        bitacoraValida = new BitacoraDTO(
                0,
                "S24021",
                "LOGIN",
                LocalDateTime.of(2026, 4, 17, 10, 30, 0),
                "El practicante inició sesión"
        );
        bitacoraSinMatricula = new BitacoraDTO(
                0,
                null,
                "LOGIN",
                LocalDateTime.of(2026, 4, 17, 10, 30, 0),
                "Bitacora invalida"
        );
    }

    @Test
    public void pruebaAgregarBitacoraExitoso() throws Exception {
        boolean resultado = bitacoraDAO.agregarBitacora(bitacoraValida);
        assertTrue(resultado);
    }

    @Test
    public void pruebaAgregarBitacoraExcepcionMatriculaNula() {
        assertThrows(DAOExcepcion.class, () -> bitacoraDAO.agregarBitacora(bitacoraSinMatricula));
    }

    @Test
    public void pruebaBuscarBitacoraMatriculaNoExistente() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> bitacoraDAO.buscarBitacoraPorMatricula("10"));
    }


}
