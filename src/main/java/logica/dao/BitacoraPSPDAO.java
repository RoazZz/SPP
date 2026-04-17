package logica.dao;
import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.BitacoraPSPDAOInterfaz;
import logica.dto.BitacoraPSPDTO;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BitacoraPSPDAO implements BitacoraPSPDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO bitacorapsp(Matricula, Fecha) VALUES (?, ?)"; //FALTA RUTA O NOMBRE DEL ARCHIVO
    private static final String SQL_SELECT_BY_IDBITACORA = "SELECT * FROM bitacorapsp WHERE idBitacoraPSP = ?";
    private static final String SQL_UPDATE = "UPDATE bitacorapsp SET Matricula = ?, Fecha = ? WHERE idBitacoraPSP = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM bitacorapsp";

    private Connection conexion;
    private static final Logger logger = Logger.getLogger(BitacoraPSPDAO.class.getName());

    public BitacoraPSPDAO() throws DAOExcepcion {
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error al leer archivo de cofniguración", e);
            throw new DAOExcepcion("Error de configuracion", e);
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error de conexion SQL en BitacoraPSPDAO", e);
            throw new DAOExcepcion("Error de base de datos", e);
        }
    }

    @Override
    public void agregarBitacoraPSP(BitacoraPSPDTO bitacora) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, bitacora.getMatricula());
            preparedStatement.setDate(2, java.sql.Date.valueOf(bitacora.getFecha()));
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    bitacora.setIdBBitacora(resultSet.getInt(1));
                }
            }
            logger.log(Level.INFO, "Bitacora PSP agregada con éxito. ID: " + bitacora.getIdBBitacora());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al agregar bitacora PSP", e);
            throw new DAOExcepcion("Error al agregar bitácora PSP", e);
        }
    }

    @Override
    public BitacoraPSPDTO buscarBitacoraPSPPorId(int idBitacora) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_IDBITACORA)) {
            preparedStatement.setInt(1, idBitacora);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new BitacoraPSPDTO(
                            resultSet.getInt("idBitacoraPSP"),
                            resultSet.getString("Matricula"),
                            resultSet.getDate("Fecha").toLocalDate()
                    );
                }else{
                    logger.log(Level.WARNING, "No se encontró bitacoraPSP con ID: " + idBitacora);
                    throw new EntidadNoEncontradaExcepcion("BitacoraPSP no encontrado con ID: " + idBitacora);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al buscar bitacora PSP por ID: " + idBitacora, e);
            throw new DAOExcepcion("Error al buscar bitácora PSP por ID", e);
        }
    }

    @Override
    public void actualizarBitacoraPSP(BitacoraPSPDTO bitacora) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
                preparedStatement.setString(1, bitacora.getMatricula());
                preparedStatement.setDate(2, java.sql.Date.valueOf(bitacora.getFecha()));
                preparedStatement.setInt(3, bitacora.getIdBBitacora());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al actualizar bitacora PSP", e);
            throw new DAOExcepcion("Error al actualizar bitácora PSP", e);
        }
    }

    public List<BitacoraPSPDTO> listarBitacorasPSP() throws DAOExcepcion{
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
            logger.log(Level.SEVERE, "Error SQL al listar bitacoras PSP", e);
            throw new DAOExcepcion("Error al listar bitácoras PSP", e);
        }
    }
}
