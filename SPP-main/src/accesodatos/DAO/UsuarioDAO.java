package accesodatos.DAO;

import accesodatos.ConexionBD;
import accesodatos.DTO.UsuarioDTO;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioDAO extends ConexionBD {

    private static final Logger logger = Logger.getLogger(UsuarioDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO Usuario (idUsuario, nombre, apellidoP, apellidoM, contrasenia) VALUES (?, ?, ?, ?, ?)";

    public UsuarioDAO() {
        super();
    }

    public boolean insertar(UsuarioDTO usuario) {
        if (conexion == null) {
            logger.log(Level.SEVERE, "Error en la conexion a la base de datos.");
            return false;
        }

        try (PreparedStatement statement = conexion.prepareStatement(SQL_INSERT)) {

            statement.setInt(1, usuario.getIdUsuario());
            statement.setString(2, usuario.getNombre());
            statement.setString(3, usuario.getApellidoPaterno());
            statement.setString(4, usuario.getApellidoMaterno());
            statement.setString(5, usuario.getContrasenia());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al intentar insertar el usuario: " + usuario.getIdUsuario(), e);
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado", e);
            throw new RuntimeException(e);
        }
    }
}