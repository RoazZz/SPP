package logica.dao;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.UsuarioDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.UsuarioDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;

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

public class UsuarioDAO implements UsuarioDAOInterfaz {
    private final Connection conexion;
    private static final Logger logger = Logger.getLogger(UsuarioDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO Usuario(Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID_USUARIO = "SELECT * FROM Usuario WHERE idUsuario = ?";
    private static final String SQL_UPDATE = "UPDATE Usuario SET Nombre = ?, ApellidoP = ?, ApellidoM = ?, Contrasenia = ?, TipoUsuario = ? WHERE NumeroDePersonal = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Usuario";

    public UsuarioDAO() throws DAOExcepcion {
        try {
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error al leer archivo de configuración", e);
            throw new DAOExcepcion("Error de configuracion", e);
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error de conexion SQL en UsuarioDAO", e);
            throw new DAOExcepcion("Error de base de datos", e);
        }
    }

    public UsuarioDAO(Connection conexionExistente) {
        this.conexion = conexionExistente;
    }

    @Override
    public void agregarUsuario(UsuarioDTO usuario) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, usuario.getNombre());
            preparedStatement.setString(2, usuario.getApellidoPaterno());
            preparedStatement.setString(3, usuario.getApellidoMaterno());
            preparedStatement.setString(4, usuario.getContrasenia());
            preparedStatement.setString(5, usuario.getTipoEstado().name());
            preparedStatement.setString(6, usuario.getTipoDeUsuario().name());

            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    usuario.setIdUsuario(resultSet.getInt(1));
                }
            }
            logger.log(Level.INFO, "Usuario base creado exitosamente: " + usuario.getIdUsuario());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al agregar usuario", e);
            throw new DAOExcepcion("Error al agregar usuario: ", e);
        }
    }

    @Override
    public void actualizarUsuario(UsuarioDTO usuario) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, usuario.getNombre());
            preparedStatement.setString(2, usuario.getApellidoPaterno());
            preparedStatement.setString(3, usuario.getApellidoMaterno());
            preparedStatement.setString(4, usuario.getContrasenia());
            preparedStatement.setString(5, usuario.getTipoDeUsuario().name());
            preparedStatement.setInt(6, usuario.getIdUsuario());
            preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Usuarios base actualizado exitosamente: " + usuario.getIdUsuario());
        } catch (SQLException e){
            logger.log(Level.SEVERE, "Error al actualizar al usuario", e);
            throw new DAOExcepcion("Error al actualizar al usuario: ", e);
        }
    }

    @Override
    public UsuarioDTO buscarUsuarioPorIdUsuario(int idUsuario) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_ID_USUARIO)){
            preparedStatement.setInt(1, idUsuario);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int idDeUsuario = resultSet.getInt("IdUsuario");
                String nombre = resultSet.getString("Nombre");
                String apellidoPaterno = resultSet.getString("ApellidoPaterno");
                String apellidoMaterno = resultSet.getString("ApellidoMaterno");
                String contrasenia  = resultSet.getString("Contrasenia");
                String estado = resultSet.getString("Estado");
                String tipoUsuario = resultSet.getString("TipoUsuario");
                return new UsuarioDTO(idDeUsuario, nombre, apellidoPaterno, apellidoMaterno, contrasenia, TipoEstado.valueOf(estado), TipoDeUsuario.valueOf(tipoUsuario));
            }else{
                logger.log(Level.WARNING, "No se encontro algun usuario con id: " + idUsuario);
                throw new EntidadNoEncontradaExcepcion("No existe usuario con el id: " + idUsuario);
            }
        } catch (SQLException e){
            logger.log(Level.SEVERE, "Error al buscar al usuario", e);
            throw new DAOExcepcion("Error al buscar al Usuario: ", e);
        }
    }

    @Override
    public List<UsuarioDTO> listarUsuarios() throws DAOExcepcion {
        List<UsuarioDTO> listaUsuario = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL); ResultSet resultSet = preparedStatement.executeQuery();) {
            while (resultSet.next()) {
                UsuarioDTO usuario = new UsuarioDTO(
                        resultSet.getInt("idUsuario"),
                        resultSet.getString("Nombre"),
                        resultSet.getString("ApellidoPaterno"),
                        resultSet.getString("ApellidoMaterno"),
                        resultSet.getString("Contrasenia"),
                        TipoEstado.valueOf(resultSet.getString("Estado")),
                        TipoDeUsuario.valueOf(resultSet.getString("TipoUsuario"))
                );
                listaUsuario.add(usuario);
            }
            return listaUsuario;
        } catch (SQLException e){
            logger.log(Level.SEVERE, "Error al listar a los usuarios", e);
            throw new DAOExcepcion("Error al listar a los usuarios: ", e);
        }
    }
}