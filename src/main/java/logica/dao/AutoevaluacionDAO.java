package logica.dao;
import accesodatos.ConexionBD;
import interfaces.AutoevaluacionDAOInterfaz;
import logica.dto.AutoevaluacionDTO;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AutoevaluacionDAO extends ConexionBD implements AutoevaluacionDAOInterfaz {
    private static String SQL_INSERT = "INSERT INTO autoevaluacion(idAutoEvaluacion, Matricula, Calificacion, Comentarios) VALUES (?,?,?,?)";
    private static String SQL_SELECT_BY_MATRICULA = "SELECT * FROM autoevaluacion WHERE Matricula = ?";
    private static String SQL_UPDATE = "UPDATE autoevaluacion SET Calificacion = ?, Comentarios = ? WHERE Matricula = ?";
    private static String SQL_EXISTS_PRACTICANTE = "SELECT 1 FROM practicante WHERE Matricula = ?";
    private static String SQL_SELECT_ALL = "SELECT * FROM autoevaluacion";


    public AutoevaluacionDAO() {
        super();
    }

    @Override
    public void agregarAutoevalaucion(AutoevaluacionDTO autoevaluacion) throws Exception {
        try(PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setInt(1, autoevaluacion.getIdAutoevalaucion());
            preparedStatement.setString(2, autoevaluacion.getMatricula());
            preparedStatement.setBigDecimal(3, autoevaluacion.getCalificacion());
            preparedStatement.setString(4, autoevaluacion.getComentarios());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    autoevaluacion.setIdAutoevalaucion(resultSet.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al agregar autoevaluación: " + e.getMessage());

        }
    }

    @Override
    public void actualizarAutoevaluacion(AutoevaluacionDTO autoevaluacion) throws Exception {
        try (PreparedStatement ps = conexion.prepareStatement(SQL_EXISTS_PRACTICANTE)) {

            try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
                preparedStatement.setBigDecimal(1, autoevaluacion.getCalificacion());
                preparedStatement.setString(2, autoevaluacion.getComentarios());
                preparedStatement.setString(3, autoevaluacion.getMatricula());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                throw new Exception("Error al actualizar autoevaluación: " + e.getMessage());
            }
        }
    }

    @Override
    public AutoevaluacionDTO buscarAutoevaluacionPorMatricula(String matricula) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_MATRICULA)) {
            preparedStatement.setString(1, matricula);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new AutoevaluacionDTO(
                            resultSet.getInt("idAutoEvaluacion"),
                            resultSet.getString("Matricula"),
                            resultSet.getBigDecimal("Calificacion"),
                            resultSet.getString("Comentarios")
                    );
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar autoevaluación por matrícula: " + e.getMessage());
        }
        return null;
    }

    public List<AutoevaluacionDTO> obtenerTodasLasAutoevaluaciones() throws Exception {
        List<AutoevaluacionDTO> autoevaluaciones = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                AutoevaluacionDTO autoevaluacion = new AutoevaluacionDTO(
                        resultSet.getInt("idAutoEvaluacion"),
                        resultSet.getString("Matricula"),
                        resultSet.getBigDecimal("Calificacion"),
                        resultSet.getString("Comentarios")
                );
                autoevaluaciones.add(autoevaluacion);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener todas las autoevaluaciones: " + e.getMessage());
        }
        return autoevaluaciones;
    }

}
