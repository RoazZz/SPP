package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.ReglaDeNegocioExcepcion;
import logica.dao.CoordinadorDAO;
import logica.dao.UsuarioDAO;
import logica.dto.CoordinadorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CoordinadorControlador {

    private static final int LONGITUD_MINIMA_CONTRASENIA = 8;
    private final CoordinadorDAO coordinadorDAO;
    private final UsuarioDAO usuarioDAO;

    private static final Logger LOGGER = Logger.getLogger(CoordinadorControlador.class.getName());

    public CoordinadorControlador() throws DAOExcepcion {
        this.coordinadorDAO = new CoordinadorDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    public CoordinadorDTO construirCoordinadorDTO(int id, String nombre, String apellidoP, String apellidoM, String contrasenia, String numeroPersonal) {
        return new CoordinadorDTO(
                id, nombre, apellidoP, apellidoM, contrasenia,
                TipoEstado.ACTIVO, TipoDeUsuario.COORDINADOR,
                numeroPersonal
        );
    }

    public void procesarGuardadoCoordinador(CoordinadorDTO coordinadorDTO, boolean modoEdicion)
            throws DAOExcepcion, ReglaDeNegocioExcepcion {
        validarCamposCoordinador(coordinadorDTO.getNombre(), coordinadorDTO.getContrasenia(), coordinadorDTO.getNumeroPersonal());

        int idExcluir = modoEdicion ? coordinadorDTO.getIdUsuario() : 0;
        if (coordinadorDAO.existeCoordinadorConNumeroPersonal(coordinadorDTO.getNumeroPersonal(), idExcluir)) {
            throw new ReglaDeNegocioExcepcion("Ya existe un coordinador con el número de personal: " + coordinadorDTO.getNumeroPersonal());
        }

        if (modoEdicion) {
            LOGGER.log(Level.INFO, "Actualizando coordinador con id: {0}", coordinadorDTO.getIdUsuario());
            usuarioDAO.actualizarUsuario(coordinadorDTO);
            coordinadorDAO.actualizarCoordinador(coordinadorDTO);
        } else {
            LOGGER.log(Level.INFO, "Guardando coordinador con numero de personal: {0}", coordinadorDTO.getNumeroPersonal());
            coordinadorDAO.agregarCoordinador(coordinadorDTO);
        }
    }

    private void validarCamposCoordinador(String nombre, String contrasenia, String numeroPersonal) throws ReglaDeNegocioExcepcion {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ReglaDeNegocioExcepcion("El nombre no puede estar vacío.");
        }
        if (contrasenia == null || contrasenia.length() < LONGITUD_MINIMA_CONTRASENIA) {
            throw new ReglaDeNegocioExcepcion("La contraseña debe tener al menos " + LONGITUD_MINIMA_CONTRASENIA + " caracteres.");
        }
        if (numeroPersonal == null || numeroPersonal.trim().isEmpty()) {
            throw new ReglaDeNegocioExcepcion("El número de personal no puede estar vacío.");
        }
    }
}