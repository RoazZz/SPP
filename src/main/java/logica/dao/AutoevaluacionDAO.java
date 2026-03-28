package logica.dao;
import accesodatos.ConexionBD;
import interfaces.InterAutoevaluacionDAO;
import logica.dto.AutoevaluacionDTO;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AutoevaluacionDAO extends ConexionBD implements InterAutoevaluacionDAO {
    private static String SQL_INSERT = "INSERT INTO autoevaluacion(idAutoEvaluacion, Matricula, Calificacion, Comentarios) VALUES (?,?,?,?)";
    private static String SQL_SELECT_BY_MATRICULA = "SELECT * FROM autoevaluacion WHERE Matricula = ?";
    private static String SQL_UPDATE = "UPDATE autoevaluacion SET Calificacion = ?, Comentarios = ? WHERE Matricula = ?";
    private static String SQL_EXISTS_PRACTICANTE = "SELECT 1 FROM practicante WHERE Matricula = ?";

    public AutoevaluacionDAO() {
        super();
    }

    @Override
    public void agregar(AutoevaluacionDTO autoevaluacion) throws Exception {
        try (PreparedStatement ps = conexion.prepareStatement(SQL_EXISTS_PRACTICANTE)) {
            ps.setString(1, autoevaluacion.getMatricula());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("La matrícula '" + autoevaluacion.getMatricula() + "' no existe en practicante. " + "No se puede registrar la autoevaluación.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al validar matrícula en practicante: " + e.getMessage());
        }

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
    public void actualizar(AutoevaluacionDTO autoevaluacion) throws Exception {
        try (PreparedStatement ps = conexion.prepareStatement(SQL_EXISTS_PRACTICANTE)) {
            ps.setString(1, autoevaluacion.getMatricula());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("La matrícula '" + autoevaluacion.getMatricula() + "' no existe en practicante. " + "No se puede actualizar la autoevaluación.");
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al validar matrícula en practicante: " + e.getMessage());
        }

        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setBigDecimal(1, autoevaluacion.getCalificacion());
            preparedStatement.setString(2, autoevaluacion.getComentarios());
            preparedStatement.setString(3, autoevaluacion.getMatricula());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al actualizar autoevaluación: " + e.getMessage());
        }
    }

    @Override
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
