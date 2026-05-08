package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.ReglaDeNegocioExcepcion;
import logica.dao.CoordinadorAsignaProyectoDAO;
import logica.dao.SolicitaProyectoDAO;
import logica.dto.CoordinadorAsignaProyectoDTO;
import logica.dto.SolicitaProyectoDTO;
import logica.enums.EstadoAsignacionProyecto;
import logica.enums.TipoEstadoSolicitud;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsignacionProyectoControlador {

    private final CoordinadorAsignaProyectoDAO coordinadorAsignaProyectoDAO;
    private final SolicitaProyectoDAO solicitaProyectoDAO;

    private static final Logger LOGGER = Logger.getLogger(AsignacionProyectoControlador.class.getName());

    public AsignacionProyectoControlador() throws DAOExcepcion {
        this.coordinadorAsignaProyectoDAO = new CoordinadorAsignaProyectoDAO();
        this.solicitaProyectoDAO = new SolicitaProyectoDAO();
    }

    public List<SolicitaProyectoDTO> obtenerSolicitudesPendientes(String periodo) throws DAOExcepcion {
        List<SolicitaProyectoDTO> todasLasSolicitudes = solicitaProyectoDAO.obtenerSolicitudesProyectoPorPeriodo(periodo);
        return todasLasSolicitudes.stream()
                .filter(s -> s.getEstadoProyecto() == TipoEstadoSolicitud.PENDIENTE)
                .toList();
    }

    public void procesarAsignacionProyecto(SolicitaProyectoDTO solicitudDTO, String numeroDePersonalCoordinador)
            throws DAOExcepcion, ReglaDeNegocioExcepcion {
        validarAsignacion(solicitudDTO, numeroDePersonalCoordinador);

        solicitudDTO.setEstadoProyecto(TipoEstadoSolicitud.ACEPTADO);
        solicitaProyectoDAO.actualizarSolicitudProyecto(solicitudDTO);
        LOGGER.log(Level.INFO, "Solicitud aprobada para matricula:", solicitudDTO.getMatricula());

        CoordinadorAsignaProyectoDTO asignacionDTO = new CoordinadorAsignaProyectoDTO(
                numeroDePersonalCoordinador,
                solicitudDTO.getIdProyecto(),
                EstadoAsignacionProyecto.EN_REVISION
        );
        coordinadorAsignaProyectoDAO.insertarAsignacionDeProyecto(asignacionDTO);
        LOGGER.log(Level.INFO, "Proyecto asignado por coordinador",
                new Object[]{solicitudDTO.getIdProyecto(), numeroDePersonalCoordinador});
    }

    private void validarAsignacion(SolicitaProyectoDTO solicitudDTO, String numeroDePersonalCoordinador)
            throws ReglaDeNegocioExcepcion {
        if (solicitudDTO == null) {
            throw new ReglaDeNegocioExcepcion("Debe seleccionar una solicitud.");
        }
        if (solicitudDTO.getEstadoProyecto() != TipoEstadoSolicitud.PENDIENTE) {
            throw new ReglaDeNegocioExcepcion("Solo se pueden asignar solicitudes en estado Pendiente.");
        }
        if (numeroDePersonalCoordinador == null || numeroDePersonalCoordinador.trim().isEmpty()) {
            throw new ReglaDeNegocioExcepcion("El número de personal del coordinador no puede estar vacío.");
        }
    }
}