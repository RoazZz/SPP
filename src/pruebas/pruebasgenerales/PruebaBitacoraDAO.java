package pruebasgenerales;

import accesodatos.ConexionBD;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.BitacoraDAO;
import logica.dto.BitacoraDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PruebaBitacoraDAO {

    @BeforeAll
    static void configurarConexion() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        System.out.println("Conexión reiniciada");
    }

    @BeforeEach
    void limpiarAntes() throws Exception {
        limpiarTablas();
    }

    @AfterEach
    void limpiarDespues() throws Exception {
        limpiarTablas();
    }

    void limpiarTablas() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();

        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0");
        conexion.createStatement().execute("TRUNCATE TABLE Bitacora");
        conexion.createStatement().execute("TRUNCATE TABLE Practicante");
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    private BitacoraDTO crearBitacoraEjemplo() {
        return new BitacoraDTO(
                0,
                "S24021",
                "LOGIN",
                LocalDateTime.of(2026, 4, 17, 10, 30, 0),
                "El practicante inició sesión"
        );
    }

    @Test
    public void pruebaBuscarBitacoraPorMatricula() throws Exception {
        BitacoraDAO bitacoraDAO = new BitacoraDAO();
        BitacoraDTO bitacoraDTO = crearBitacoraEjemplo();

        bitacoraDAO.agregarBitacora(bitacoraDTO);

        BitacoraDTO resultado = bitacoraDAO.buscarBitacoraPorMatricula("S24021");

        assertEquals("S24021", resultado.getMatricula());
    }

    @Test
    public void pruebaBuscarBitacoraNoExistente() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> {
            BitacoraDAO bitacoraDAO = new BitacoraDAO();
            bitacoraDAO.buscarBitacoraPorMatricula("S24210");
        });
    }

    @Test
    public void pruebaListarBitacoras() throws Exception {
        BitacoraDAO bitacoraDAO = new BitacoraDAO();
        bitacoraDAO.agregarBitacora(crearBitacoraEjemplo());

        List<BitacoraDTO> lista = bitacoraDAO.listarBitacoras();

        assertFalse(lista.isEmpty(), "La lista no debe estar vacia");
    }

}
