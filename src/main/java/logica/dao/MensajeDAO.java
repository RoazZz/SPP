package logica.dao;

import accesodatos.ConexionBD;
import interfaces.MensajeDAOInterfaz;
import logica.dto.MensajeDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MensajeDAO extends ConexionBD implements MensajeDAOInterfaz {
    public static final String SQL_INSERT = "INSERT INTO mensaje(idMensaje, Remitente, Destinatario, Asunto, Contenido, Fecha) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String SQL_SELECT_ALL_BY_DESTINATARIO = "SELECT * FROM mensaje WHERE Destinatario = ?";
    public static final String SQL_UPDATE = "UPDATE mensaje SET Contenido = ? WHERE idMensaje = ?";
    public static final String SQL_SELECT_BY_ID = "SELECT Contenido FROM mensaje WHERE idMensaje = ?";

    public MensajeDAO() {
        super();
    }

    @Override
    public void insertarMensaje(MensajeDTO mensaje) throws Exception{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)) {
            preparedStatement.setInt(1, mensaje.getIdMensaje());
            preparedStatement.setString(2, mensaje.getRemitente());
            preparedStatement.setString(3, mensaje.getDestinatario());
            preparedStatement.setString(4, mensaje.getAsunto());
            preparedStatement.setString(5, mensaje.getContenido());
            preparedStatement.setTimestamp(6, java.sql.Timestamp.valueOf(mensaje.getFecha()));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al insertar el mensaje: " + e.getMessage());
        }
    }

    @Override
    public String obtenerMensaje(String idMensaje) throws Exception{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_ID)) {
            preparedStatement.setString(1, idMensaje);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("Contenido");
                } else {
                    throw new Exception("Mensaje no encontrado con ID: " + idMensaje);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error al obtener el mensaje: " + e.getMessage());
        }
    }

    @Override
    public void actualizarMensaje(MensajeDTO mensaje) throws Exception{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, mensaje.getContenido());
            preparedStatement.setInt(2, mensaje.getIdMensaje());
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new Exception("No se encontró el mensaje con ID: " + mensaje.getIdMensaje());
            }
        } catch (SQLException e) {
            throw new Exception("Error al actualizar el mensaje: " + e.getMessage());
        }
    }

    @Override
    public List<MensajeDTO> obtenerMensajesPorDestinatario(String destinatario) throws Exception{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL_BY_DESTINATARIO)) {
            preparedStatement.setString(1, destinatario);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<MensajeDTO> mensajes = new java.util.ArrayList<>();
                while (resultSet.next()) {
                    MensajeDTO mensaje = new MensajeDTO(
                            resultSet.getInt("idMensaje"),
                            resultSet.getString("Remitente"),
                            resultSet.getString("Destinatario"),
                            resultSet.getString("Asunto"),
                            resultSet.getString("Contenido"),
                            resultSet.getTimestamp("Fecha").toLocalDateTime()
                    );
                    mensajes.add(mensaje);
                }
                return mensajes;
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener los mensajes por usuario: " + e.getMessage());

        }
    }

}
