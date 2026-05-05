package interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.CoordinadorDAO;
import logica.dto.CoordinadorDTO;
import logica.dto.ProfesorDTO;

import java.util.List;

public interface CoordinadorDAOInterfaz {
    public boolean agregarCoordinador (CoordinadorDTO coordinador) throws DAOExcepcion;
    public boolean actualizarCoordinador (CoordinadorDTO coordinador) throws DAOExcepcion;
    public CoordinadorDTO buscarCoordinadorPorNumeroDePersonal(String numeroPersonal) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<CoordinadorDTO> listarCoordinador() throws DAOExcepcion;
}
