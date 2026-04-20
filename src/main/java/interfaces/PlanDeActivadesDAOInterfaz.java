package interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.PlanDeActividadesDTO;
import java.util.List;

public interface PlanDeActivadesDAOInterfaz {
    public PlanDeActividadesDTO agregarPlanDeActividades(PlanDeActividadesDTO planDeActividadesDTO) throws DAOExcepcion;
    public boolean actualizarPlanDeActividades(PlanDeActividadesDTO planDeActividadesDTO) throws DAOExcepcion;
    public List<PlanDeActividadesDTO> obtenerPlanesDeActividadesPorMatricula(String matricula) throws DAOExcepcion;
    public List<PlanDeActividadesDTO> obtenerTodosLosPlanesDeActividades() throws DAOExcepcion;
    public PlanDeActividadesDTO obtenerPlanDeActividadesPorId(int idPlanDeActividades) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
}
