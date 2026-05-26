package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.BitacoraPSPDTO;

import java.util.List;

public interface BitacoraPSPDAOInterfaz {
    BitacoraPSPDTO agregarBitacoraPSP(BitacoraPSPDTO bitacora) throws DAOExcepcion;
    BitacoraPSPDTO buscarBitacoraPSPPorId(int id) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    boolean actualizarBitacoraPSP(BitacoraPSPDTO bitacora) throws DAOExcepcion;
    List<BitacoraPSPDTO> listarBitacorasPSP() throws DAOExcepcion;
}
