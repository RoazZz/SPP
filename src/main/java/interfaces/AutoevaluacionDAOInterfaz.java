package interfaces;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.AutoevaluacionDTO;

import java.util.List;

public interface AutoevaluacionDAOInterfaz {
    public AutoevaluacionDTO agregarAutoevalaucion(AutoevaluacionDTO autoevaluacion) throws DAOExcepcion;
    public boolean actualizarAutoevaluacion(AutoevaluacionDTO autoevaluacion) throws DAOExcepcion;
    public AutoevaluacionDTO buscarAutoevaluacionPorMatricula(String matricula) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<AutoevaluacionDTO> obtenerTodasLasAutoevaluaciones() throws DAOExcepcion;
}
