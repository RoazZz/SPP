package interfaces;

import logica.dto.MensajeDTO;

import java.time.LocalDate;
import java.util.List;

public interface MensajeDAOInterfaz {
    public void insertarMensaje(MensajeDTO mensaje) throws Exception;
    public String obtenerMensaje(String idMensaje) throws Exception;
    public void actualizarMensaje(MensajeDTO mensaje) throws Exception;
    public List<MensajeDTO> obtenerMensajesPorDestinatario(String idUsuario) throws Exception;
}
