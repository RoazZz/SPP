package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.UsuarioDTO;
import logica.enums.TipoDeUsuario;

import java.util.List;

public interface UsuarioDAOInterfaz {
    UsuarioDTO agregarUsuario (UsuarioDTO usuario) throws DAOExcepcion;
    void actualizarUsuario (UsuarioDTO usuario) throws DAOExcepcion;
    UsuarioDTO buscarUsuarioPorIdUsuario(int idUsuario) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    List<UsuarioDTO> listarUsuarios() throws DAOExcepcion;
    public List<UsuarioDTO> listarUsuariosPorTipos(List<TipoDeUsuario> tiposPermitidos) throws DAOExcepcion;
}
