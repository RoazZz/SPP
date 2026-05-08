package excepciones;

public class ReglaDeNegocioExcepcion extends Exception {

    public ReglaDeNegocioExcepcion(String mensaje) {
        super(mensaje);
    }

    public ReglaDeNegocioExcepcion(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}