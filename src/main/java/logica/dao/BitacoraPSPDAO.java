package logica.dao;
import accesodatos.ConexionBD;
import logica.dto.BitacoraPSPDTO;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class BitacoraPSPDAO extends ConexionBD{
    private static final String SQL_INSERT = "INSERT INTO bitacorapsp(idBitacoraPSP, Matricula, Fecha) VALUES (?, ?, ?)";
    private static final String SQL_SELECT_BY_IDBITACORA = "SELECT * FROM bitacorapsp WHERE idBitacoraPSP = ?";
    private static final String SQL_UPDATE = "UPDATE bitacorapsp SET Matricula = ?, Fecha = ? WHERE idBitacoraPSP = ?";
    private static final String SQL_DELETE = "DELETE FROM bitacorapsp WHERE idBitacoraPSP = ?";

    public BitacoraPSPDAO() {
        super();
    }

    public void agregar(BitacoraPSPDTO bitacora) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, bitacora.getIdBBitacora());
            preparedStatement.setString(2, bitacora.getMatricula());
            preparedStatement.setDate(3, java.sql.Date.valueOf(bitacora.getFecha()));
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    bitacora.setIdBBitacora(resultSet.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al agregar bitácora PSP: " + e.getMessage());
        }
    }

    public BitacoraPSPDTO buscarPorId(int idBitacora) throws Exception {
            try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_IDBITACORA)) {
                preparedStatement.setInt(1, idBitacora);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return new BitacoraPSPDTO(
                                resultSet.getInt("idBitacoraPSP"),
                                resultSet.getString("Matricula"),
                                resultSet.getDate("Fecha").toLocalDate()
                        );
                    }
                    return null;
                }
            } catch (SQLException e) {
                throw new Exception("Error al buscar bitácora PSP por ID: " + e.getMessage());
            }
    }

    public void actualizar(BitacoraPSPDTO bitacora) throws Exception {
            try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
                preparedStatement.setString(1, bitacora.getMatricula());
                preparedStatement.setDate(2, java.sql.Date.valueOf(bitacora.getFecha()));
                preparedStatement.setInt(3, bitacora.getIdBBitacora());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new Exception("Error al actualizar bitácora PSP: " + e.getMessage());
            }
    }

    public void eliminar(int idBitacora) throws Exception {
            try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_DELETE)) {
                preparedStatement.setInt(1, idBitacora);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new Exception("Error al eliminar bitácora PSP: " + e.getMessage());
            }
    }
}
