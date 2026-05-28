package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.AutoevaluacionDTO;

import java.util.List;

public interface AutoevaluacionDAOInterfaz {
    AutoevaluacionDTO agregarAutoevaluacion(AutoevaluacionDTO autoevaluacion) throws DAOExcepcion;
    boolean actualizarAutoevaluacion(AutoevaluacionDTO autoevaluacion) throws DAOExcepcion;
    AutoevaluacionDTO buscarAutoevaluacionPorMatricula(String matricula) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    List<AutoevaluacionDTO> obtenerTodasLasAutoevaluaciones() throws DAOExcepcion;
}
