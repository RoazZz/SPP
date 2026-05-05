package excepciones;

public class AutenticacionDeUsuarioExcepcion extends RuntimeException {
    public AutenticacionDeUsuarioExcepcion(String mensaje) {
        super(mensaje);
    }
}
