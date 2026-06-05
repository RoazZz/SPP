package logica.interfaces;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.CalificacionFinalDTO;

public interface CalificacionFinalDAOInterfaz {
    boolean guardarCalificacionFinal(CalificacionFinalDTO calificacion) throws DAOExcepcion;
    CalificacionFinalDTO buscarPorMatricula(String matricula) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
}
