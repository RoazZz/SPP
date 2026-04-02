package interfaces;

import logica.dto.PracticanteDTO;
import java.util.List;

public interface PracticanteDAOInterfaz {
    public void agregarPracticante (PracticanteDTO practicante) throws Exception;
    public void actualizarPracticante (PracticanteDTO practicante) throws Exception;
    public PracticanteDTO buscarPracticantePorMatricula (String matricula) throws Exception;
    public List<PracticanteDTO> listarPracticantes() throws Exception;
}

