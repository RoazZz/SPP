package pruebasgenerales;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.dao.DocumentosSoporteDAO;
import logica.dao.ProfesorDAO;
import logica.dto.DocumentosSoporteDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PruebaDocumentosSoporteDAO {
    private static DocumentosSoporteDAO documentosSoporteDAO;
    private DocumentosSoporteDTO documentosSoporteValido;
    private DocumentosSoporteDTO documentoSoporteInvalidoMatriculaNula;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
        documentosSoporteDAO = new DocumentosSoporteDAO();

        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("TRUNCATE TABLE documentossoporte");
            statement.execute("TRUNCATE TABLE practicante");
            statement.execute("TRUNCATE TABLE usuario");
            statement.execute("TRUNCATE TABLE seccion");

            statement.execute("INSERT INTO seccion VALUES (1, 'Sistemas')");

            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) " +
                    "VALUES (1, 'Juan', 'Perez', 'Admin', 'pass123', 'ACTIVO', 'PRACTICANTE')");

            statement.execute("INSERT INTO practicante VALUES ('S21012345', 1, '7', 'MASCULINO', 21, 0, 1)");

            statement.execute("INSERT INTO documentossoporte (idDocumentoSoporte, Matricula, TipoDocumento, Estado) " +
                    "VALUES (999, 'S21012345', 'Reporte Maestro', 'Validado')");

            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM documentossoporte WHERE idDocumentoSoporte != 999");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }

        documentosSoporteValido = new DocumentosSoporteDTO(0, "S21012345", "Carta Aceptación", "Pendiente");
        documentoSoporteInvalidoMatriculaNula = new DocumentosSoporteDTO(0, null, "Error", "Fallo");
    }

    @AfterEach
    void restaurarRecursos() {
        ConexionBD.reset();
        try {
            documentosSoporteDAO = new DocumentosSoporteDAO();
        } catch (Exception e) {
            System.err.println("Error al restaurar el DAO: " + e.getMessage());
        }
    }
    @Test
    public void pruebaAgregarDocumentoSoporteExitoso() throws Exception {
        DocumentosSoporteDTO resultado = documentosSoporteDAO.agregarDocumentoSoporte(documentosSoporteValido);
        assertNotNull(resultado);
    }

    @Test
    public void pruebaBuscarDocumentoSoportePorIdExitoso() throws Exception {
        DocumentosSoporteDTO recuperado = documentosSoporteDAO.buscarDocumentoSoportePorId(999);
        assertEquals("Reporte Maestro", recuperado.getTipoDocumento());
    }

    @Test
    public void pruebaObtenerTodosLosDocumentosSoporteExitoso() throws Exception {
        List<DocumentosSoporteDTO> lista = documentosSoporteDAO.obtenerTodosLosDocumentosSoporte();
        assertFalse(lista.isEmpty());
    }

    @Test
    public void pruebaAgregarDocumentoExcepcionMatriculaNula() {
        assertThrows(DAOExcepcion.class, () -> documentosSoporteDAO.agregarDocumentoSoporte(documentoSoporteInvalidoMatriculaNula));
    }

    @Test
    public void pruebaActualizarDocumentoExcepcionConexionCerrada() throws Exception {
        ConexionBD.obtenerInstancia().obtenerConexion().close();
        assertThrows(DAOExcepcion.class, () -> documentosSoporteDAO.actualizarDocumentoSoporte(documentosSoporteValido));
    }
}