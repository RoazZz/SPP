package interfaces;

import logica.dto.SolicitaProyectoDTO;
import logica.enums.TipoEstado;
import logica.enums.TipoEstadoSolicitud;

import java.util.List;

public interface SolicitaProyectoDAOInterfaz {
    public void insertarSolicitudProyecto(SolicitaProyectoDTO solicitaProyectoDTO) throws Exception;
    public void actualizarSolicitudProyecto(SolicitaProyectoDTO solicitaProyectoDTO) throws Exception;
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorMatricula(String matricula) throws Exception;
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorIdProyecto(int idProyecto) throws Exception;
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorPeriodo(String periodo) throws Exception;
    public List<SolicitaProyectoDTO> obtenerTodasLasSolicitudesProyecto() throws Exception;

}
