import accesodatos.ConexionBD;

public class PruebaConexion {
    public static void main(String[] args) {
        ConexionBD conexion = new ConexionBD();

        if (conexion.conexion != null) {
            System.out.println("¡Conexión exitosa!");
        } else {
            System.err.println("Falló la conexión.");
        }
    }
}
