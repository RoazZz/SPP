package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.BitacoraPSPDAO;
import logica.dto.BitacoraPSPDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaBitacoraPSPDAO {

    private static final String MATRICULA_PRUEBA = "S20009120";

    private static BitacoraPSPDAO bitacoraPSPDAO;
    private BitacoraPSPDTO bitacoraPSPValida;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        bitacoraPSPDAO = new BitacoraPSPDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM seccion WHERE idSeccion = 912");
            statement.execute("INSERT INTO seccion (idSeccion, Nombre) VALUES (912, 'Seccion Base')");
            statement.execute("DELETE FROM usuario WHERE idUsuario = 912");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (912, 'PracBase', 'Ap', 'Am', 'clave', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("DELETE FROM practicante WHERE Matricula = 'S20009120'");
            statement.execute("INSERT INTO practicante (Matricula, idSeccion, Semestre, Genero, Edad, LenguaIndigena, idUsuario) VALUES ('S20009120', 912, '5', 'MASCULINO', 22, 0, 912)");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM bitacorapsp WHERE Matricula = '" + MATRICULA_PRUEBA + "'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        bitacoraPSPValida = new BitacoraPSPDTO(0, MATRICULA_PRUEBA, LocalDate.of(2026, 4, 17));
    }

    @Test
    public void pruebaAgregarBitacoraPSPExitoso() throws Exception {
        BitacoraPSPDTO bitacoraGuardada = bitacoraPSPDAO.agregarBitacoraPSP(bitacoraPSPValida);
        assertTrue(bitacoraGuardada.getIdBBitacora() > 0);
    }

    @Test
    public void pruebaActualizarBitacoraPSPExitoso() throws Exception {
        BitacoraPSPDTO bitacoraGuardada = bitacoraPSPDAO.agregarBitacoraPSP(bitacoraPSPValida);
        bitacoraGuardada.setFecha(LocalDate.of(2026, 5, 1));
        boolean resultado = bitacoraPSPDAO.actualizarBitacoraPSP(bitacoraGuardada);
        assertTrue(resultado);
    }

    @Test
    public void pruebaBuscarBitacoraPSPPorIdExitoso() throws Exception {
        BitacoraPSPDTO bitacoraGuardada = bitacoraPSPDAO.agregarBitacoraPSP(bitacoraPSPValida);
        BitacoraPSPDTO bitacoraRecuperada = bitacoraPSPDAO.buscarBitacoraPSPPorId(bitacoraGuardada.getIdBBitacora());
        assertEquals(bitacoraGuardada.getIdBBitacora(), bitacoraRecuperada.getIdBBitacora());
    }

    @Test
    public void pruebaListarBitacorasPSPExitoso() throws Exception {
        bitacoraPSPDAO.agregarBitacoraPSP(bitacoraPSPValida);
        List<BitacoraPSPDTO> bitacorasRecuperadas = bitacoraPSPDAO.listarBitacorasPSP();
        assertFalse(bitacorasRecuperadas.isEmpty());
    }

    @Test
    public void pruebaBuscarBitacoraPSPPorIdExcepcionNoEncontrado() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () ->
                bitacoraPSPDAO.buscarBitacoraPSPPorId(-1));
    }

    @Test
    public void pruebaAgregarBitacoraPSPExcepcionMatriculaNula() {
        bitacoraPSPValida.setMatricula(null);
        assertThrows(DAOExcepcion.class, () ->
                bitacoraPSPDAO.agregarBitacoraPSP(bitacoraPSPValida));
    }
}