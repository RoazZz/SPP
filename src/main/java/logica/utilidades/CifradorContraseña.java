package logica.utilidades;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CifradorContraseña {
    private static final Logger LOGGER = Logger.getLogger(CifradorContraseña.class.getName());

    public static String cifrarContraseña(String contraseña){
        String hashResultante = "";
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(contraseña.getBytes(StandardCharsets.UTF_8));
            StringBuilder constructorHex = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                constructorHex.append(String.format("%02x", b));
            }
            hashResultante = constructorHex.toString();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "Error al cifrar", e);
        }
        return hashResultante;
    }

    public static boolean verificarContrasenia(String contraseniaSinCifrar, String contraseniaCifrada) {
        boolean esValida = false;
        String contraseniaCifradaInput = cifrarContraseña(contraseniaSinCifrar);
        if (contraseniaCifradaInput.equals(contraseniaCifrada)) {
            esValida = true;
        }
        return esValida;
    }
}
