package gui.controladores;

import accesodatos.ConexionBD;
import logica.dao.ProfesorDAO;
import logica.dao.UsuarioDAO;
import logica.dto.ProfesorDTO;
import logica.dto.UsuarioDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import logica.enums.TipoTurno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class PruebaUtilDatos {

    private static final String SQL_ELIMINAR_PROFESOR_POR_ID_USUARIO =
            "DELETE FROM Profesor WHERE idUsuario = ?";
    private static final String SQL_ELIMINAR_USUARIO_POR_ID =
            "DELETE FROM Usuario WHERE idUsuario = ?";

    private final List<Integer> idsUsuariosPruebaInsertados = new ArrayList<>();
    private final List<Integer> idsProfesoresPruebaInsertados = new ArrayList<>();

    public UsuarioDTO insertarUsuarioPruebaBase(String nombre, String apellidoPaterno, String apellidoMaterno,
                                                TipoDeUsuario tipoDeUsuario, TipoEstado tipoEstado) throws Exception {
        UsuarioDTO usuarioPrueba = new UsuarioDTO(
                0, nombre, apellidoPaterno, apellidoMaterno,
                "contraseniaPrueba123", tipoEstado, tipoDeUsuario
        );
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        usuarioDAO.agregarUsuario(usuarioPrueba);
        idsUsuariosPruebaInsertados.add(usuarioPrueba.getIdUsuario());
        return usuarioPrueba;
    }

    public ProfesorDTO insertarProfesorPrueba(String nombre, String apellidoPaterno, String apellidoMaterno,
                                              String numeroDePersonal, TipoTurno turno,
                                              TipoEstado tipoEstado) throws Exception {
        ProfesorDTO profesorPrueba = new ProfesorDTO(
                0, nombre, apellidoPaterno, apellidoMaterno,
                "contraseniaPrueba123", tipoEstado, TipoDeUsuario.PROFESOR,
                numeroDePersonal, turno
        );
        ProfesorDAO profesorDAO = new ProfesorDAO();
        profesorDAO.agregarProfesor(profesorPrueba);
        idsProfesoresPruebaInsertados.add(profesorPrueba.getIdUsuario());
        idsUsuariosPruebaInsertados.add(profesorPrueba.getIdUsuario());
        return profesorPrueba;
    }

    public void limpiarTodosLosDatosPrueba() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();

        for (Integer idProfesor : idsProfesoresPruebaInsertados) {
            try (PreparedStatement sentenciaEliminar = conexion.prepareStatement(SQL_ELIMINAR_PROFESOR_POR_ID_USUARIO)) {
                sentenciaEliminar.setInt(1, idProfesor);
                sentenciaEliminar.executeUpdate();
            }
        }
        for (Integer idUsuario : idsUsuariosPruebaInsertados) {
            try (PreparedStatement sentenciaEliminar = conexion.prepareStatement(SQL_ELIMINAR_USUARIO_POR_ID)) {
                sentenciaEliminar.setInt(1, idUsuario);
                sentenciaEliminar.executeUpdate();
            }
        }

        idsProfesoresPruebaInsertados.clear();
        idsUsuariosPruebaInsertados.clear();
    }
}