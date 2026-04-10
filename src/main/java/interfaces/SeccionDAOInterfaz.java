package interfaces;

import excepciones.DAOExcepcion;
import logica.dto.SeccionDTO;

import java.util.List;

public interface SeccionDAOInterfaz {
    public void agregarSeccion(SeccionDTO seccionDTO) throws DAOExcepcion;
    public void actualizarSeccion(SeccionDTO seccionDTO) throws DAOExcepcion;
    public SeccionDTO obtenerSeccionPorId(int idSeccion) throws DAOExcepcion;
    public List<SeccionDTO> obtenerTodasLasSecciones() throws DAOExcepcion;
}
