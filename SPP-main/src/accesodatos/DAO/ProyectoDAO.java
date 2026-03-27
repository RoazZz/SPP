package accesodatos.DAO;

import accesodatos.ConexionBD;
import accesodatos.DTO.ProyectoDTO;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProyectoDAO extends ConexionBD {

    private static final Logger logger = Logger.getLogger(ProyectoDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO Proyecto (idProyecto, idOrganizacion, numeroDePersonal, Nombre, Descripcion) VALUES (?, ?, ?, ?, ?)";

    public ProyectoDAO() {
        super();
    }

    public boolean insertar(ProyectoDTO proyecto) {
        if (conexion == null) {
            logger.log(Level.SEVERE, "Error en la conexion a la base de datos.");
            return false;
        }

        try (PreparedStatement statement = conexion.prepareStatement(SQL_INSERT)) {

            statement.setInt(1, proyecto.getIdProyecto());
            statement.setString(2, proyecto.getIdOrganizacion());
            statement.setString(3, proyecto.getNumeroDePersonal());
            statement.setString(4, proyecto.getNombre());
            statement.setString(5, proyecto.getDescripcion());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al intentar insertar el proyecto: " + proyecto.getIdProyecto(), e);
            return false;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error inesperado", e);
            throw new RuntimeException(e);
        }
    }
}