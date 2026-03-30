package logica.dao;

import accesodatos.ConexionBD;
import interfaces.SolicitaProyectoDAOInterfaz;
import logica.dto.SolicitaProyectoDTO;
import logica.enums.TipoEstadoSolicitud;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SolicitudProyectoDAO extends ConexionBD implements SolicitaProyectoDAOInterfaz {
    public static final String SQL_INSERT = "INSERT INTO solicita(Matricula, idProyecto, EstadoProyecto, Periodo) VALUES (?, ?, 'Pendiente', ?)";
    public static final String SQL_UPDATE = "UPDATE solicita SET EstadoProyecto = ? WHERE idProyecto = ?";
    public static final String SQL_SELECT_BY_MATRICULA = "SELECT * FROM solicita WHERE Matricula = ?";
    public static final String SQL_SELECT_BY_ID_PROYECTO = "SELECT * FROM solicita WHERE idProyecto = ?";
    public static final String SQL_SELECT_BY_PERIODO = "SELECT * FROM solicita WHERE Periodo = ?";
    public static final String SQL_SELECT_ALL = "SELECT * FROM solicita";

    public SolicitudProyectoDAO() throws Exception {
        super();
     }

    // CHCAR Q NO TENEMOS idSolicita EN LA BD !
    @Override
    public void insertarSolicitudProyecto(SolicitaProyectoDTO solicitaProyectoDTO) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)) {
            preparedStatement.setString(1, solicitaProyectoDTO.getMatricula());
            preparedStatement.setInt(2, solicitaProyectoDTO.getIdProyecto());
            preparedStatement.setString(3, solicitaProyectoDTO.getPeriodo());
            preparedStatement.executeUpdate();
        }catch (Exception e){
            throw new Exception("Error al insertar la solicitud de proyecto: " + e.getMessage());
        }
    }

    @Override
    public void actualizarSolicitudProyecto(SolicitaProyectoDTO solicitaProyectoDTO) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, solicitaProyectoDTO.getEstadoProyecto().name());
            preparedStatement.setInt(2, solicitaProyectoDTO.getIdProyecto());
            preparedStatement.executeUpdate();
        }catch (Exception e){
            throw new Exception("Error al actualizar la solicitud de proyecto: " + e.getMessage());
        }
    }

    @Override
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorMatricula(String matricula) throws Exception {
        List<SolicitaProyectoDTO> listaSolicitudesProyecto = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_MATRICULA); ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                SolicitaProyectoDTO solicitudProyecto = new SolicitaProyectoDTO(
                        resultSet.getString("Matricula"),
                        resultSet.getInt("idProyecto"),
                        TipoEstadoSolicitud.valueOf(resultSet.getString("EstadoProyecto")),
                        resultSet.getString("Periodo")
                );
                listaSolicitudesProyecto.add(solicitudProyecto);
            }
            return listaSolicitudesProyecto;
        }catch (Exception e){
            throw new Exception("Error al obtener las solicitudes de proyecto por matrícula: " + e.getMessage());
        }
    }

    @Override
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorIdProyecto(int idProyecto) throws Exception {
        List<SolicitaProyectoDTO> listaSolicitudesProyecto = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_ID_PROYECTO); ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                SolicitaProyectoDTO solicitudProyecto = new SolicitaProyectoDTO(
                        resultSet.getString("Matricula"),
                        resultSet.getInt("idProyecto"),
                        TipoEstadoSolicitud.valueOf(resultSet.getString("EstadoProyecto")),
                        resultSet.getString("Periodo")
                );
                listaSolicitudesProyecto.add(solicitudProyecto);
            }
            return listaSolicitudesProyecto;
        }catch (Exception e){
            throw new Exception("Error al obtener las solicitudes de proyecto por ID de proyecto: " + e.getMessage());
        }
    }

    @Override
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorPeriodo(String periodo) throws Exception {
        List<SolicitaProyectoDTO> listaSolicitudesProyecto = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_PERIODO); ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                SolicitaProyectoDTO solicitudProyecto = new SolicitaProyectoDTO(
                        resultSet.getString("Matricula"),
                        resultSet.getInt("idProyecto"),
                        TipoEstadoSolicitud.valueOf(resultSet.getString("EstadoProyecto")),
                        resultSet.getString("Periodo")
                );
                listaSolicitudesProyecto.add(solicitudProyecto);
            }
            return listaSolicitudesProyecto;
        }catch (Exception e){
            throw new Exception("Error al obtener las solicitudes de proyecto por período: " + e.getMessage());
        }
    }

    @Override
    public List<SolicitaProyectoDTO> obtenerTodasLasSolicitudesProyecto() throws Exception {
        List<SolicitaProyectoDTO> listaSolicitudesProyecto = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL); ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                SolicitaProyectoDTO solicitudProyecto = new SolicitaProyectoDTO(
                        resultSet.getString("Matricula"),
                        resultSet.getInt("idProyecto"),
                        TipoEstadoSolicitud.valueOf(resultSet.getString("EstadoProyecto")),
                        resultSet.getString("Periodo")
                );
                listaSolicitudesProyecto.add(solicitudProyecto);
            }
            return listaSolicitudesProyecto;
        }catch (Exception e){
            throw new Exception("Error al obtener todas las solicitudes de proyecto: " + e.getMessage());
        }
    }
}
