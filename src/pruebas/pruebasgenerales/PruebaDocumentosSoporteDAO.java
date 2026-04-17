package pruebasgenerales;

import accesodatos.ConexionBD;
import logica.dao.DocumentosSoporteDAO;
import logica.dto.DocumentosSoporteDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PruebaDocumentosSoporteDAO {

    @BeforeAll
    static void configurarConexion() {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/sppbdprueba");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contraseña", "testpass123");
        ConexionBD.reset();
    }


    @BeforeEach
    void limpiarAntes() throws Exception {
        limpiarTablas();
        System.out.println("Limpieza ANTES de prueba");
        insertarDatosNecesarios();
    }

    @AfterEach
    void limpiarDespues() throws Exception {
        limpiarTablas();
        System.out.println("Limpieza DESPUÉS de prueba (aunque falle)");
    }

    void limpiarTablas() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement comandoControl = conexion.createStatement()) {
            comandoControl.execute("SET FOREIGN_KEY_CHECKS = 0");
            comandoControl.execute("TRUNCATE TABLE documentossoporte");
            comandoControl.execute("TRUNCATE TABLE practicante");
            comandoControl.execute("TRUNCATE TABLE usuario");
            comandoControl.execute("TRUNCATE TABLE seccion");
            comandoControl.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    private void insertarDatosNecesarios() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();

        String sqlSeccion = "INSERT INTO seccion (idSeccion, Nombre) VALUES (?, ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sqlSeccion)) {
            sentencia.setInt(1, 1);
            sentencia.setString(2, "Sistemas");
            sentencia.executeUpdate();
        }

        String sqlUsuario = "INSERT INTO usuario (idUsuario, Nombre, ApellidoP, Contrasenia, TipoUsuario) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sqlUsuario)) {
            sentencia.setInt(1, 1);
            sentencia.setString(2, "Juan");
            sentencia.setString(3, "Perez");
            sentencia.setString(4, "pass123");
            sentencia.setString(5, "PRACTICANTE");
            sentencia.executeUpdate();
        }

        String sqlPracticante = "INSERT INTO practicante (Matricula, idSeccion, Semestre, Genero, Edad, idUsuario) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement sentencia = conexion.prepareStatement(sqlPracticante)) {
            sentencia.setString(1, "S21012345");
            sentencia.setInt(2, 1);
            sentencia.setString(3, "7");
            sentencia.setString(4, "MASCULINO");
            sentencia.setInt(5, 21);
            sentencia.setInt(6, 1);
            sentencia.executeUpdate();
        }
    }

    @Test
    public void pruebaAgregarBuscarDocumentoSoporte() throws Exception {
        DocumentosSoporteDAO documentoDAO = new DocumentosSoporteDAO();
        DocumentosSoporteDTO documentoDTO = new DocumentosSoporteDTO(0, "S21012345", "Reporte Parcial", "Entregado");

        documentoDAO.agregarDocumentoSoporte(documentoDTO);
        DocumentosSoporteDTO documentoRecuperado = documentoDAO.buscarDocumentoSoportePorId(documentoDTO.getIdDocumento());

        assertEquals(documentoDTO.getTipoDocumento(), documentoRecuperado.getTipoDocumento(),
                "El tipo de documento recuperado debe ser igual al guardado");
    }

    @Test
    public void pruebaActualizarBuscarDocumentoSoporte() throws Exception {
        DocumentosSoporteDAO documentoDAO = new DocumentosSoporteDAO();
        DocumentosSoporteDTO documentoDTO = new DocumentosSoporteDTO(0, "S21012345", "Carta Aceptación", "Pendiente");
        documentoDAO.agregarDocumentoSoporte(documentoDTO);

        String nuevoEstado = "Validado";
        documentoDTO.setEstado(nuevoEstado);
        documentoDAO.actualizarDocumentoSoporte(documentoDTO);

        DocumentosSoporteDTO documentoActualizado = documentoDAO.buscarDocumentoSoportePorId(documentoDTO.getIdDocumento());

        assertEquals(nuevoEstado, documentoActualizado.getEstado(),
                "El estado del documento debe haberse actualizado correctamente");
    }

    @Test
    public void pruebaObtenerTodosLosDocumentosSoporte() throws Exception {
        DocumentosSoporteDAO documentoDAO = new DocumentosSoporteDAO();
        documentoDAO.agregarDocumentoSoporte(new DocumentosSoporteDTO(0, "S21012345", "Doc1", "OK"));
        documentoDAO.agregarDocumentoSoporte(new DocumentosSoporteDTO(0, "S21012345", "Doc2", "OK"));

        List<DocumentosSoporteDTO> lista = documentoDAO.obtenerTodosLosDocumentosSoporte();

        assertEquals(2, lista.size(), "La lista debería contener exactamente 2 documentos");
    }
}