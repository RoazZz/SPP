package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.BitacoraSistemaDAO;
import logica.dto.BitacoraSistemaDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaBitacoraSistemaDAO {

    private static BitacoraSistemaDAO bitacoraSistemaDAO;
    private BitacoraSistemaDTO bitacoraValida;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        bitacoraSistemaDAO = new BitacoraSistemaDAO();
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("DELETE FROM bitacora WHERE NombreUsuario = 'Usuario Prueba'");
        }
        bitacoraValida = new BitacoraSistemaDTO(
                0,
                "COORDINADOR",
                "Usuario Prueba",
                "INICIO_SESION",
                LocalDateTime.of(2026, 4, 17, 10, 30, 0),
                "Inicio sesion en el sistema"
        );
    }

    @Test
    public void pruebaAgregarBitacoraExitoso() throws Exception {
        boolean resultado = bitacoraSistemaDAO.agregarBitacora(bitacoraValida);
        assertTrue(resultado);
    }

    @Test
    public void pruebaListarBitacorasExitoso() throws Exception {
        bitacoraSistemaDAO.agregarBitacora(bitacoraValida);
        List<BitacoraSistemaDTO> bitacorasRecuperadas = bitacoraSistemaDAO.listarBitacoras();
        assertFalse(bitacorasRecuperadas.isEmpty());
    }

    @Test
    public void pruebaAgregarBitacoraExcepcionRolNulo() {
        bitacoraValida.setRolUsuario(null);
        assertThrows(DAOExcepcion.class, () ->
                bitacoraSistemaDAO.agregarBitacora(bitacoraValida));
    }
}