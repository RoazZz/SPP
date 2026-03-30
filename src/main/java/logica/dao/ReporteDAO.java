package logica.dao;

import accesodatos.ConexionBD;
import interfaces.ReporteDAOInterfaz;
import logica.dto.ReporteDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO extends ConexionBD implements ReporteDAOInterfaz {
    public static final String SQL_INSERT = "INSERT INTO reporte(idReporte, Matricula, idOrganizacion, TipoReporte, Observaciones, Fecha, Estado) VALUES (?, ?, ?, ?, ?, ?, ?)";
    public static final String SQL_UPDATE = "UPDATE reporte SET Matricula = ?, idOrganizacion = ?, TipoReporte = ?, Observaciones = ?, Fecha = ?, Estado = ? WHERE idReporte = ?";
    public static final String SQL_SELECT_BY_ID = "SELECT * FROM reporte WHERE idReporte = ?";
    public static final String SQL_SELECT_ALL = "SELECT * FROM reporte";

    public void agregarReporte(ReporteDTO reporte) throws Exception{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)) {
            preparedStatement.setString(1, reporte.getTipoReporte());
            preparedStatement.setString(2, reporte.getMatricula());
            preparedStatement.setString(3, reporte.getIdOrganizacion());
            preparedStatement.setString(4, reporte.getObservaciones());
            preparedStatement.setDate(5, java.sql.Date.valueOf(reporte.getFecha()));
            preparedStatement.setString(6, reporte.getEstado());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    reporte.setIdReporte(resultSet.getInt(1));
                }
            }catch (Exception e){
                throw new Exception("Error al agregar reporte: " + e.getMessage());
            }
        }
    }
    public void actualizarReporte(ReporteDTO reporte) throws Exception{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, reporte.getMatricula());
            preparedStatement.setString(2, reporte.getIdOrganizacion());
            preparedStatement.setString(3, reporte.getTipoReporte());
            preparedStatement.setString(4, reporte.getObservaciones());
            preparedStatement.setDate(5, java.sql.Date.valueOf(reporte.getFecha()));
            preparedStatement.setString(6, reporte.getEstado());
            preparedStatement.setInt(7, reporte.getIdReporte());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Error al actualizar reporte: " + e.getMessage());
        }
    }
    public ReporteDTO buscarReportePorId(int idReporte) throws Exception{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_ID)) {
            preparedStatement.setInt(1, idReporte);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new ReporteDTO(
                            resultSet.getInt("idReporte"),
                            resultSet.getString("Matricula"),
                            resultSet.getString("idOrganizacion"),
                            resultSet.getString("TipoReporte"),
                            resultSet.getString("Observaciones"),
                            resultSet.getDate("Fecha").toLocalDate(),
                            resultSet.getString("Estado")
                    );
                }
                return null;
            }
        } catch (Exception e) {
            throw new Exception("Error al buscar reporte por ID: " + e.getMessage());
        }
    }

    public List<ReporteDTO> buscarTodosReporte() throws Exception{
        List<ReporteDTO> listaReporte = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL); ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    ReporteDTO reporte = new ReporteDTO(
                            resultSet.getInt("idReporte"),
                            resultSet.getString("Matricula"),
                            resultSet.getString("idOrganizacion"),
                            resultSet.getString("TipoReporte"),
                            resultSet.getString("Observaciones"),
                            resultSet.getDate("Fecha").toLocalDate(),
                            resultSet.getString("Estado")
                    );
                    listaReporte.add(reporte);
                }
                return listaReporte;
            } catch (SQLException e) {
                throw new Exception("Error al buscar todos los reportes: " + e.getMessage());
        }
    }
}
