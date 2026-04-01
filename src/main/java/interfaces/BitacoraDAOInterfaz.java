package interfaces;

import logica.dto.BitacoraDTO;
import java.util.List;

public interface BitacoraDAOInterfaz {
    public void agregarBitacora (BitacoraDTO bitacora) throws Exception;
    public BitacoraDTO buscarBitacoraPorMatricula(String Matricula) throws Exception;
    public List<BitacoraDTO> listarBitacoras() throws Exception;
}
