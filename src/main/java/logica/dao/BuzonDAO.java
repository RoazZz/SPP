package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.BuzonDAOInterfaz;
import logica.dto.BuzonDTO;

import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BuzonDAO implements BuzonDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO Buzon (idUsuario) VALUES (?)";
    private static final String SQL_BUSCAR_POR_ID_USUARIO = "SELECT idBuzon, idUsuario FROM Buzon WHERE idUsuario = ?";

    private final Connection conexion;
    private static final Logger REGISTRADOR = Logger.getLogger(BuzonDAO.class.getName());

    public BuzonDAO() throws DAOExcepcion {
        try {
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioException) {
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de configuración", ioException);
            throw new DAOExcepcion("Error de configuracion", ioException);
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexion SQL en BuzonDAO", sqlException);
            throw new DAOExcepcion("Error de base de datos", sqlException);
        }
    }

    public BuzonDAO(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public boolean agregarBuzon(BuzonDTO buzonDTO) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            sentenciaPreparada.setInt(1, buzonDTO.getIdUsuario());
            sentenciaPreparada.executeUpdate();
            ResultSet conjuntoResultado = sentenciaPreparada.getGeneratedKeys();
            if (conjuntoResultado.next()) {
                buzonDTO.setIdBuzon(conjuntoResultado.getInt(1));
            }
            REGISTRADOR.log(Level.INFO, "Buzon agregado exitosamente para idUsuario " + buzonDTO.getIdUsuario());
            return true;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al agregar buzon", sqlException);
            throw new DAOExcepcion("Error al agregar buzon ", sqlException);
        }
    }

    @Override
    public BuzonDTO obtenerBuzonPorIdUsuario(int idUsuario) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_BUSCAR_POR_ID_USUARIO)) {
            sentenciaPreparada.setInt(1, idUsuario);
            ResultSet conjuntoResultado = sentenciaPreparada.executeQuery();
            if (conjuntoResultado.next()) {
                return new BuzonDTO(
                        conjuntoResultado.getInt("idBuzon"),
                        conjuntoResultado.getInt("idUsuario")
                );
            } else {
                REGISTRADOR.log(Level.WARNING, "No se encontró buzon para idUsuario " + idUsuario);
                throw new EntidadNoEncontradaExcepcion("No existe buzon para idUsuario " + idUsuario);
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al obtener buzon", sqlException);
            throw new DAOExcepcion("Error al obtener buzon: ", sqlException);
        }
    }
}
