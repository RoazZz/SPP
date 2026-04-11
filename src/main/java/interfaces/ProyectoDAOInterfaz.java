package interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.ProyectoDTO;
import java.util.List;

public interface ProyectoDAOInterfaz {
    public void agregarProyecto(ProyectoDTO proyecto) throws DAOExcepcion;
    public void actualizarProyecto (ProyectoDTO proyecto) throws DAOExcepcion;
    public ProyectoDTO buscarProyectoPorIdProyecto(int idProyecto) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<ProyectoDTO> listarProyectos() throws DAOExcepcion;
}
