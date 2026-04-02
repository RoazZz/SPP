package interfaces;

import logica.dto.CoordinadorAsignaProyectoDTO;
import java.util.List;

public interface CoordinadorAsignaProyectoDAOInterfaz {
    public void insertarAsignacionDeProyecto (CoordinadorAsignaProyectoDTO coordinadorAsignaProyectoDTO) throws Exception;
    public void actualizarAsigancionDeProyecto (CoordinadorAsignaProyectoDTO coordinadorAsignaProyectoDTO) throws Exception;
    public List<CoordinadorAsignaProyectoDTO> obtenerAsignacionDeProyectoPorNumeroDePersonal (String numeroDePersonal) throws Exception;
    public List<CoordinadorAsignaProyectoDTO> obtenerAsignacionDeProyectoPorIdSeccion (int idSeccion) throws Exception;
    public List<CoordinadorAsignaProyectoDTO> obtenerTodasLasAsignacionesDeProyecto () throws Exception;
}
