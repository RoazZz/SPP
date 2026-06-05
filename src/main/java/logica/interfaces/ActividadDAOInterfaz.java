package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.ActividadDTO;

import java.util.List;

public interface ActividadDAOInterfaz {
    boolean registrarActividad(ActividadDTO actividad) throws DAOExcepcion;
    boolean actualizarActividad(ActividadDTO actividad) throws DAOExcepcion;
    ActividadDTO buscarActividadPorIdActividad(int idActividad) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    List<ActividadDTO> listarActividadesPorMatricula(String matricula) throws DAOExcepcion;
    List<ActividadDTO> listarActividades() throws DAOExcepcion;
}