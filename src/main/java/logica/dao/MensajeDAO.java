package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.MensajeDAOInterfaz;
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
    private static final String SQL_MARCAR_LEIDO = "UPDATE Mensaje SET leido = 1, fechaLectura = NOW()" +
            " WHERE idMensaje = ?";
    private static final String SQL_SELECT_BY_BUZON_DESTINO =
            "SELECT idMensaje, idBuzonOrigen, idBuzonDestino, asunto, contenido, fecha, leido, fechaLectura " +
                    "FROM Mensaje WHERE idBuzonDestino = ?";
    private static final String SQL_SELECT_BY_BUZON_DESTINO_CON_REMITENTE =
            "SELECT m.idMensaje, m.idBuzonOrigen, m.idBuzonDestino, m.asunto, m.contenido, " +
                    "m.fecha, m.leido, m.fechaLectura, " +
                    "u.Nombre, u.ApellidoP, u.ApellidoM, u.TipoUsuario, " +
                    "COALESCE(p.Matricula, c.NumeroDePersonal, pr.NumeroDePersonal, CAST(a.idAdministrador AS CHAR))" +
                    " AS identificador " +
                    "FROM Mensaje m " +
                    "JOIN Buzon b ON m.idBuzonOrigen = b.idBuzon " +
                    "JOIN Usuario u ON b.idUsuario = u.idUsuario " +
                    "LEFT JOIN Practicante p ON u.idUsuario = p.idUsuario " +
                    "LEFT JOIN Coordinador c ON u.idUsuario = c.idUsuario " +
                    "LEFT JOIN Profesor pr ON u.idUsuario = pr.idUsuario " +
                    "LEFT JOIN Administrador a ON u.idUsuario = a.idUsuario " +
                    "WHERE m.idBuzonDestino = ?";

    private Connection conexion;
    private static final Logger REGISTRADOR = Logger.getLogger(MensajeDAO.class.getName());

    public MensajeDAO() throws DAOExcepcion {
        try {
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error de entrada/salida al configurar la conexión", ioExcepcion);
            throw new DAOExcepcion("Error al leer la configuración de la base de datos", ioExcepcion);
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error de SQL al intentar conectar", sqlExcepcion);
            throw new DAOExcepcion("Error de acceso a la base de datos", sqlExcepcion);
        }
    }

    @Override
    public boolean insertarMensaje(MensajeDTO mensajeDTO) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            sentenciaPreparada.setInt(1, mensajeDTO.getIdBuzonOrigen());
            sentenciaPreparada.setInt(2, mensajeDTO.getIdBuzonDestino());
            sentenciaPreparada.setString(3, mensajeDTO.getAsunto());
            sentenciaPreparada.setString(4, mensajeDTO.getContenido());
            sentenciaPreparada.executeUpdate();
            ResultSet llavesGeneradas = sentenciaPreparada.getGeneratedKeys();
            if (llavesGeneradas.next()) {
                mensajeDTO.setIdMensaje(llavesGeneradas.getInt(1));
            }
            REGISTRADOR.log(Level.INFO, "Mensaje insertado correctamente. ID: " + mensajeDTO.getIdMensaje());
            return true;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al insertar el mensaje", sqlExcepcion);
            throw new DAOExcepcion("Error al guardar el mensaje", sqlExcepcion);
        }
    }

    @Override
    public boolean marcarComoLeido(int idMensaje) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_MARCAR_LEIDO)) {
            sentenciaPreparada.setInt(1, idMensaje);
            int filasAfectadas = sentenciaPreparada.executeUpdate();
            if (filasAfectadas > 0) {
                REGISTRADOR.log(Level.INFO, "Mensaje marcado como leído. ID " + idMensaje);
                return true;
            } else {
                REGISTRADOR.log(Level.WARNING, "No se encontró mensaje para marcar como leído. ID " + idMensaje);
                throw new EntidadNoEncontradaExcepcion("No se encontró el mensaje con ID " + idMensaje);
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al marcar mensaje como leído. ID " + idMensaje, sqlExcepcion);
            throw new DAOExcepcion("Error al marcar el mensaje como leído", sqlExcepcion);
        }
    }

    @Override
    public List<MensajeDTO> obtenerMensajesPorDestinatario(int idBuzonDestino) throws DAOExcepcion {
        List<MensajeDTO> mensajes = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_BUZON_DESTINO)) {
            sentenciaPreparada.setInt(1, idBuzonDestino);
            ResultSet conjuntoResultado = sentenciaPreparada.executeQuery();
            while (conjuntoResultado.next()) {
                MensajeDTO mensajeDTO = new MensajeDTO(
                        conjuntoResultado.getInt("idMensaje"),
                        conjuntoResultado.getInt("idBuzonOrigen"),
                        conjuntoResultado.getInt("idBuzonDestino"),
                        conjuntoResultado.getString("asunto"),
                        conjuntoResultado.getString("contenido"),
                        conjuntoResultado.getTimestamp("fecha").toLocalDateTime(),
                        conjuntoResultado.getBoolean("leido"),
                        conjuntoResultado.getTimestamp("fechaLectura") != null ?
                                conjuntoResultado.getTimestamp("fechaLectura").toLocalDateTime() : null
                );
                mensajes.add(mensajeDTO);
            }
            return mensajes;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al obtener mensajes del buzon: " + idBuzonDestino, sqlExcepcion);
            throw new DAOExcepcion("Error al obtener la lista de mensajes", sqlExcepcion);
        }
    }

    @Override
    public List<MensajeDTO> obtenerMensajesConRemitente(int idBuzonDestino) throws DAOExcepcion {
        List<MensajeDTO> mensajes = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_BUZON_DESTINO_CON_REMITENTE)) {
            sentenciaPreparada.setInt(1, idBuzonDestino);
            ResultSet conjuntoResultado = sentenciaPreparada.executeQuery();
            while (conjuntoResultado.next()) {
                MensajeDTO mensajeDTO = new MensajeDTO(
                        conjuntoResultado.getInt("idMensaje"),
                        conjuntoResultado.getInt("idBuzonOrigen"),
                        conjuntoResultado.getInt("idBuzonDestino"),
                        conjuntoResultado.getString("asunto"),
                        conjuntoResultado.getString("contenido"),
                        conjuntoResultado.getTimestamp("fecha").toLocalDateTime(),
                        conjuntoResultado.getBoolean("leido"),
                        conjuntoResultado.getTimestamp("fechaLectura") != null ?
                                conjuntoResultado.getTimestamp("fechaLectura").toLocalDateTime() : null
                );
                String nombreCompleto = conjuntoResultado.getString("Nombre") + " " +
                        conjuntoResultado.getString("ApellidoP") + " " +
                        conjuntoResultado.getString("ApellidoM");
                String identificador = conjuntoResultado.getString("identificador");
                String tipoUsuario = conjuntoResultado.getString("TipoUsuario");
                mensajeDTO.setNombreRemitente(nombreCompleto + " - " + identificador + " (" + tipoUsuario + ")");
                REGISTRADOR.log(Level.INFO, "Remitente seteado: " + mensajeDTO.getNombreRemitente());
                mensajes.add(mensajeDTO);
            }
            return mensajes;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al obtener mensajes con remitente: " + idBuzonDestino, sqlExcepcion);
            throw new DAOExcepcion("Error al obtener la lista de mensajes", sqlExcepcion);
        }
    }
}
