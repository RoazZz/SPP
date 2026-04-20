package interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.SeccionDTO;

import java.util.List;

public interface SeccionDAOInterfaz {
    public SeccionDTO agregarSeccion(SeccionDTO seccionDTO) throws DAOExcepcion;
    public boolean actualizarSeccion(SeccionDTO seccionDTO) throws DAOExcepcion;
    public SeccionDTO obtenerSeccionPorId(int idSeccion) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<SeccionDTO> obtenerTodasLasSecciones() throws DAOExcepcion;
}
