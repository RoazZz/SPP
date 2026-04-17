package pruebasgenerales;

import accesodatos.ConexionBD;
import logica.dao.AdministradorDAO;
import logica.dto.AdministradorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import logica.enums.TipoTurno;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PruebaAdministradorDAO {
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
        System.out.println("Limpieza ANTES de prueba");
    }

    @AfterEach
    void limpiarDespues() throws Exception {
        limpiarTablas();
        System.out.println("Limpieza DESPUÉS de prueba (aunque falle)");
    }

    @BeforeEach
    void limpiarTablas() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();

        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0");
        conexion.createStatement().execute("TRUNCATE TABLE administrador");
        conexion.createStatement().execute("TRUNCATE TABLE usuario");
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");

        System.out.println("Tablas limpiadas para prueba");
    }

    private AdministradorDTO crearAdministradorEjemplo() {
        return new AdministradorDTO(
                0,
                "Admin",
                "User",
                "adminuser",
                "adminpass",
                TipoEstado.ACTIVO,
                TipoDeUsuario.ADMIN,
                0
        );
    }

    @Test
    public void pruebaAgregarBuscarAdministrador() throws Exception {
        AdministradorDAO administradorDAO = new AdministradorDAO();
        AdministradorDTO administradorDTO = crearAdministradorEjemplo();
        administradorDAO.agregarAdministrador(administradorDTO);

        AdministradorDTO adminRecuperado = administradorDAO.buscarAdministradorPorId(administradorDTO.getIdAdministrador());
        assertEquals(administradorDTO.getNombre(), adminRecuperado.getNombre());

        System.out.println("Prueba de integración exitosa: Administrador persistido correctamente.");
    }
 }

