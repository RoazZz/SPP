package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.ReglaDeNegocioExcepcion;
import logica.dao.ProfesorDAO;
import logica.dao.UsuarioDAO;
import logica.dto.ProfesorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import logica.enums.TipoTurno;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfesorControlador {

    private static final int LONGITUD_MINIMA_CONTRASENIA = 8;
    private final ProfesorDAO profesorDAO;
    private final UsuarioDAO usuarioDAO;

    private static final Logger LOGGER = Logger.getLogger(ProfesorControlador.class.getName());

    public ProfesorControlador() throws DAOExcepcion {
        this.profesorDAO = new ProfesorDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    public ProfesorDTO construirProfesorDTO(int id, String nombre, String apellidoP, String apellidoM, String contrasenia, String numeroPersonal, TipoTurno turno) {
        return new ProfesorDTO(
                id, nombre, apellidoP, apellidoM, contrasenia,
                TipoEstado.ACTIVO, TipoDeUsuario.PROFESOR,
                numeroPersonal, turno
        );
    }

    public void procesarGuardadoProfesor(ProfesorDTO profesorDTO, boolean modoEdicion)
            throws DAOExcepcion, ReglaDeNegocioExcepcion {
        validarCamposProfesor(profesorDTO.getNumeroDePersonal(), profesorDTO.getTurno());

        int idExcluir = modoEdicion ? profesorDTO.getIdUsuario() : 0;
        if (profesorDAO.existeProfesorConNumeroPersonal(profesorDTO.getNumeroDePersonal(), idExcluir)) {
            throw new ReglaDeNegocioExcepcion("Ya existe un profesor con el número de personal: " + profesorDTO.getNumeroDePersonal()
            );
        }

        if (modoEdicion) {
            LOGGER.log(Level.INFO, "Actualizando profesor con id: {0}", profesorDTO.getIdUsuario());
            usuarioDAO.actualizarUsuario(profesorDTO);
            profesorDAO.actualizarProfesor(profesorDTO);
        } else {
            LOGGER.log(Level.INFO, "Guardando profesor con numero de personal: {0}", profesorDTO.getNumeroDePersonal());
            profesorDAO.agregarProfesor(profesorDTO);
        }
    }

    private void validarCamposProfesor(String numeroPersonal, TipoTurno turno) throws ReglaDeNegocioExcepcion {
        if (numeroPersonal == null || numeroPersonal.trim().isEmpty()) {
            throw new ReglaDeNegocioExcepcion("El número de personal no puede estar vacío.");
        }
        if (turno == null) {
            throw new ReglaDeNegocioExcepcion("Debe seleccionar un turno.");
        }
    }
}