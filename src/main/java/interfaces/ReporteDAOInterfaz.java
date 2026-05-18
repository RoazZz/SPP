package interfaces;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.ReporteDTO;
import logica.enums.EstadoReporte;
import logica.enums.TipoReporte;

import java.util.List;

public interface ReporteDAOInterfaz {
    public ReporteDTO agregarReporte(ReporteDTO reporte) throws DAOExcepcion;
    public boolean actualizarReporte(ReporteDTO reporte) throws DAOExcepcion;
    public ReporteDTO buscarReportePorId(int idReporte) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<ReporteDTO> listarTodosReporte() throws DAOExcepcion;
    public boolean existeDuplicado(int idUsuario, TipoReporte tipo, String mes, EstadoReporte estado) throws DAOExcepcion;
    public boolean existeHashDuplicado(String hashArchivo, String hashContenido) throws DAOExcepcion;
}
