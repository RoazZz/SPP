package accesodatos;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionBD implements AutoCloseable {
    private String ENLACE;
    private String USUARIO;
    private String CONTRASEÑA;
    protected Connection conexion = null;

    private static final Logger logger = Logger.getLogger(ConexionBD.class.getName());

    public ConexionBD() throws IOException {
        Properties properties = new Properties();
        try(FileInputStream fileInputStream = new FileInputStream("config.properties")) {
            properties.load(fileInputStream);
            this.ENLACE = properties.getProperty("db.enlace");
            this.USUARIO = properties.getProperty("db.usuario");
            this.CONTRASEÑA = properties.getProperty("db.contraseña");
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error al leer el archivo de configuración: " + e.getMessage(), e);
        }
    }

    public Connection conectarBD() throws SQLException {
        try {
            conexion = DriverManager.getConnection(ENLACE, USUARIO, CONTRASEÑA);
            logger.info("Conexión exitosa a la base de datos.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al conectar a la base de datos: " + e.getMessage(), e);
        }
        return conexion;
    }

    @Override
    public void close(){
        if (conexion != null) {
            try {
                conexion.close();
                logger.info("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error al cerrar la conexión: " + e.getMessage(), e);
            }
        }
    }
}