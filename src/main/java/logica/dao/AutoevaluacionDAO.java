package logica.dao;
import accesodatos.ConexionBD;
import logica.dto.AutoevaluacionDTO;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AutoevaluacionDAO extends ConexionBD {
    private static String SQL_INSERT = "INSERT INTO autoevaluacion(idAutoEvaluacion, Matricula, Calificacion, Comentarios) VALUES (?,?,?,?)";
    private static String SQL_SELECT_BY_MATRICULA = "SELECT * FROM autoevaluacion WHERE Matricula = ?";
    private static String SQL_UPDATE = "UPDATE autoevaluacion SET Calificacion = ?, Comentarios = ? WHERE Matricula = ?";
    private static String SQL_DELETE = "DELETE FROM autoevaluacion WHERE Matricula = ?";

    public AutoevaluacionDAO() {
        super();
    }

    public void agregar(AutoevaluacionDTO autoevaluacion) throws Exception {
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

    public void actualizar(AutoevaluacionDTO autoevaluacion) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setBigDecimal(1, autoevaluacion.getCalificacion());
            preparedStatement.setString(2, autoevaluacion.getComentarios());
            preparedStatement.setString(3, autoevaluacion.getMatricula());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al actualizar autoevaluación: " + e.getMessage());
        }
    }

    public void eliminar(AutoevaluacionDTO autoevaluacion) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_DELETE)) {
            preparedStatement.setString(1, autoevaluacion.getMatricula());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al eliminar autoevaluación: " + e.getMessage());
        }
    }

    public AutoevaluacionDTO buscarPorMatricula(String matricula) throws Exception {
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


}
