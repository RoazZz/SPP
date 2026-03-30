package accesodatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionBD {
    public Connection conexion;
    private static final Logger logger = Logger.getLogger(ConexionBD.class.getName());

    public ConexionBD() {
        String controlador = "com.mysql.cj.jdbc.Driver";
        String usuario = "RoazAccess";
        String contrasenia = "nugRJa1105!";
        String basedatos = "sppbd";
        String servidor = "jdbc:mysql://localhost:3306/" + basedatos;

        try{
            Class.forName(controlador);
            conexion = DriverManager.getConnection(servidor, usuario, contrasenia);
        }catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error al establecer conexión con la base de datos", ex);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "No se encontró el driver JDBC", ex);
        }
    }
}