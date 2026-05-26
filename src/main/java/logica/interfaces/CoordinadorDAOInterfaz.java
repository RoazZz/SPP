package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.CoordinadorDTO;


import java.util.List;

public interface CoordinadorDAOInterfaz {
    boolean agregarCoordinador (CoordinadorDTO coordinador) throws DAOExcepcion;
    boolean actualizarCoordinador (CoordinadorDTO coordinador) throws DAOExcepcion;
    CoordinadorDTO buscarCoordinadorPorNumeroDePersonal(String numeroPersonal) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    List<CoordinadorDTO> listarCoordinador() throws DAOExcepcion;
    boolean existeCoordinadorConNumeroPersonal(String numeroPersonal, int idExcluir) throws DAOExcepcion;
}
