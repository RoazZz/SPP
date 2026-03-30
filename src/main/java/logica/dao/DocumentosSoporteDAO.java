package logica.dao;
import accesodatos.ConexionBD;
import logica.dto.DocumentosSoporteDTO;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentosSoporteDAO extends ConexionBD implements interfaces.DocumentosSoporteDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO documentossoporte(idDocumentoSoporte, Matricula, TipoDocumento, Estado) VALUES (?, ?, ?, ?)";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM documentossoporte WHERE idDocumentoSoporte = ?";
    private static final String SQL_UPDATE = "UPDATE documentossoporte SET Matricula = ?, TipoDocumento = ?, Estado = ? WHERE idDocumentoSoporte = ?";
    public DocumentosSoporteDAO() {
        super();
    }

    @Override
    public void agregar(DocumentosSoporteDTO documento) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, documento.getIdDocumento());
            preparedStatement.setString(2, documento.getMatricula());
            preparedStatement.setString(3, documento.getTipoDocumento());
            preparedStatement.setString(4, documento.getEstado());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    documento.setIdDocumento(resultSet.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al agregar documento de soporte: " + e.getMessage());
        }
    }

    @Override
    public DocumentosSoporteDTO buscarPorId(int idDocumento) throws Exception {
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
                }
                return null;
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar documento de soporte por ID: " + e.getMessage());
        }
    }

    @Override
    public void actualizar(DocumentosSoporteDTO documento) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, documento.getMatricula());
            preparedStatement.setString(2, documento.getTipoDocumento());
            preparedStatement.setString(3, documento.getEstado());
            preparedStatement.setInt(4, documento.getIdDocumento());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al actualizar documento de soporte: " + e.getMessage());
        }
    }
}
