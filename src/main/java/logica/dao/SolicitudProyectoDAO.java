package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import interfaces.SolicitudProyectoDAOInterfaz;
import logica.dto.SolicitaProyectoDTO;
import logica.enums.TipoEstadoSolicitud;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SolicitudProyectoDAO implements SolicitudProyectoDAOInterfaz {
    public static final String SQL_INSERT = "INSERT INTO solicita(Matricula, idProyecto, EstadoProyecto, Periodo) VALUES (?, ?, 'Pendiente', ?)";
    public static final String SQL_UPDATE = "UPDATE solicita SET EstadoProyecto = ? WHERE idProyecto = ?";
    public static final String SQL_SELECT_BY_MATRICULA = "SELECT * FROM solicita WHERE Matricula = ?";
    public static final String SQL_SELECT_BY_ID_PROYECTO = "SELECT * FROM solicita WHERE idProyecto = ?";
    public static final String SQL_SELECT_BY_PERIODO = "SELECT * FROM solicita WHERE Periodo = ?";
    public static final String SQL_SELECT_ALL = "SELECT * FROM solicita";

    private Connection conexion;
    private static final Logger logger = Logger.getLogger(SolicitudProyectoDAO.class.getName());

    public SolicitudProyectoDAO() throws DAOExcepcion {
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
    public void insertarSolicitudProyecto(SolicitaProyectoDTO solicitaProyectoDTO) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)) {
            preparedStatement.setString(1, solicitaProyectoDTO.getMatricula());
            preparedStatement.setInt(2, solicitaProyectoDTO.getIdProyecto());
            preparedStatement.setString(3, solicitaProyectoDTO.getPeriodo());
            preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Solicitud de proyecto insertada. Proyecto ID: " + solicitaProyectoDTO.getIdProyecto());
        }catch (Exception e){
            logger.log(Level.SEVERE, "Error SQL al insertar solicitud de proyecto", e);
            throw new DAOExcepcion("Error al insertar la solicitud de proyecto", e);
        }
    }

    @Override
    public void actualizarSolicitudProyecto(SolicitaProyectoDTO solicitaProyectoDTO) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, solicitaProyectoDTO.getEstadoProyecto().name());
            preparedStatement.setInt(2, solicitaProyectoDTO.getIdProyecto());
            preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Solicitud de proyecto actualizada. Proyecto ID: " + solicitaProyectoDTO.getIdProyecto());
        }catch (Exception e){
            logger.log(Level.SEVERE, "Error SQL al actualizar solicitud de proyecto", e);
            throw new DAOExcepcion("Error al actualizar la solicitud de proyecto", e);
        }
    }

    @Override
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorMatricula(String matricula) throws DAOExcepcion {
        List<SolicitaProyectoDTO> listaSolicitudesProyecto = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_MATRICULA)) {
            preparedStatement.setString(1, matricula);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    listaSolicitudesProyecto.add(new SolicitaProyectoDTO(
                            resultSet.getString("Matricula"),
                            resultSet.getInt("idProyecto"),
                            TipoEstadoSolicitud.valueOf(resultSet.getString("EstadoProyecto")),
                            resultSet.getString("Periodo")
                    ));
                }
            }
            return listaSolicitudesProyecto;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al obtener solicitudes por matricula: " + matricula, e);
            throw new DAOExcepcion("Error al obtener las solicitudes de proyecto por matrícula", e);
        }
    }

    @Override
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorIdProyecto(int idProyecto) throws DAOExcepcion {
        List<SolicitaProyectoDTO> listaSolicitudesProyecto = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_ID_PROYECTO)) {
            preparedStatement.setInt(1, idProyecto);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    listaSolicitudesProyecto.add(new SolicitaProyectoDTO(
                            resultSet.getString("Matricula"),
                            resultSet.getInt("idProyecto"),
                            TipoEstadoSolicitud.valueOf(resultSet.getString("EstadoProyecto")),
                            resultSet.getString("Periodo")
                    ));
                }
            }
            return listaSolicitudesProyecto;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al obtener solicitudes por ID proyecto: " + idProyecto, e);
            throw new DAOExcepcion("Error al obtener las solicitudes de proyecto por ID de proyecto", e);
        }
    }

    @Override
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorPeriodo(String periodo) throws DAOExcepcion {
        List<SolicitaProyectoDTO> listaSolicitudesProyecto = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_PERIODO)) {
            preparedStatement.setString(1, periodo);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    listaSolicitudesProyecto.add(new SolicitaProyectoDTO(
                            resultSet.getString("Matricula"),
                            resultSet.getInt("idProyecto"),
                            TipoEstadoSolicitud.valueOf(resultSet.getString("EstadoProyecto")),
                            resultSet.getString("Periodo")
                    ));
                }
            }
            return listaSolicitudesProyecto;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al obtener solicitudes por periodo: " + periodo, e);
            throw new DAOExcepcion("Error al obtener las solicitudes de proyecto por período", e);
        }
    }

    @Override
    public List<SolicitaProyectoDTO> obtenerTodasLasSolicitudesProyecto() throws DAOExcepcion {
        List<SolicitaProyectoDTO> listaSolicitudesProyecto = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                listaSolicitudesProyecto.add(new SolicitaProyectoDTO(
                        resultSet.getString("Matricula"),
                        resultSet.getInt("idProyecto"),
                        TipoEstadoSolicitud.valueOf(resultSet.getString("EstadoProyecto")),
                        resultSet.getString("Periodo")
                ));
            }
            return listaSolicitudesProyecto;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al obtener todas las solicitudes", e);
            throw new DAOExcepcion("Error al obtener todas las solicitudes de proyecto", e);
        }
    }
}
