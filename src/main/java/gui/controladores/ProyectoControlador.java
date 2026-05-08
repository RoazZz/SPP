package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.ReglaDeNegocioExcepcion;
import logica.dao.ProyectoDAO;
import logica.dto.ProyectoDTO;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProyectoControlador {

    private static final int LONGITUD_MINIMA_DESCRIPCION = 10;
    private final ProyectoDAO proyectoDAO;

    private static final Logger LOGGER = Logger.getLogger(ProyectoControlador.class.getName());

    public ProyectoControlador() throws DAOExcepcion {
        this.proyectoDAO = new ProyectoDAO();
    }

    public ProyectoDTO construirProyectoDTO(int id, String idOrganizacion, String numeroDePersonal,
                                            String nombre, String descripcion) {
        return new ProyectoDTO(id, idOrganizacion, numeroDePersonal, nombre, descripcion);
    }

    public void procesarGuardadoProyecto(ProyectoDTO proyectoDTO, boolean modoEdicion)
            throws DAOExcepcion, ReglaDeNegocioExcepcion {
        validarCamposProyecto(proyectoDTO.getNombre(), proyectoDTO.getDescripcion(),
                proyectoDTO.getIdOrganizacion(), proyectoDTO.getNumeroDePersonal());

        if (modoEdicion) {
            LOGGER.log(Level.INFO, "Actualizando proyecto con id:", proyectoDTO.getIdProyecto());
            proyectoDAO.actualizarProyecto(proyectoDTO);
        } else {
            LOGGER.log(Level.INFO, "Guardando proyecto:", proyectoDTO.getNombre());
            proyectoDAO.agregarProyecto(proyectoDTO);
        }
    }

    public ProyectoDTO buscarProyecto(int idProyecto) throws DAOExcepcion, excepciones.EntidadNoEncontradaExcepcion {
        return proyectoDAO.buscarProyectoPorIdProyecto(idProyecto);
    }

    public List<ProyectoDTO> listarProyectos() throws DAOExcepcion {
        return proyectoDAO.listarProyectos();
    }

    private void validarCamposProyecto(String nombre, String descripcion,
                                       String idOrganizacion, String numeroDePersonal)
            throws ReglaDeNegocioExcepcion {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ReglaDeNegocioExcepcion("El nombre del proyecto no puede estar vacío.");
        }
        if (descripcion == null || descripcion.trim().length() < LONGITUD_MINIMA_DESCRIPCION) {
            throw new ReglaDeNegocioExcepcion("La descripción debe tener al menos " + LONGITUD_MINIMA_DESCRIPCION + " caracteres.");
        }
        if (idOrganizacion == null || idOrganizacion.trim().isEmpty()) {
            throw new ReglaDeNegocioExcepcion("El id de organización no puede estar vacío.");
        }
        if (numeroDePersonal == null || numeroDePersonal.trim().isEmpty()) {
            throw new ReglaDeNegocioExcepcion("El número de personal no puede estar vacío.");
        }
    }
}