package interfaces;
import logica.dto.AutoevaluacionDTO;

public interface AutoevaluacionDAOInterfaz {
    public void agregar(AutoevaluacionDTO autoevaluacion) throws Exception;
    public void actualizar(AutoevaluacionDTO autoevaluacion) throws Exception;
    public AutoevaluacionDTO buscarPorMatricula(String matricula) throws Exception;
}
