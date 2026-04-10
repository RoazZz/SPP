package excepciones;

public class DAOExcepcion extends Exception {
    public DAOExcepcion(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
