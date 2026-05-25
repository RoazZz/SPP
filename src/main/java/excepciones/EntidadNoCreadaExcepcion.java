package excepciones;

public class EntidadNoCreadaExcepcion extends RuntimeException {
    public EntidadNoCreadaExcepcion(String mensaje) {
        super(mensaje);
    }
}
