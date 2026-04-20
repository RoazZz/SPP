package interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.MensajeDTO;

import java.time.LocalDate;
import java.util.List;

public interface MensajeDAOInterfaz {
    public MensajeDTO insertarMensaje(MensajeDTO mensaje) throws DAOExcepcion;
    public String obtenerMensaje(String idMensaje) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public boolean actualizarMensaje(MensajeDTO mensaje) throws DAOExcepcion;
    public List<MensajeDTO> obtenerMensajesPorDestinatario(String idUsuario) throws DAOExcepcion;
}
