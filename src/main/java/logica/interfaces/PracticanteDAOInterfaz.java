package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.PracticanteDTO;
import java.util.List;

public interface PracticanteDAOInterfaz {
    boolean agregarPracticante(PracticanteDTO practicante) throws DAOExcepcion;
    boolean actualizarPracticante(PracticanteDTO practicante) throws DAOExcepcion;
    PracticanteDTO buscarPracticantePorMatricula(String matricula) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    List<PracticanteDTO> listarPracticantes() throws DAOExcepcion;
    boolean existePracticanteConMatricula(String matricula) throws DAOExcepcion;
    List<PracticanteDTO> listarPracticantesPorSeccion(int idSeccion) throws DAOExcepcion;

}


