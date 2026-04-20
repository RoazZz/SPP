package interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.ActividadDTO;

import java.util.List;

public interface ActividadDAOInterfaz {
    public boolean agregarActividad (ActividadDTO actividad) throws DAOExcepcion;
    public boolean actualizarActividad (ActividadDTO actividad) throws DAOExcepcion;
    public ActividadDTO buscarActividadPorIdActividad (int idActividad) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<ActividadDTO> listarActividades() throws DAOExcepcion;
}
