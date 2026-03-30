package logica.dao;
import accesodatos.ConexionBD;
import interfaces.BitacoraPSPDAOInterfaz;
import logica.dto.BitacoraPSPDTO;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class BitacoraPSPDAO extends ConexionBD implements BitacoraPSPDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO bitacorapsp(idBitacoraPSP, Matricula, Fecha) VALUES (?, ?, ?)"; //FALTA RUTA O NOMBRE DEL ARCHIVO
    private static final String SQL_SELECT_BY_IDBITACORA = "SELECT * FROM bitacorapsp WHERE idBitacoraPSP = ?";
    private static final String SQL_UPDATE = "UPDATE bitacorapsp SET Matricula = ?, Fecha = ? WHERE idBitacoraPSP = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM bitacorapsp";

    public BitacoraPSPDAO() {
        super();
    }

    @Override
    public void agregarBitacoraPSP(BitacoraPSPDTO bitacora) throws Exception {
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
    public BitacoraPSPDTO buscarBitacoraPSPPorId(int idBitacora) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_IDBITACORA)) {
            preparedStatement.setInt(1, idBitacora);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new BitacoraPSPDTO(
                            resultSet.getInt("idBitacoraPSP"),
                            resultSet.getString("Matricula"),
                            resultSet.getDate("Fecha").toLocalDate()
                    );
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar bitácora PSP por ID: " + e.getMessage());
        }
    }

    @Override
    public void actualizarBitacoraPSP(BitacoraPSPDTO bitacora) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
                preparedStatement.setString(1, bitacora.getMatricula());
                preparedStatement.setDate(2, java.sql.Date.valueOf(bitacora.getFecha()));
                preparedStatement.setInt(3, bitacora.getIdBBitacora());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new Exception("Error al actualizar bitácora PSP: " + e.getMessage());
            }
    }

    public List<BitacoraPSPDTO> listarBitacorasPSP() throws Exception{
        List<BitacoraPSPDTO> listaBitacorasPSP = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                BitacoraPSPDTO bitacora = new BitacoraPSPDTO(
                        resultSet.getInt("idBitacoraPSP"),
                        resultSet.getString("Matricula"),
                        resultSet.getDate("Fecha").toLocalDate()
                );
                listaBitacorasPSP.add(bitacora);
            }
            return listaBitacorasPSP;
        } catch (SQLException e) {
            throw new Exception("Error al listar bitácoras PSP: " + e.getMessage());
        }
    }
}
