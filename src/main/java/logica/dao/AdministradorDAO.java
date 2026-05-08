package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoCreadaExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.AdministradorDAOInterfaz;
import logica.dto.AdministradorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;

import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdministradorDAO implements AdministradorDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO administrador (idUsuario) VALUES (?)";
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoP, usuario.apellidoM, " +
                    "usuario.contrasenia, usuario.TipoUsuario, usuario.estado, " +
                    "administrador.idAdministrador" +
                    " FROM usuario JOIN administrador ON usuario.idUsuario = administrador.idUsuario " +
                    "WHERE administrador.idAdministrador = ?";
    private static final String SQL_BUSCAR_POR_NOMBRE =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoP, usuario.apellidoM, " +
                    "usuario.contrasenia, usuario.TipoUsuario, usuario.estado, " +
                    "administrador.idAdministrador" +
                    " FROM usuario JOIN administrador ON usuario.idUsuario = administrador.idUsuario " +
                    "WHERE usuario.nombre = ?";
    private Connection conexion;
    private static final Logger logger = Logger.getLogger(AdministradorDAO.class.getName());

    public AdministradorDAO() throws DAOExcepcion {
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error de entrada/salida al configurar la conexión", e);
            throw new DAOExcepcion("Error al leer la configuración de la base de datos", e);
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error de SQL al intentar conectar", e);
            throw new DAOExcepcion("Error de acceso a la base de datos", e);
        }
    }

    @Override
    public AdministradorDTO agregarAdministrador(AdministradorDTO admin) throws DAOExcepcion {
        UsuarioDAO usuarioDAO = new UsuarioDAO(this.conexion);
        try {
            conexion.setAutoCommit(false);

            usuarioDAO.agregarUsuario(admin);
            int idGenerado = admin.getIdUsuario();

            if (idGenerado > 0) {
                try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                    preparedStatement.setInt(1, idGenerado);
                    preparedStatement.executeUpdate();

                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int idAdminGenerado = generatedKeys.getInt(1);
                            admin.setIdAdministrador(idAdminGenerado);
                            logger.log(Level.INFO, "Admin vinculado con ID de Usuario: " + idGenerado + " y ID de Admin: " + idAdminGenerado);
                        }
                    }
                }
                conexion.commit();
                logger.log(Level.INFO, "Administrador agregado correctamente: " + admin.getIdUsuario());
                return admin;
            }else{
                logger.log(Level.WARNING, "Usuario base no generado para el Administrador");
                throw new EntidadNoCreadaExcepcion("Usuario base no creado correctamente");
            }
        } catch (SQLException e) {
            try {
                if (conexion != null){
                    conexion.rollback();
                }
            } catch (SQLException exRollback) {
                logger.log(Level.SEVERE, "Error al hacer rollback", exRollback);
            }
            logger.log(Level.SEVERE, "Error SQL al agregar administrador", e);
            throw new DAOExcepcion("Error al agregar administrador", e);
        } catch (Exception e) {
            try {
                if (conexion != null) {
                    conexion.rollback();
                }
            } catch (SQLException exRollback) {
                logger.log(Level.SEVERE, "Error al hacer rollback tras error inesperado", exRollback);
            }
            logger.log(Level.SEVERE, "Error no esperado en AdministradorDAO", e);
            throw new DAOExcepcion("Ocurrió un error inesperado al registrar el administrador", e);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "No se pudo resetear autocommit", e);
            }
        }
    }

    @Override
    public AdministradorDTO buscarAdministradorPorId(int idAdministrador) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_ID)) {
            preparedStatement.setInt(1, idAdministrador);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new AdministradorDTO(
                    resultSet.getInt("idUsuario"),
                    resultSet.getString("nombre"),
                    resultSet.getString("apellidoP"),
                    resultSet.getString("apellidoM"),
                    resultSet.getString("contrasenia"), TipoEstado.valueOf(resultSet.getString("estado")),
                    TipoDeUsuario.valueOf(resultSet.getString("TipoUsuario")),
                    resultSet.getInt("idAdministrador")
                    );
                } else {
                    logger.log(Level.INFO, "Administrador no encontrado con ID: " + idAdministrador);
                    throw new EntidadNoEncontradaExcepcion("Administrador no encontrado con ID: " + idAdministrador);
                }
            }
        }catch (SQLException e){
            logger.log(Level.SEVERE, "Error SQL al buscar administrador por ID", e);
            throw new DAOExcepcion("Error al buscar administrador por ID", e);
        }
    }

    @Override
    public AdministradorDTO buscarAdministradorPorNombre(String nombre) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_NOMBRE)) {
            preparedStatement.setString(1, nombre);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new AdministradorDTO(
                            resultSet.getInt("idUsuario"),
                            resultSet.getString("nombre"),
                            resultSet.getString("apellidoP"),
                            resultSet.getString("apellidoM"),
                            resultSet.getString("contrasenia"), TipoEstado.valueOf(resultSet.getString("estado")),
                            TipoDeUsuario.valueOf(resultSet.getString("TipoUsuario")),
                            resultSet.getInt("idAdministrador")
                    );
                } else {
                    logger.log(Level.INFO, "Administrador no encontrado con nombre: " + nombre);
                    throw new EntidadNoEncontradaExcepcion("Administrador no encontrado con nombre: " + nombre);
                }
            }
        }catch (SQLException e){
            logger.log(Level.SEVERE, "Error SQL al buscar administrador por nombre", e);
            throw new DAOExcepcion("Error al buscar administrador por nombre", e);
        }
    }


}