package interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.CoordinadorAsignaProyectoDTO;
import java.util.List;

public interface CoordinadorAsignaProyectoDAOInterfaz {
    public void insertarAsignacionDeProyecto(CoordinadorAsignaProyectoDTO coordinadorAsignaProyectoDTO) throws DAOExcepcion;
    public void actualizarAsignacionDeProyecto (CoordinadorAsignaProyectoDTO coordinadorAsignaProyectoDTO) throws DAOExcepcion;
    public List<CoordinadorAsignaProyectoDTO> obtenerAsignacionDeProyectoPorNumeroDePersonal (String numeroDePersonal) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<CoordinadorAsignaProyectoDTO> obtenerAsignacionDeProyectoPorIdSeccion (int idSeccion) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<CoordinadorAsignaProyectoDTO> obtenerTodasLasAsignacionesDeProyecto () throws DAOExcepcion;
}
