package interfaces;

import logica.dto.CoordinadorDTO;
import java.util.List;

public interface CoordinadorDAOInterfaz {
    public void agregarCoordinador (CoordinadorDTO coordinador) throws Exception;
    public List<CoordinadorDTO> listarCoordinador() throws Exception;
}
