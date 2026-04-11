package interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.UsuarioDTO;
import java.util.List;

public interface UsuarioDAOInterfaz {
    public void agregarUsuario (UsuarioDTO usuario) throws DAOExcepcion;
    public void actualizarUsuario (UsuarioDTO usuario) throws DAOExcepcion;
    public UsuarioDTO buscarUsuarioPorIdUsuario(int idUsuario) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<UsuarioDTO> listarUsuarios() throws DAOExcepcion;
}
