package logica.dao;

import interfaces.ActividadDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.ActividadDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ActividadDAO extends ConexionBD implements ActividadDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO Actividad (idActividad, Matricula, Nombre, Descripcion, Fecha ) VALUES ( ?, ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID_ACTIVIDAD = "SELECT * FROM Actividad WHERE idActividad = ?";
    private static final String SQL_UPDATE = "UPDATE Actividad SET Matricula = ?, Nombre = ?, Descripcion = ?, Fecha = ? WHERE idActividad = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Actividad";

    public ActividadDAO() {
        super();
    }

    @Override
    public void agregarActividad(ActividadDTO actividad) throws SQLException {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, actividad.getIdActividad());
            preparedStatement.setString(2, actividad.getMatricula());
            preparedStatement.setString(3, actividad.getNombre());
            preparedStatement.setString(4, actividad.getDescripcion());
            preparedStatement.setDate(5, actividad.getFecha());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    actividad.setIdActividad(resultSet.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al agregar la actividad: " + e.getMessage());
        }
    }

    @Override
    public void actualizarProyecto(ActividadDTO actividad) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, actividad.getMatricula());
            preparedStatement.setString(2, actividad.getNombre());
            preparedStatement.setString(3, actividad.getDescripcion());
            preparedStatement.setDate(4, actividad.getFecha());
            preparedStatement.setInt(5, actividad.getIdActividad());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al actualizar la Actividad: " + e.getMessage());
        }
    }

    @Override
    public ActividadDTO buscarActividadPorIdActividad(int idActividad) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_ID_ACTIVIDAD)) {
            preparedStatement.setInt(1, idActividad);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new ActividadDTO (
                            resultSet.getInt("idActividad"),
                            resultSet.getString("Matricula"),
                            resultSet.getString("Nombre"),
                            resultSet.getString("Descripcion"),
                            resultSet.getDate("Fecha")
                    );
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar Proyecto por idActividad: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ActividadDTO> listarActividades() throws Exception {
        List<ActividadDTO> listaActividad = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                ActividadDTO actividad = new ActividadDTO(
                        resultSet.getInt("idActividad"),
                        resultSet.getString("Matricula"),
                        resultSet.getString("Nombre"),
                        resultSet.getString("Descripcion"),
                        resultSet.getDate("Fecha")
                );
                listaActividad.add(actividad);
            }
        } catch (SQLException e) {
            throw new Exception("Error al listar las actividades: " + e.getMessage());
        }
        return listaActividad;
    }
}
