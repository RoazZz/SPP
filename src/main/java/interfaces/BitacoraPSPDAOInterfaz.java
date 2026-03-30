package interfaces;
import logica.dto.BitacoraPSPDTO;

public interface BitacoraPSPDAOInterfaz {
    public void agregar(BitacoraPSPDTO bitacora) throws Exception;
    public BitacoraPSPDTO buscarPorId(int id) throws Exception;
    public void actualizar(BitacoraPSPDTO bitacora) throws Exception;
}
