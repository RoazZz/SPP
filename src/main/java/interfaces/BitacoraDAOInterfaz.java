package interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.BitacoraDTO;
import java.util.List;

public interface BitacoraDAOInterfaz {
    public void agregarBitacora (BitacoraDTO bitacora) throws DAOExcepcion;
    public BitacoraDTO buscarBitacoraPorMatricula(String Matricula) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<BitacoraDTO> listarBitacoras() throws DAOExcepcion;
}
