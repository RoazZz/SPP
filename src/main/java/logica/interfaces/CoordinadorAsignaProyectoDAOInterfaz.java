package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.CoordinadorAsignaProyectoDTO;
import java.util.List;

public interface CoordinadorAsignaProyectoDAOInterfaz {
    void insertarAsignacionDeProyecto(CoordinadorAsignaProyectoDTO coordinadorAsignaProyectoDTO) throws DAOExcepcion;

    void actualizarAsignacionDeProyecto(CoordinadorAsignaProyectoDTO coordinadorAsignaProyectoDTO) throws DAOExcepcion;

    List<CoordinadorAsignaProyectoDTO> obtenerAsignacionDeProyectoPorNumeroDePersonal(String numeroDePersonal) throws DAOExcepcion, EntidadNoEncontradaExcepcion;

    List<CoordinadorAsignaProyectoDTO> obtenerAsignacionDeProyectoPorIdSeccion(int idSeccion) throws DAOExcepcion, EntidadNoEncontradaExcepcion;

    List<CoordinadorAsignaProyectoDTO> obtenerTodasLasAsignacionesDeProyecto() throws DAOExcepcion;
}
