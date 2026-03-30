package interfaces;

import logica.dto.SeccionDTO;

import java.util.List;

public interface SeccionDAOInterfaz {
    public void agregarSeccion(SeccionDTO seccionDTO) throws Exception;
    public void actualizarSeccion(SeccionDTO seccionDTO) throws Exception;
    public SeccionDTO obtenerSeccionPorId(int idSeccion) throws Exception;
    public List<SeccionDTO> obtenerTodasLasSecciones() throws Exception;
}
