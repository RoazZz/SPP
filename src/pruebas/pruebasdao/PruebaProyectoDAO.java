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

import static org.junit.jupiter.api.Assertions.*;

public class PruebaProyectoDAO {

    private static ProyectoDAO proyectoDAO;
    private ProyectoDTO proyectoValido;
    private ProyectoDTO proyectoSinOrganizacion;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        proyectoDAO = new ProyectoDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE Proyecto");
            statement.execute("TRUNCATE TABLE Profesor");
            statement.execute("TRUNCATE TABLE OrganizacionVinculada");
            statement.execute("TRUNCATE TABLE Usuario");
            statement.execute("INSERT INTO Usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                    "VALUES (1, 'Jared', 'Morales', 'Tirado', '123', 'ACTIVO', 'COORDINADOR')");
            statement.execute("INSERT INTO Profesor (idUsuario, NumeroDePersonal, Turno) " +
                    "VALUES (1, '00000', 'MATUTINO')");
            statement.execute("INSERT INTO OrganizacionVinculada (idOrganizacion, Nombre, Direccion) " +
                    "VALUES ('ORG001', 'Ejemplooo', 'Calle FEI 12')");
            statement.execute("INSERT INTO Proyecto (idProyecto, idOrganizacion, NumeroDePersonal, Nombre, Descripcion) " +
                    "VALUES (999, 'ORG001', '00000', 'Proyecto Maestro', 'Descripcion maestra')");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }


    private void limpiarTablas() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 0");
        conexion.createStatement().execute("TRUNCATE TABLE Proyecto");
        conexion.createStatement().execute("TRUNCATE TABLE Profesor");
        conexion.createStatement().execute("TRUNCATE TABLE OrganizacionVinculada");
        conexion.createStatement().execute("TRUNCATE TABLE Usuario");
        conexion.createStatement().execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM Proyecto WHERE idProyecto != 999");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        proyectoValido = new ProyectoDTO(
                0,
                "ORG001",
                "00000",
                "Proyecto FEI",
                "Esta descripcion es un ejemplo."
        );
        proyectoSinOrganizacion = new ProyectoDTO(
                0,
                null,
                "00000",
                "Proyecto Invalido",
                "Descripcion invalida"
        );
    }

    @Test
    public void pruebaAgregarProyectoExitoso() throws Exception {
        proyectoDAO.agregarProyecto(proyectoValido);
        assertTrue(proyectoValido.getIdProyecto() > 0);
    }

    @Test
    public void pruebaBuscarProyectoNoExistente() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () -> proyectoDAO.buscarProyectoPorIdProyecto(9999));
    }

    @Test
    public void pruebaAgregarProyectoExcepcionOrganizacionNula() {
        assertThrows(DAOExcepcion.class, () -> proyectoDAO.agregarProyecto(proyectoSinOrganizacion));
    }

}
