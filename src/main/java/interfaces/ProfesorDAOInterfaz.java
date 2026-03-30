package interfaces;

import logica.dto.ProfesorDTO;

import java.util.List;

public interface ProfesorDAOInterfaz {
    public void agregarProfesor(ProfesorDTO profesor) throws Exception;
    public void actualizarProfesor(ProfesorDTO profesor) throws Exception;
    public ProfesorDTO buscarProfesorPorNumPersonal(String numPersonal) throws Exception;
    public List<ProfesorDTO> listarProfesores() throws Exception;
}
