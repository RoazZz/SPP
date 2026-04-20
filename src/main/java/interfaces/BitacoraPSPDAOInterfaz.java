package interfaces;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.BitacoraPSPDTO;

import java.util.List;

public interface BitacoraPSPDAOInterfaz {
    public BitacoraPSPDTO agregarBitacoraPSP(BitacoraPSPDTO bitacora) throws DAOExcepcion;
    public BitacoraPSPDTO buscarBitacoraPSPPorId(int id) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public boolean actualizarBitacoraPSP(BitacoraPSPDTO bitacora) throws DAOExcepcion;
    public List<BitacoraPSPDTO> listarBitacorasPSP() throws DAOExcepcion;
}
