package interfaces;

import logica.dto.PlanDeActividadesDTO;
import java.util.List;

public interface PlanDeActivadesDAOInterfaz {
    public void agregarPlanDeActividades(PlanDeActividadesDTO planDeActividadesDTO) throws Exception;
    public void actualizarPlanDeActividades(PlanDeActividadesDTO planDeActividadesDTO) throws Exception;
    public List<PlanDeActividadesDTO> obtenerPlanesDeActividadesPorMatricula(String matricula) throws Exception;
    public List<PlanDeActividadesDTO> obtenerTodosLosPlanesDeActividades() throws Exception;
    public PlanDeActividadesDTO obtenerPlanDeActividadesPorId(int idPlanDeActividades) throws Exception;
}
