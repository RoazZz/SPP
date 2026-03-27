package interfaces;
import logica.dto.AutoevaluacionDTO;

public interface InterAutoevaluacionDAO {
    void agregar(AutoevaluacionDTO autoevaluacion) throws Exception;
    void actualizar(AutoevaluacionDTO autoevaluacion) throws Exception;
    AutoevaluacionDTO buscarPorMatricula(String matricula) throws Exception;
}
