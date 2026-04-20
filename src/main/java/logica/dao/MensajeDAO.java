package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.MensajeDAOInterfaz;
import logica.dto.MensajeDTO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MensajeDAO implements MensajeDAOInterfaz {
    public static final String SQL_INSERT = "INSERT INTO mensaje(Remitente, Destinatario, Asunto, Contenido, Fecha) VALUES (?, ?, ?, ?, ?)";
    public static final String SQL_SELECT_ALL_BY_DESTINATARIO = "SELECT * FROM mensaje WHERE Destinatario = ?";
    public static final String SQL_UPDATE = "UPDATE mensaje SET Contenido = ? WHERE idMensaje = ?";
    public static final String SQL_SELECT_BY_ID = "SELECT Contenido FROM mensaje WHERE idMensaje = ?";

    private Connection conexion;
    private static final Logger logger = Logger.getLogger(MensajeDAO.class.getName());

    public MensajeDAO() throws DAOExcepcion {
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error de entrada/salida al configurar la conexión", e);
            throw new DAOExcepcion("Error al leer la configuración de la base de datos", e);
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error de SQL al intentar conectar", e);
            throw new DAOExcepcion("Error de acceso a la base de datos", e);
        }
    }

    @Override
    public MensajeDTO insertarMensaje(MensajeDTO mensaje) throws DAOExcepcion{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, mensaje.getRemitente());
            preparedStatement.setString(2, mensaje.getDestinatario());
            preparedStatement.setString(3, mensaje.getAsunto());
            preparedStatement.setString(4, mensaje.getContenido());
            preparedStatement.setTimestamp(5, java.sql.Timestamp.valueOf(mensaje.getFecha()));
            preparedStatement.executeUpdate();

            try (ResultSet llavesGeneradas = preparedStatement.getGeneratedKeys()) {
                if (llavesGeneradas.next()) {
                    mensaje.setIdMensaje(llavesGeneradas.getInt(1));
                }
            }

            logger.log(Level.INFO, "Mensaje insertado correctamente. ID: " + mensaje.getIdMensaje());
            return mensaje;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al insertar el mensaje", e);
            throw new DAOExcepcion("Error al guardar el mensaje", e);        }
    }

    @Override
    public String obtenerMensaje(String idMensaje) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_ID)) {
            preparedStatement.setString(1, idMensaje);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("Contenido");
                }else{
                    logger.log(Level.WARNING, "No se encontró mensaje con ID: " + idMensaje);
                    throw new EntidadNoEncontradaExcepcion("No se encontró el mensaje con ID: " + idMensaje);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error SQL al obtener mensaje por ID: " + idMensaje, e);
            throw new DAOExcepcion("Error al buscar el contenido del mensaje", e);        }
    }

    @Override
    public boolean actualizarMensaje(MensajeDTO mensaje) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, mensaje.getContenido());
            preparedStatement.setInt(2, mensaje.getIdMensaje());
            int filasAfectadas = preparedStatement.executeUpdate();
            if (filasAfectadas > 0) {
                logger.log(Level.INFO, "Mensaje actualizado correctamente. ID: " + mensaje.getIdMensaje());
                return true;
            }else{
                logger.log(Level.WARNING, "No se encontró mensaje para actualizar con ID: " + mensaje.getIdMensaje());
                throw new EntidadNoEncontradaExcepcion("No se encontró el mensaje con ID: " + mensaje.getIdMensaje());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al actualizar el mensaje", e);
            throw new DAOExcepcion("Error al modificar el mensaje", e);        }
    }

    @Override
    public List<MensajeDTO> obtenerMensajesPorDestinatario(String destinatario) throws DAOExcepcion{
        List<MensajeDTO> mensajes = new java.util.ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL_BY_DESTINATARIO)) {
            preparedStatement.setString(1, destinatario);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
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
            }
            return mensajes;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al obtener mensajes del destinatario: " + destinatario, e);
            throw new DAOExcepcion("Error al obtener la lista de mensajes", e);
        }
    }

}
