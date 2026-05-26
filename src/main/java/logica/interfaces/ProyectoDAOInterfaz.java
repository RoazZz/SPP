package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.ProyectoDTO;
import java.util.List;

public interface ProyectoDAOInterfaz {
    void agregarProyecto(ProyectoDTO proyecto) throws DAOExcepcion;
    void actualizarProyecto (ProyectoDTO proyecto) throws DAOExcepcion;
    ProyectoDTO buscarProyectoPorIdProyecto(int idProyecto) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    List<ProyectoDTO> listarProyectos() throws DAOExcepcion;
}
