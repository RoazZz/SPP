package logica.dao;

import interfaces.UsuarioDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.UsuarioDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UsuarioDAO extends ConexionBD implements UsuarioDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO Usuario(Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID_USUARIO = "SELECT * FROM Usuario WHERE idUsuario = ?";
    private static final String SQL_UPDATE = "UPDATE Usuario SET Nombre = ?, ApellidoP = ?, ApellidoM = ?, Contrasenia = ?, TipoUsuario = ? WHERE NumeroDePersonal = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Usuario";

    public UsuarioDAO() {
        super();
    }

    public UsuarioDAO(Connection conexionExistente) {
        this.conexion = conexionExistente;
    }

    @Override
    public void agregarUsuario(UsuarioDTO usuario) throws Exception {
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
        } catch (SQLException e) {
            throw new Exception("Error al insertar usuario: " + e.getMessage());
        }
    }

    @Override
    public void actualizarUsuario(UsuarioDTO usuario) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setInt(1, usuario.getIdUsuario());
            preparedStatement.setString(2, usuario.getNombre());
            preparedStatement.setString(3, usuario.getApellidoPaterno());
            preparedStatement.setString(4, usuario.getApellidoMaterno());
            preparedStatement.setString(5, usuario.getContrasenia());
            preparedStatement.setString(6, usuario.getTipoEstado().name());
            preparedStatement.setString(7, usuario.getTipoDeUsuario().name());
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            throw new Exception("Error al actualizar al usuario: " + e.getMessage());
        }
    }

    @Override
    public UsuarioDTO buscarUsuarioPorIdUsuario(int idUsuario) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_ID_USUARIO)){
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
                return null;
            }
        } catch (SQLException e){
            throw new Exception("Error al buscar al Usuario: " + e.getMessage());
        }
    }

    @Override
    public List<UsuarioDTO> listarUsuarios() throws Exception {
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
            throw new Exception("Error al listar a los usuarios: " + e.getMessage());
        }
    }
}