package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.BitacoraDTO;
import java.util.List;

public interface BitacoraDAOInterfaz {
    boolean agregarBitacora (BitacoraDTO bitacora) throws DAOExcepcion;
    BitacoraDTO buscarBitacoraPorMatricula(String matricula) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    List<BitacoraDTO> listarBitacoras() throws DAOExcepcion;
}
