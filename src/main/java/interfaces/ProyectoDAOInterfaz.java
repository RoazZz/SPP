package interfaces;

import excepciones.DAOExcepcion;
import logica.dto.ProyectoDTO;
import java.util.List;

public interface ProyectoDAOInterfaz {
    public void agregarProyecto(ProyectoDTO proyecto) throws DAOExcepcion;
    public void actualizarProyecto (ProyectoDTO proyecto) throws DAOExcepcion;
    public ProyectoDTO buscarProyectoPorIdProyecto(int idProyecto) throws DAOExcepcion;
    public List<ProyectoDTO> listarProyectos() throws DAOExcepcion;
}
