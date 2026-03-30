package interfaces;
import logica.dto.AutoevaluacionDTO;

import java.util.List;

public interface AutoevaluacionDAOInterfaz {
    public void agregarAutoevalaucion(AutoevaluacionDTO autoevaluacion) throws Exception;
    public void actualizarAutoevaluacion(AutoevaluacionDTO autoevaluacion) throws Exception;
    public AutoevaluacionDTO buscarAutoevaluacionPorMatricula(String matricula) throws Exception;
    public List<AutoevaluacionDTO> obtenerTodasLasAutoevaluaciones() throws Exception;
}
