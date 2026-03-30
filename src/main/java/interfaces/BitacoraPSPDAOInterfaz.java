package interfaces;
import logica.dto.BitacoraPSPDTO;

import java.util.List;

public interface BitacoraPSPDAOInterfaz {
    public void agregarBitacoraPSP(BitacoraPSPDTO bitacora) throws Exception;
    public BitacoraPSPDTO buscarBitacoraPSPPorId(int id) throws Exception;
    public void actualizarBitacoraPSP(BitacoraPSPDTO bitacora) throws Exception;
    public List<BitacoraPSPDTO> listarBitacorasPSP() throws Exception;
}
