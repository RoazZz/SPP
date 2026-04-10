package accesodatos;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionBD{
    private static ConexionBD instancia;
    private String ENLACE;
    private String USUARIO;
    private String CONTRASEÑA;
    private Connection conexion = null;

    private static final Logger logger = Logger.getLogger(ConexionBD.class.getName());

    private ConexionBD() throws IOException, SQLException {
        Properties properties = new Properties();
        try(FileInputStream fileInputStream = new FileInputStream("config.properties")) {
            properties.load(fileInputStream);
            this.ENLACE = properties.getProperty("db.enlace");
            this.USUARIO = properties.getProperty("db.usuario");
            this.CONTRASEÑA = properties.getProperty("db.contraseña");
            conexion = DriverManager.getConnection(ENLACE, USUARIO, CONTRASEÑA);
            logger.log(Level.INFO, "Conexión exitosa a la base de datos");
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error al leer el archivo de configuración: " + e.getMessage(), e);
            throw e;
        }catch (SQLException e){
            logger.log(Level.SEVERE, "Error al conectar a la base de datos: " + e.getMessage(), e);
            throw e;
        }
    }

    public static ConexionBD obtenerInstancia() throws IOException, SQLException {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public Connection obtenerConexion() {
        return conexion;
    }

    public void cerrarConexion(){
        if(conexion != null){
            try{
                conexion.close();
                instancia = null;
                logger.log(Level.INFO, "Conexión cerrada correctamente");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al cerrar la conexión", e);
            }
        }
    }

}