package interfaces;

import excepciones.DAOExcepcion;
import logica.dto.SolicitaProyectoDTO;

import java.util.List;

public interface SolicitudProyectoDAOInterfaz {
    public SolicitaProyectoDTO insertarSolicitudProyecto(SolicitaProyectoDTO solicitaProyectoDTO) throws DAOExcepcion;
    public boolean actualizarSolicitudProyecto(SolicitaProyectoDTO solicitaProyectoDTO) throws DAOExcepcion;
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorMatricula(String matricula) throws DAOExcepcion;
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorIdProyecto(int idProyecto) throws DAOExcepcion;
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorPeriodo(String periodo) throws DAOExcepcion;
    public List<SolicitaProyectoDTO> obtenerTodasLasSolicitudesProyecto() throws DAOExcepcion;

}
