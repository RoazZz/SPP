package interfaces;

import excepciones.DAOExcepcion;
import logica.dto.CoordinadorDTO;

import java.util.List;

public interface CoordinadorDAOInterfaz {
    public boolean agregarCoordinador (CoordinadorDTO coordinador) throws DAOExcepcion;
    public boolean actualizarCoordinador (CoordinadorDTO coordinador) throws DAOExcepcion;
    public List<CoordinadorDTO> listarCoordinador() throws DAOExcepcion;
}
