package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.MensajeDTO;
import java.util.List;

public interface MensajeDAOInterfaz {
    boolean insertarMensaje(MensajeDTO mensajeDTO) throws DAOExcepcion;
    boolean marcarComoLeido(int idMensaje) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    List<MensajeDTO> obtenerMensajesPorDestinatario(int idBuzonDestino) throws DAOExcepcion;
    List<MensajeDTO> obtenerMensajesConRemitente(int idBuzonDestino) throws DAOExcepcion;

}
