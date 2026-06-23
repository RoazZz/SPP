package logica.interfaces;
import excepciones.DAOExcepcion;
import logica.dto.BitacoraSistemaDTO;
import java.util.List;

public interface BitacoraSistemaDAOInterfaz {
    boolean agregarBitacora(BitacoraSistemaDTO bitacora) throws DAOExcepcion;
    List<BitacoraSistemaDTO> listarBitacoras() throws DAOExcepcion;
}