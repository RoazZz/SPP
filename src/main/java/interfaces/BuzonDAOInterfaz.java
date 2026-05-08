package interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.BuzonDTO;

public interface BuzonDAOInterfaz {
    boolean agregarBuzon(BuzonDTO buzonDTO) throws DAOExcepcion;
    BuzonDTO obtenerBuzonPorIdUsuario(int idUsuario) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
}
