package accesodatos;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionBD {
    private static ConexionBD instancia;
    private String enlace;
    private String usuario;
    private String contrasenia;
    private static Connection conexion = null;

    private static final Logger REGISTRADOR = Logger.getLogger(ConexionBD.class.getName());

    private ConexionBD() throws IOException, SQLException {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) {
                throw new FileNotFoundException("Archivo config.properties no encontrado en recursos");
            }
            properties.load(inputStream);
            this.enlace = System.getProperty("db.enlace", properties.getProperty("db.enlace"));
            this.usuario = System.getProperty("db.usuario", properties.getProperty("db.usuario"));
            this.contrasenia = System.getProperty("db.contrasenia", properties.getProperty("db.contrasenia"));

            conexion = DriverManager.getConnection(enlace, usuario, contrasenia);
            REGISTRADOR.log(Level.INFO, "Conexión exitosa a la base de datos");
        } catch (IOException ioException) {
            REGISTRADOR.log(Level.SEVERE, "Error al cargar configuración", ioException);
            throw ioException;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al establecer conexión con la base de datos", sqlException);
            throw sqlException;
        }
    }

    public static ConexionBD obtenerInstancia() throws IOException, SQLException {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }


    public static Connection obtenerConexion() {
        return conexion;
    }

    public void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                instancia = null;
                REGISTRADOR.log(Level.INFO, "Conexión cerrada correctamente");
            } catch (SQLException sqlException) {
                REGISTRADOR.log(Level.SEVERE, "Error al cerrar la conexión", sqlException);
            }
        }
    }

    public static void reset() {
        if (instancia != null) {
            instancia.cerrarConexion();
            instancia = null;
        }
    }
}