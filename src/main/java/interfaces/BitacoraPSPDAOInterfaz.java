package interfaces;
import excepciones.DAOExcepcion;
import logica.dto.BitacoraPSPDTO;

import java.util.List;

public interface BitacoraPSPDAOInterfaz {
    public void agregarBitacoraPSP(BitacoraPSPDTO bitacora) throws DAOExcepcion;
    public BitacoraPSPDTO buscarBitacoraPSPPorId(int id) throws DAOExcepcion;
    public void actualizarBitacoraPSP(BitacoraPSPDTO bitacora) throws DAOExcepcion;
    public List<BitacoraPSPDTO> listarBitacorasPSP() throws DAOExcepcion;
}
