package interfaces;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.ReporteDTO;

import java.util.List;

public interface ReporteDAOInterfaz {
    public void agregarReporte(ReporteDTO reporte) throws DAOExcepcion;
    public void actualizarReporte(ReporteDTO reporte) throws DAOExcepcion;
    public ReporteDTO buscarReportePorId(int idReporte) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<ReporteDTO> listarTodosReporte() throws DAOExcepcion;
}
