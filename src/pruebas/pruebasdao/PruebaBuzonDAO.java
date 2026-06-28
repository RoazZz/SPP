package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.BuzonDAO;
import logica.dto.BuzonDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaBuzonDAO {

    private static final int ID_USUARIO_PRUEBA = 917;

    private static BuzonDAO buzonDAO;
    private BuzonDTO buzonValido;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        buzonDAO = new BuzonDAO();
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM buzon WHERE idUsuario = " + ID_USUARIO_PRUEBA);
            statement.execute("DELETE FROM usuario WHERE idUsuario = " + ID_USUARIO_PRUEBA);
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (" + ID_USUARIO_PRUEBA + ", 'UsuarioBuzon', 'Ap', 'Am', 'clave', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        buzonValido = new BuzonDTO(ID_USUARIO_PRUEBA);
    }

    @Test
    public void pruebaAgregarBuzonExitoso() throws Exception {
        boolean resultado = buzonDAO.agregarBuzon(buzonValido);
        assertTrue(resultado);
    }

    @Test
    public void pruebaObtenerBuzonPorIdUsuarioExitoso() throws Exception {
        buzonDAO.agregarBuzon(buzonValido);
        BuzonDTO buzonRecuperado = buzonDAO.obtenerBuzonPorIdUsuario(ID_USUARIO_PRUEBA);
        assertEquals(ID_USUARIO_PRUEBA, buzonRecuperado.getIdUsuario());
    }

    @Test
    public void pruebaObtenerBuzonPorIdUsuarioExcepcionNoEncontrado() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () ->
                buzonDAO.obtenerBuzonPorIdUsuario(-1));
    }
}