package interfaces;

import logica.dto.ProyectoDTO;
import java.util.List;

public interface ProyectoDAOInterfaz {
    public void agregarProyecto(ProyectoDTO proyecto) throws Exception;
    public void actualizarProyecto (ProyectoDTO proyecto) throws Exception;
    public ProyectoDTO buscarProyectoPorIdProyecto(int idProyecto) throws Exception;
    public List<ProyectoDTO> listarProyectos() throws Exception;
}
