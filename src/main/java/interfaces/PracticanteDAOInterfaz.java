package interfaces;

import excepciones.DAOExcepcion;
import logica.dto.PracticanteDTO;
import java.util.List;

public interface PracticanteDAOInterfaz {
    public void agregarPracticante (PracticanteDTO practicante) throws DAOExcepcion;
    public void actualizarPracticante (PracticanteDTO practicante) throws DAOExcepcion;
    public PracticanteDTO buscarPracticantePorMatricula (String matricula) throws DAOExcepcion;
    public List<PracticanteDTO> listarPracticantes() throws DAOExcepcion;
}

