package interfaces;

import logica.dto.ActividadDTO;

import java.util.List;

public interface ActividadDAOInterfaz {
    public void agregarActividad (ActividadDTO actividad) throws Exception;
    public void actualizarProyecto (ActividadDTO actividad) throws Exception;
    public ActividadDTO buscarActividadPorIdActividad (int idActividad) throws Exception;
    public List<ActividadDTO> listarActividades() throws Exception;
}
