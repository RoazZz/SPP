package pruebasdao;

import accesodatos.ConexionBD;
import java.sql.Connection;

public class PruebaConexion {

    public static void main(String[] args) {
        try {
            ConexionBD instanciaBD = ConexionBD.obtenerInstancia();
            Connection conexion = instanciaBD.obtenerConexion();

            if (conexion != null && !conexion.isClosed()) {
                System.out.println("[ÉXITO] Conexión establecida correctamente");

                instanciaBD.cerrarConexion();
                System.out.println("[ÉXITO] Conexión cerrada con éxito.");

            } else {
                System.err.println("[ERROR] La conexión es nula o está cerrada.");
            }

        } catch (Exception e) {
            System.err.println("[FALLÓ] No se pudo conectar a la base de datos.");
            System.err.println("Causa del error: " + e.getMessage());

            if (e instanceof java.io.FileNotFoundException) {
                System.err.println("CONSEJO: No se encuentra el archivo 'config.properties'. Asegúrate de que esté en la raíz del proyecto.");
            } else if (e instanceof java.sql.SQLException) {
                System.err.println("CONSEJO: Revisa que el usuario/contraseña sean correctos y que el servidor MySQL esté encendido.");
            }

            e.printStackTrace();
        }
    }
}