package interfaces;
import logica.dto.BitacoraPSPDTO;

public interface InterBitacoraPSPDAO {
    void agregar(BitacoraPSPDTO bitacora) throws Exception;
    BitacoraPSPDTO buscarPorId(int id) throws Exception;
    void actualizar(BitacoraPSPDTO bitacora) throws Exception;
}
