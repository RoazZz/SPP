package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.DocumentosSoporteDAOInterfaz;
import logica.dto.DocumentosSoporteDTO;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentosSoporteDAO implements DocumentosSoporteDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO documentossoporte(Matricula, TipoDocumento, Estado) " +
            "VALUES (?, ?, ?)";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM documentossoporte WHERE idDocumentoSoporte = ?";
    private static final String SQL_UPDATE = "UPDATE documentossoporte SET Matricula = ?, TipoDocumento = ?, Estado = ? " +
            "WHERE idDocumentoSoporte = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM documentossoporte";

    private Connection conexion;
    private static final Logger REGISTRADOR = Logger.getLogger(DocumentosSoporteDAO.class.getName());

    public DocumentosSoporteDAO() throws DAOExcepcion {
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioExcepcion){
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de cofniguración", ioExcepcion);
            throw new DAOExcepcion("Error de configuracion", ioExcepcion);
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexion SQL en DocumentoSoporteDAO", sqlExcepcion);
            throw new DAOExcepcion("Error de base de datos", sqlExcepcion);
        }
    }

    @Override
    public DocumentosSoporteDTO agregarDocumentoSoporte(DocumentosSoporteDTO documento) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            sentenciaPreparada.setString(1, documento.getMatricula());
            sentenciaPreparada.setString(2, documento.getTipoDocumento());
            sentenciaPreparada.setString(3, documento.getEstado());
            sentenciaPreparada.executeUpdate();

            try (ResultSet conjuntoResultado = sentenciaPreparada.getGeneratedKeys()) {
                if (conjuntoResultado.next()) {
                    documento.setIdDocumento(conjuntoResultado.getInt(1));
                }
            }
            REGISTRADOR.log(Level.INFO, "Documento de soporte agregado con éxito. ID " + documento.getIdDocumento());
            return documento;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al agregar documento de soporte", sqlExcepcion);
            throw new DAOExcepcion("Error al agregar documento de soporte", sqlExcepcion);        }
    }

    @Override
    public DocumentosSoporteDTO buscarDocumentoSoportePorId(int idDocumento) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_ID)) {
            sentenciaPreparada.setInt(1, idDocumento);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return new DocumentosSoporteDTO(
                            conjuntoResultado.getInt("idDocumentoSoporte"),
                            conjuntoResultado.getString("Matricula"),
                            conjuntoResultado.getString("TipoDocumento"),
                            conjuntoResultado.getString("Estado")
                    );
                } else{
                    REGISTRADOR.log(Level.WARNING, "No se encontró documento de soporte con ID: " + idDocumento);
                    throw new DAOExcepcion("Documento de soporte no encontrado con ID: " + idDocumento, null);
                }
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al buscar documento de soporte por ID: " + idDocumento, sqlExcepcion);
            throw new DAOExcepcion("Error al buscar documento de soporte por ID", sqlExcepcion);        }
    }

    @Override
    public boolean actualizarDocumentoSoporte(DocumentosSoporteDTO documento) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE)) {
            sentenciaPreparada.setString(1, documento.getMatricula());
            sentenciaPreparada.setString(2, documento.getTipoDocumento());
            sentenciaPreparada.setString(3, documento.getEstado());
            sentenciaPreparada.setInt(4, documento.getIdDocumento());
            int filasAfectadas = sentenciaPreparada.executeUpdate();
            if (filasAfectadas > 0){
                REGISTRADOR.log(Level.INFO, "Documento de soporte actualizado con éxito. ID: " + documento.getIdDocumento());
                return true;
            } else{
                REGISTRADOR.log(Level.WARNING, "No se encontró documento de soporte para actualizar con ID: " + documento.getIdDocumento());
                throw new EntidadNoEncontradaExcepcion("Documento de soporte no encontrado para actualizar con ID: " + documento.getIdDocumento());
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al actualizar documento de soporte", sqlExcepcion);
            throw new DAOExcepcion("Error al actualizar documento de soporte", sqlExcepcion);        }
    }

    @Override
    public List<DocumentosSoporteDTO> obtenerTodosLosDocumentosSoporte() throws DAOExcepcion {
        List<DocumentosSoporteDTO> lista = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            while (conjuntoResultado.next()) {
                DocumentosSoporteDTO documento = new DocumentosSoporteDTO(
                        conjuntoResultado.getInt("idDocumentoSoporte"),
                        conjuntoResultado.getString("Matricula"),
                        conjuntoResultado.getString("TipoDocumento"),
                        conjuntoResultado.getString("Estado")
                );
                lista.add(documento);
            }
            return lista;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al listar todos los documentos de soporte", sqlExcepcion);
            throw new DAOExcepcion("Error al listar documentos de soporte", sqlExcepcion);
        }    }
}
