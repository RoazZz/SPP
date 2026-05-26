package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.ProfesorDTO;
import java.util.List;

public interface ProfesorDAOInterfaz {
    ProfesorDTO agregarProfesor(ProfesorDTO profesor) throws DAOExcepcion;
    boolean actualizarProfesor(ProfesorDTO profesor) throws DAOExcepcion;
    ProfesorDTO buscarProfesorPorNumPersonal(String numPersonal) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    List<ProfesorDTO> listarProfesores() throws DAOExcepcion;
    boolean existeProfesorConNumeroPersonal(String numeroPersonal, int idExcluir) throws DAOExcepcion;
}
