package interfaces;

import excepciones.DAOExcepcion;
import logica.dto.MensajeDTO;

import java.time.LocalDate;
import java.util.List;

public interface MensajeDAOInterfaz {
    public void insertarMensaje(MensajeDTO mensaje) throws DAOExcepcion;
    public String obtenerMensaje(String idMensaje) throws DAOExcepcion;
    public void actualizarMensaje(MensajeDTO mensaje) throws DAOExcepcion;
    public List<MensajeDTO> obtenerMensajesPorDestinatario(String idUsuario) throws DAOExcepcion;
}
