package interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.ProfesorDTO;

import java.util.List;

public interface ProfesorDAOInterfaz {
    public void agregarProfesor(ProfesorDTO profesor) throws DAOExcepcion;
    public void actualizarProfesor(ProfesorDTO profesor) throws DAOExcepcion;
    public ProfesorDTO buscarProfesorPorNumPersonal(String numPersonal) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<ProfesorDTO> listarProfesores() throws DAOExcepcion;
}
