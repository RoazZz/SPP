package logica.dao;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.CoordinadorDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.CoordinadorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoordinadorDAO implements CoordinadorDAOInterfaz{
    private final Connection conexion;
    private static final Logger logger = Logger.getLogger(CoordinadorDAO.class.getName());
    private static final String SQL_INSERT  = "INSERT INTO Coordinador (idUsuario, NumeroDePersonal) VALUES (?, ?)";
    private static final String SQL_UPDATE = "UPDATE Coordinador SET NumeroDePersonal = ? WHERE idUsuario = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoP, usuario.apellidoM, " +
            "usuario.contrasenia, usuario.tipoUsuario, usuario.estado, " +
            "coordinador.NumeroDePersonal " +
            "FROM usuario JOIN coordinador ON usuario.idUsuario = coordinador.idUsuario";

    public CoordinadorDAO() throws DAOExcepcion {
        try {
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error al leer archivo de configuración", e);
            throw new DAOExcepcion("Error de configuracion", e);
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error de conexion SQL en CoordinadorDAO", e);
            throw new DAOExcepcion("Error de base de datos", e);
        }
    }

    @Override
    public void agregarCoordinador(CoordinadorDTO coordinador) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        UsuarioDAO usuarioDAO = new UsuarioDAO(this.conexion);
        try {
            conexion.setAutoCommit(false);
            usuarioDAO.agregarUsuario(coordinador);
            int idGenerado = coordinador.getIdUsuario();

            if (idGenerado > 0) {
                try (PreparedStatement preparedStatements = conexion.prepareStatement(SQL_INSERT)) {
                    preparedStatements.setInt(1, idGenerado);
                    preparedStatements.setString(2, coordinador.getNumeroPersonal());
                    preparedStatements.executeUpdate();
                }
                conexion.commit();
                logger.log(Level.INFO, "Coordinador agregado exitosamente: " + coordinador.getNumeroPersonal());
            } else {
                logger.log(Level.SEVERE, "No se pudo crear usuario base para coordinador");
                throw new EntidadNoEncontradaExcepcion( "No se pudo crear el usuario base");
            }
        } catch (SQLException e) {
            try {
                conexion.rollback();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error al hacer rollback", ex);
            }
            logger.log(Level.SEVERE, "Error al agregar coordinador", e);
            throw new DAOExcepcion("Error al agregar coordinador: ", e);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error al restaurar AutoCommit", ex);
            }
        }
    }

    @Override
    public void actualizarCoordinador(CoordinadorDTO coordinador) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1,coordinador.getNumeroPersonal());
            preparedStatement.setInt(2, coordinador.getIdUsuario());
            preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Coordinador actualizado correctamente: " + coordinador.getNumeroPersonal());
        } catch (SQLException e){
            logger.log(Level.SEVERE, "Error al actualizar al Coordinador", e);
            throw new DAOExcepcion("Error al actualizar al Coordinador: ", e);
        }
    }

    @Override
    public List<CoordinadorDTO> listarCoordinador() throws DAOExcepcion {
        List<CoordinadorDTO> listaCoordinador = new ArrayList<>();
        try {
            try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    CoordinadorDTO coordinador = new CoordinadorDTO(
                            resultSet.getInt("idUsuario"),
                            resultSet.getString("nombre"),
                            resultSet.getString("apellidoP"),
                            resultSet.getString("apellidoM"),
                            resultSet.getString("contrasenia"),
                            TipoEstado.valueOf(resultSet.getString("estado")),
                            TipoDeUsuario.valueOf(resultSet.getString("tipoUsuario")),
                            resultSet.getString("NumeroDePersonal")
                    );
                    listaCoordinador.add(coordinador);
                }
            }
            return listaCoordinador;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al listar a los coordinadores", e);
            throw new DAOExcepcion("Error al listar los coordinadores: ", e);
        }
    }
}
