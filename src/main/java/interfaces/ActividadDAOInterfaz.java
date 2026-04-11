package interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.ActividadDTO;

import java.util.List;

public interface ActividadDAOInterfaz {
    public void agregarActividad (ActividadDTO actividad) throws DAOExcepcion;
    public void actualizarProyecto (ActividadDTO actividad) throws DAOExcepcion;
    public ActividadDTO buscarActividadPorIdActividad (int idActividad) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<ActividadDTO> listarActividades() throws DAOExcepcion;
}
