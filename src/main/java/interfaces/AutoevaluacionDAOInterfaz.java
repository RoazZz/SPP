package interfaces;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.AutoevaluacionDTO;

import java.util.List;

public interface AutoevaluacionDAOInterfaz {
    public void agregarAutoevalaucion(AutoevaluacionDTO autoevaluacion) throws DAOExcepcion;
    public void actualizarAutoevaluacion(AutoevaluacionDTO autoevaluacion) throws DAOExcepcion;
    public AutoevaluacionDTO buscarAutoevaluacionPorMatricula(String matricula) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<AutoevaluacionDTO> obtenerTodasLasAutoevaluaciones() throws DAOExcepcion;
}
