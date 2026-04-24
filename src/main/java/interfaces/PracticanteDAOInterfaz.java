package interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.PracticanteDTO;
import java.util.List;

public interface PracticanteDAOInterfaz {
    public boolean agregarPracticante (PracticanteDTO practicante) throws DAOExcepcion;
    public boolean actualizarPracticante (PracticanteDTO practicante) throws DAOExcepcion;
    public PracticanteDTO buscarPracticantePorMatricula (String matricula) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<PracticanteDTO> listarPracticantes() throws DAOExcepcion;
}

