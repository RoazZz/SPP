package pruebasgenerales;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.BitacoraPSPDAO;
import logica.dto.BitacoraPSPDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaBitacoraPSPDAO {
    private static BitacoraPSPDAO bitacoraPSPDAO;
    private BitacoraPSPDTO dtoParaAgregar;
    private BitacoraPSPDTO dtoInvalido;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        bitacoraPSPDAO = new BitacoraPSPDAO();

        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE bitacorapsp");
            statement.execute("TRUNCATE TABLE practicante");
            statement.execute("TRUNCATE TABLE usuario");
            statement.execute("TRUNCATE TABLE seccion");

            statement.execute("INSERT INTO seccion VALUES (1, 'Sistemas')");

            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                    "VALUES (1, 'Juan', 'Perez', 'Admin', 'pass123', 'ACTIVO', 'PRACTICANTE')");

            statement.execute("INSERT INTO practicante VALUES ('S21012345', 1, '7', 'MASCULINO', 21, 0, 1)");

            statement.execute("INSERT INTO bitacorapsp (idBitacoraPSP, Matricula, Fecha) " +
                    "VALUES (999, 'S21012345', '2026-04-20')");

            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM bitacorapsp WHERE idBitacoraPSP != 999");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }

        dtoParaAgregar = new BitacoraPSPDTO(0, "S21012345", LocalDate.now());
        dtoInvalido = new BitacoraPSPDTO(0, null, LocalDate.now());
    }

    @Test
    public void pruebaAgregarBitacoraPSPExitoso() throws Exception {
        BitacoraPSPDTO resultado = bitacoraPSPDAO.agregarBitacoraPSP(dtoParaAgregar);
        assertNotNull(resultado);
    }

    @Test
    public void pruebaBuscarBitacoraPSPPorIdExitoso() throws Exception {
        BitacoraPSPDTO recuperada = bitacoraPSPDAO.buscarBitacoraPSPPorId(999);
        assertEquals("S21012345", recuperada.getMatricula());
    }

    @Test
    public void pruebaListarBitacorasPSPExitoso() throws Exception {
        List<BitacoraPSPDTO> lista = bitacoraPSPDAO.listarBitacorasPSP();
        assertFalse(lista.isEmpty());
    }

    @Test
    public void pruebaAgregarBitacoraErrorMatriculaNula() {
        assertThrows(DAOExcepcion.class, () -> bitacoraPSPDAO.agregarBitacoraPSP(dtoInvalido));
    }

    @Test
    public void pruebaActualizarBitacoraExcepcionConexionCerrada() throws Exception {
        ConexionBD.obtenerInstancia().obtenerConexion().close();
        assertThrows(DAOExcepcion.class, () -> bitacoraPSPDAO.actualizarBitacoraPSP(dtoParaAgregar));
        ConexionBD.reset();
        bitacoraPSPDAO = new BitacoraPSPDAO();
    }
}