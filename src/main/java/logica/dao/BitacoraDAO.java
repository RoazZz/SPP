package logica.dao;

import interfaces.BitacoraDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.BitacoraDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BitacoraDAO extends ConexionBD implements BitacoraDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO Bitacora (Matricula, Fecha_Hora, TipoEvento, Descripcion) VALUES ( ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_MATRICULA = "SELECT * FROM Bitacora WHERE Matricula = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Bitacora";

    public BitacoraDAO(){
        super();
    }

    @Override
    public void agregarBitacora(BitacoraDTO bitacora) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, bitacora.getMatricula());
            preparedStatement.setTimestamp(2, java.sql.Timestamp.valueOf(bitacora.getFechaHora()));
            preparedStatement.setString(3, bitacora.getTipoEvento());
            preparedStatement.setString(4, bitacora.getDescripcionEvento());
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    bitacora.setIdRegistro(resultSet.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al agregar la bitacora: " + e.getMessage());
        }
    }

    @Override
    public BitacoraDTO buscarBitacoraPorMatricula(String matricula) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_MATRICULA)) {
            preparedStatement.setString(1, matricula);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new BitacoraDTO(
                            resultSet.getInt("idRegistro"),
                            resultSet.getString("Matricula"),
                            resultSet.getString("TipoEvento"),
                            resultSet.getTimestamp("Fecha_Hora").toLocalDateTime(),
                            resultSet.getString("DescripcionEvento")
                    );
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar Bitacora por Matricula: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<BitacoraDTO> listarBitacoras() throws Exception {
        List<BitacoraDTO> listaBitacora = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                BitacoraDTO bitacora = new BitacoraDTO(
                        resultSet.getInt("idRegistro"),
                        resultSet.getString("Matricula"),
                        resultSet.getString("TipoEvento"),
                        resultSet.getTimestamp("Fecha_Hora").toLocalDateTime(),
                        resultSet.getString("DescripcionEvento")
                );
                listaBitacora.add(bitacora);
            }
        } catch (SQLException e) {
            throw new Exception("Error al listar las actividades: " + e.getMessage());
        }
        return listaBitacora;
    }
}
