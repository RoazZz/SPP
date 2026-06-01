package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.ReporteDTO;
import logica.enums.EstadoReporte;
import logica.enums.TipoReporte;

import java.util.List;

public interface ReporteDAOInterfaz {
    ReporteDTO agregarReporte(ReporteDTO reporte) throws DAOExcepcion;
    boolean actualizarReporte(ReporteDTO reporte) throws DAOExcepcion;
    ReporteDTO buscarReportePorId(int idReporte) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    List<ReporteDTO> listarTodosReporte() throws DAOExcepcion;
    boolean existeDuplicado(int idUsuario, TipoReporte tipo, String mes, EstadoReporte estado) throws DAOExcepcion;
}
