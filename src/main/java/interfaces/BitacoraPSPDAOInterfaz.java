package interfaces;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.BitacoraPSPDTO;

import java.util.List;

public interface BitacoraPSPDAOInterfaz {
    public void agregarBitacoraPSP(BitacoraPSPDTO bitacora) throws DAOExcepcion;
    public BitacoraPSPDTO buscarBitacoraPSPPorId(int id) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public void actualizarBitacoraPSP(BitacoraPSPDTO bitacora) throws DAOExcepcion;
    public List<BitacoraPSPDTO> listarBitacorasPSP() throws DAOExcepcion;
}
