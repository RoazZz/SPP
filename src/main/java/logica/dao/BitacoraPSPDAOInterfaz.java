package logica.dao;
import accesodatos.ConexionBD;
import logica.dto.BitacoraPSPDTO;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class BitacoraPSPDAOInterfaz extends ConexionBD implements interfaces.BitacoraPSPDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO bitacorapsp(idBitacoraPSP, Matricula, Fecha) VALUES (?, ?, ?)"; //FALTA RUTA O NOMBRE DEL ARCHIVO
    private static final String SQL_SELECT_BY_IDBITACORA = "SELECT * FROM bitacorapsp WHERE idBitacoraPSP = ?";
    private static final String SQL_UPDATE = "UPDATE bitacorapsp SET Matricula = ?, Fecha = ? WHERE idBitacoraPSP = ?";
    private static final String SQL_EXISTS_PRACTICANTE = "SELECT 1 FROM practicante WHERE Matricula = ?";
    public BitacoraPSPDAOInterfaz() {
        super();
    }

    @Override
    public void agregar(BitacoraPSPDTO bitacora) throws Exception {
        try (PreparedStatement ps = conexion.prepareStatement(SQL_EXISTS_PRACTICANTE)) {
            ps.setString(1, bitacora.getMatricula());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("La matrícula '" + bitacora.getMatricula() + "' no existe en practicante. " + "No se puede registrar la bitácora PSP.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al validar matrícula en practicante: " + e.getMessage());
        }

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

    @Override
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

    @Override
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
}
