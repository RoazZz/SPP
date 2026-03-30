package interfaces;
import logica.dto.ReporteDTO;

import java.util.List;

public interface ReporteDAOInterfaz {
    public void agregarReporte(ReporteDTO reporte) throws Exception;
    public void actualizarReporte(ReporteDTO reporte) throws Exception;
    public ReporteDTO buscarReportePorId(int idReporte) throws Exception;
    public List<ReporteDTO> buscarTodosReporte() throws Exception;
}
