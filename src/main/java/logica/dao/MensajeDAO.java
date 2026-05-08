package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.MensajeDAOInterfaz;
import logica.dto.MensajeDTO;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MensajeDAO implements MensajeDAOInterfaz {
    private static final String SQL_INSERT =
            "INSERT INTO Mensaje (idBuzonOrigen, idBuzonDestino, asunto, contenido) VALUES (?, ?, ?, ?)";
    private static final String SQL_SELECT_BY_ID =
            "SELECT idMensaje, idBuzonOrigen, idBuzonDestino, asunto, contenido, fecha, leido, fechaLectura " +
                    "FROM Mensaje WHERE idMensaje = ?";
    private static final String SQL_MARCAR_LEIDO = "UPDATE Mensaje SET leido = 1, fechaLectura = NOW() WHERE idMensaje = ?";
    private static final String SQL_SELECT_BY_BUZON_DESTINO =
            "SELECT idMensaje, idBuzonOrigen, idBuzonDestino, asunto, contenido, fecha, leido, fechaLectura " +
                    "FROM Mensaje WHERE idBuzonDestino = ?";

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
    public boolean insertarMensaje(MensajeDTO mensajeDTO) throws DAOExcepcion{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, mensajeDTO.getIdBuzonOrigen());
            preparedStatement.setInt(2, mensajeDTO.getIdBuzonDestino());
            preparedStatement.setString(3, mensajeDTO.getAsunto());
            preparedStatement.setString(4, mensajeDTO.getContenido());
            preparedStatement.executeUpdate();
            ResultSet llavesGeneradas = preparedStatement.getGeneratedKeys();
            if (llavesGeneradas.next()) {
                mensajeDTO.setIdMensaje(llavesGeneradas.getInt(1));
            }
            logger.log(Level.INFO, "Mensaje insertado correctamente. ID: " + mensajeDTO.getIdMensaje());
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al insertar el mensaje", e);
            throw new DAOExcepcion("Error al guardar el mensaje", e);
        }
    }

    @Override
    public boolean marcarComoLeido(int idMensaje) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_MARCAR_LEIDO)) {
            preparedStatement.setInt(1, idMensaje);
            int filasAfectadas = preparedStatement.executeUpdate();
            if (filasAfectadas > 0) {
                logger.log(Level.INFO, "Mensaje marcado como leído. ID: " + idMensaje);
                return true;
            } else {
                logger.log(Level.WARNING, "No se encontró mensaje para marcar como leído. ID: " + idMensaje);
                throw new EntidadNoEncontradaExcepcion("No se encontró el mensaje con ID: " + idMensaje);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al marcar mensaje como leído. ID: " + idMensaje, e);
            throw new DAOExcepcion("Error al marcar el mensaje como leído", e);
        }
    }

    @Override
    public List<MensajeDTO> obtenerMensajesPorDestinatario(int idBuzonDestino) throws DAOExcepcion {
        List<MensajeDTO> mensajes = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_BUZON_DESTINO)) {
            preparedStatement.setInt(1, idBuzonDestino);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                MensajeDTO mensajeDTO = new MensajeDTO(
                        resultSet.getInt("idMensaje"),
                        resultSet.getInt("idBuzonOrigen"),
                        resultSet.getInt("idBuzonDestino"),
                        resultSet.getString("asunto"),
                        resultSet.getString("contenido"),
                        resultSet.getTimestamp("fecha").toLocalDateTime(),
                        resultSet.getBoolean("leido"),
                        resultSet.getTimestamp("fechaLectura") != null ?
                                resultSet.getTimestamp("fechaLectura").toLocalDateTime() : null
                );
                mensajes.add(mensajeDTO);
            }
            return mensajes;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al obtener mensajes del buzon: " + idBuzonDestino, e);
            throw new DAOExcepcion("Error al obtener la lista de mensajes", e);
        }
    }
}
