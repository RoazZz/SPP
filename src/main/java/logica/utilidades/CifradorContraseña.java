package logica.utilidades;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CifradorContraseña {
    public static String cifrarContraseña(String contraseña){
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(contraseña.getBytes(StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                stringBuilder.append(String.format("%02x", b));
            }
            return stringBuilder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al cifrar la contraseña", e);
        }
    }

    public static boolean verificarContraseña(String contraseñaSinCifrar, String contraseñaCifrada) {
        String contraseñaCifradaInput = cifrarContraseña(contraseñaSinCifrar);
        return contraseñaCifradaInput.equals(contraseñaCifrada);
    }
}
