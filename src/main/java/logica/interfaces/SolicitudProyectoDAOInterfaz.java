package logica.interfaces;

import excepciones.DAOExcepcion;
import logica.dto.SolicitaProyectoDTO;

import java.util.List;

public interface SolicitudProyectoDAOInterfaz {
    SolicitaProyectoDTO insertarSolicitudProyecto(SolicitaProyectoDTO solicitaProyectoDTO) throws DAOExcepcion;
    boolean actualizarSolicitudProyecto(SolicitaProyectoDTO solicitaProyectoDTO) throws DAOExcepcion;
    List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorMatricula(String matricula) throws DAOExcepcion;
    List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorIdProyecto(int idProyecto) throws DAOExcepcion;
    List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorPeriodo(String periodo) throws DAOExcepcion;
    List<SolicitaProyectoDTO> obtenerTodasLasSolicitudesProyecto() throws DAOExcepcion;
    List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorProfesor(String numeroDePersonalProfesor, String periodo) throws DAOExcepcion;

}
