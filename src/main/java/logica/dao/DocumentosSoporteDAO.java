package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.DocumentosSoporteDAOInterfaz;
import logica.dto.DocumentosSoporteDTO;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DocumentosSoporteDAO implements DocumentosSoporteDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO documentossoporte(Matricula, TipoDocumento, Estado) VALUES (?, ?, ?)";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM documentossoporte WHERE idDocumentoSoporte = ?";
    private static final String SQL_UPDATE = "UPDATE documentossoporte SET Matricula = ?, TipoDocumento = ?, Estado = ? WHERE idDocumentoSoporte = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM documentossoporte";

    private Connection conexion;
    private static final Logger logger = Logger.getLogger(DocumentosSoporteDAO.class.getName());

    public DocumentosSoporteDAO() throws DAOExcepcion {
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error al leer archivo de cofniguración", e);
            throw new DAOExcepcion("Error de configuracion", e);
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error de conexion SQL en DocumentoSoporteDAO", e);
            throw new DAOExcepcion("Error de base de datos", e);
        }
    }

    @Override
    public DocumentosSoporteDTO agregarDocumentoSoporte(DocumentosSoporteDTO documento) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, documento.getMatricula());
            preparedStatement.setString(2, documento.getTipoDocumento());
            preparedStatement.setString(3, documento.getEstado());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    documento.setIdDocumento(resultSet.getInt(1));
                }
            }
            logger.log(Level.INFO, "Documento de soporte agregado con éxito. ID: " + documento.getIdDocumento());
            return documento;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al agregar documento de soporte", e);
            throw new DAOExcepcion("Error al agregar documento de soporte", e);        }
    }

    @Override
    public DocumentosSoporteDTO buscarDocumentoSoportePorId(int idDocumento) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_ID)) {
            preparedStatement.setInt(1, idDocumento);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new DocumentosSoporteDTO(
                            resultSet.getInt("idDocumentoSoporte"),
                            resultSet.getString("Matricula"),
                            resultSet.getString("TipoDocumento"),
                            resultSet.getString("Estado")
                    );
                }else{
                    logger.log(Level.WARNING, "No se encontró documento de soporte con ID: " + idDocumento);
                    throw new DAOExcepcion("Documento de soporte no encontrado con ID: " + idDocumento, null);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al buscar documento de soporte por ID: " + idDocumento, e);
            throw new DAOExcepcion("Error al buscar documento de soporte por ID", e);        }
    }

    @Override
    public boolean actualizarDocumentoSoporte(DocumentosSoporteDTO documento) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, documento.getMatricula());
            preparedStatement.setString(2, documento.getTipoDocumento());
            preparedStatement.setString(3, documento.getEstado());
            preparedStatement.setInt(4, documento.getIdDocumento());
            int filasAfectadas = preparedStatement.executeUpdate();
            if (filasAfectadas > 0){
                logger.log(Level.INFO, "Documento de soporte actualizado con éxito. ID: " + documento.getIdDocumento());
                return true;
            }else{
                logger.log(Level.WARNING, "No se encontró documento de soporte para actualizar con ID: " + documento.getIdDocumento());
                throw new EntidadNoEncontradaExcepcion("Documento de soporte no encontrado para actualizar con ID: " + documento.getIdDocumento());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al actualizar documento de soporte", e);
            throw new DAOExcepcion("Error al actualizar documento de soporte", e);        }
    }

    @Override
    public List<DocumentosSoporteDTO> obtenerTodosLosDocumentosSoporte() throws DAOExcepcion {
        List<DocumentosSoporteDTO> lista = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                DocumentosSoporteDTO documento = new DocumentosSoporteDTO(
                        resultSet.getInt("idDocumentoSoporte"),
                        resultSet.getString("Matricula"),
                        resultSet.getString("TipoDocumento"),
                        resultSet.getString("Estado")
                );
                lista.add(documento);
            }
            return lista;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al listar todos los documentos de soporte", e);
            throw new DAOExcepcion("Error al listar documentos de soporte", e);
        }    }
}
