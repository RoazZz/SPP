package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.PlanDeActividadesDTO;
import java.util.List;

public interface PlanDeActivadesDAOInterfaz {
    PlanDeActividadesDTO agregarPlanDeActividades(PlanDeActividadesDTO planDeActividadesDTO) throws DAOExcepcion;
    boolean actualizarPlanDeActividades(PlanDeActividadesDTO planDeActividadesDTO) throws DAOExcepcion;
    List<PlanDeActividadesDTO> obtenerPlanesDeActividadesPorMatricula(String matricula) throws DAOExcepcion;
    List<PlanDeActividadesDTO> obtenerTodosLosPlanesDeActividades() throws DAOExcepcion;
    PlanDeActividadesDTO obtenerPlanDeActividadesPorId(int idPlanDeActividades) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
}
