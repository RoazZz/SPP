package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.BuzonDAOInterfaz;
import logica.dto.BuzonDTO;

import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BuzonDAO implements BuzonDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO Buzon (idUsuario) VALUES (?)";
    private static final String SQL_BUSCAR_POR_ID_USUARIO = "SELECT idBuzon, idUsuario FROM Buzon WHERE idUsuario = ?";

    private final Connection conexion;
    private static final Logger logger = Logger.getLogger(BuzonDAO.class.getName());

    public BuzonDAO() throws DAOExcepcion {
        try {
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al leer archivo de configuración", e);
            throw new DAOExcepcion("Error de configuracion", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error de conexion SQL en BuzonDAO", e);
            throw new DAOExcepcion("Error de base de datos", e);
        }
    }

    public BuzonDAO(Connection conexion) {
        this.conexion = conexion;
    }


    @Override
    public boolean agregarBuzon(BuzonDTO buzonDTO) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, buzonDTO.getIdUsuario());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                buzonDTO.setIdBuzon(resultSet.getInt(1));
            }
            logger.log(Level.INFO, "Buzon agregado exitosamente para idUsuario: " + buzonDTO.getIdUsuario());
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al agregar buzon", e);
            throw new DAOExcepcion("Error al agregar buzon: ", e);
        }
    }

    @Override
    public BuzonDTO obtenerBuzonPorIdUsuario(int idUsuario) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_ID_USUARIO)) {
            preparedStatement.setInt(1, idUsuario);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new BuzonDTO(
                        resultSet.getInt("idBuzon"),
                        resultSet.getInt("idUsuario")
                );
            } else {
                logger.log(Level.WARNING, "No se encontró buzon para idUsuario: " + idUsuario);
                throw new EntidadNoEncontradaExcepcion("No existe buzon para idUsuario: " + idUsuario);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener buzon", e);
            throw new DAOExcepcion("Error al obtener buzon: ", e);
        }
    }
}
