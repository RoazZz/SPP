package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.SeccionDTO;

import java.util.List;

public interface SeccionDAOInterfaz {
    SeccionDTO agregarSeccion(SeccionDTO seccionDTO) throws DAOExcepcion;
    boolean actualizarSeccion(SeccionDTO seccionDTO) throws DAOExcepcion;
    SeccionDTO obtenerSeccionPorId(int idSeccion) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    List<SeccionDTO> obtenerTodasLasSecciones() throws DAOExcepcion;
}
