package excepciones;

public class EntidadNoCreadaExcepcion extends RuntimeException {
    public EntidadNoCreadaExcepcion(String message) {
        super(message);
    }
}
