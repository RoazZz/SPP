package interfaces;

import logica.dto.UsuarioDTO;
import java.util.List;

public interface UsuarioDAOInterfaz {
    public void agregarUsuario (UsuarioDTO usuario) throws Exception;
    public void actualizarUsuario (UsuarioDTO usuario) throws Exception;
    public UsuarioDTO buscarUsuarioPorIdUsuario(int idUsuario) throws Exception;
    public List<UsuarioDTO> listarUsuarios() throws Exception;
}
