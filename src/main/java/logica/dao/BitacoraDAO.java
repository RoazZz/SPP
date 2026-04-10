package logica.dao;

import excepciones.DAOExcepcion;
import interfaces.BitacoraDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.BitacoraDTO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BitacoraDAO implements BitacoraDAOInterfaz {
    private final Connection conexion;
    private static final Logger logger = Logger.getLogger(BitacoraDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO Bitacora (Matricula, Fecha_Hora, TipoEvento, Descripcion) VALUES ( ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_MATRICULA = "SELECT * FROM Bitacora WHERE Matricula = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Bitacora";

    public BitacoraDAO() throws IOException, SQLException {
        this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
    }

    @Override
    public void agregarBitacora(BitacoraDTO bitacora) throws DAOExcepcion {
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
            logger.log(Level.SEVERE, "Error al agregar bitacora", e);
            throw new DAOExcepcion ("Error al agregar la bitacora: ", e);
        }
    }

    @Override
    public BitacoraDTO buscarBitacoraPorMatricula(String matricula) throws DAOExcepcion {
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
                }else{
                    logger.log(Level.WARNING, "No se encontro alguna bitacora con Matricula : " + matricula);
                    throw new DAOExcepcion("No existe bitacora con Matricula : " + matricula, null);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar bitacora", e);
            throw new DAOExcepcion("Error al buscar Bitacora por Matricula: ", e);
        }
    }

    @Override
    public List<BitacoraDTO> listarBitacoras() throws DAOExcepcion {
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
            logger.log(Level.SEVERE, "Error al buscar bitacora", e);
            throw new DAOExcepcion("Error al listar las bitacoras: ", e);
        }
        return listaBitacora;
    }
}
