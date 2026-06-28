package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.ProyectoDAO;
import logica.dto.ProyectoDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaProyectoDAO {

    private static final String ID_ORGANIZACION_PRUEBA = "ORG905";
    private static final String NUMERO_PERSONAL_PRUEBA = "COORD905";
    private static final int ID_USUARIO_PRUEBA = 905;

    private static ProyectoDAO proyectoDAO;
    private ProyectoDTO proyectoValido;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        proyectoDAO = new ProyectoDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM organizacionvinculada WHERE idOrganizacion = '" + ID_ORGANIZACION_PRUEBA + "'");
            statement.execute("INSERT INTO organizacionvinculada (idOrganizacion, Nombre, Direccion) VALUES ('" + ID_ORGANIZACION_PRUEBA + "', 'Org Proyecto', 'Direccion')");
            statement.execute("DELETE FROM usuario WHERE idUsuario = " + ID_USUARIO_PRUEBA);
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (" + ID_USUARIO_PRUEBA + ", 'CoordProy', 'Ap', 'Am', 'clave', 'ACTIVO', 'COORDINADOR')");
            statement.execute("DELETE FROM coordinador WHERE NumeroDePersonal = '" + NUMERO_PERSONAL_PRUEBA + "'");
            statement.execute("INSERT INTO coordinador (NumeroDePersonal, idUsuario) VALUES ('" + NUMERO_PERSONAL_PRUEBA + "', " + ID_USUARIO_PRUEBA + ")");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM proyecto WHERE Nombre = 'ProyectoPrueba'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        proyectoValido = new ProyectoDTO(0, ID_ORGANIZACION_PRUEBA, NUMERO_PERSONAL_PRUEBA, "ProyectoPrueba", "Descripcion del proyecto");
    }

    @Test
    public void pruebaAgregarProyectoExitoso() throws Exception {
        proyectoDAO.agregarProyecto(proyectoValido);
        assertTrue(proyectoValido.getIdProyecto() > 0);
    }

    @Test
    public void pruebaActualizarProyectoExitoso() throws Exception {
        proyectoDAO.agregarProyecto(proyectoValido);
        proyectoValido.setNombre("ProyectoPrueba");
        proyectoValido.setDescripcion("Descripcion modificada");
        proyectoDAO.actualizarProyecto(proyectoValido);
        ProyectoDTO proyectoRecuperado = proyectoDAO.buscarProyectoPorIdProyecto(proyectoValido.getIdProyecto());
        assertEquals("Descripcion modificada", proyectoRecuperado.getDescripcion());
    }

    @Test
    public void pruebaBuscarProyectoPorIdProyectoExitoso() throws Exception {
        proyectoDAO.agregarProyecto(proyectoValido);
        ProyectoDTO proyectoRecuperado = proyectoDAO.buscarProyectoPorIdProyecto(proyectoValido.getIdProyecto());
        assertEquals(proyectoValido.getIdProyecto(), proyectoRecuperado.getIdProyecto());
    }

    @Test
    public void pruebaListarProyectosExitoso() throws Exception {
        proyectoDAO.agregarProyecto(proyectoValido);
        List<ProyectoDTO> proyectosRecuperados = proyectoDAO.listarProyectos();
        assertFalse(proyectosRecuperados.isEmpty());
    }

    @Test
    public void pruebaBuscarProyectoPorIdProyectoExcepcionNoEncontrado() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () ->
                proyectoDAO.buscarProyectoPorIdProyecto(-1));
    }

    @Test
    public void pruebaAgregarProyectoExcepcionNombreNulo() {
        proyectoValido.setNombre(null);
        assertThrows(DAOExcepcion.class, () ->
                proyectoDAO.agregarProyecto(proyectoValido));
    }
}