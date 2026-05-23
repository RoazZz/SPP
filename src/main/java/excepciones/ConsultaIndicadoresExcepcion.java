package excepciones;

public class ConsultaIndicadoresExcepcion extends Exception {

    public ConsultaIndicadoresExcepcion(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}