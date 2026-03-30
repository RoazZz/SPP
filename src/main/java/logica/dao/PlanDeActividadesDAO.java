package logica.dao;

import accesodatos.ConexionBD;
import interfaces.PlanDeActivadesDAOInterfaz;
import logica.dto.PlanDeActividadesDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlanDeActividadesDAO extends ConexionBD implements PlanDeActivadesDAOInterfaz {
    public static final String SQL_INSERT = "INSERT INTO plandeactividades(idPlanActividades, Matricula, idProyecto, Descripcion) VALUES (?, ?, ?, ?)";
    public static final String SQL_UPDATE = "UPDATE plandeactividades SET Matricula = ?, idProyecto = ?, Descripcion = ? WHERE idPlanActividades = ?";
    public static final String SQL_SELECT_BY_MATRICULA = "SELECT * FROM plandeactividades WHERE Matricula = ?";
    public static final String SQL_SELECT_ALL = "SELECT * FROM plandeactividades";
    public static final String SQL_SELECT_BY_ID = "SELECT * FROM plandeactividades WHERE idPlanActividades = ?";

    @Override
    public void agregarPlanDeActividades(PlanDeActividadesDTO planDeActividadesDTO) throws Exception{
        try(PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setInt(1, planDeActividadesDTO.getIdplanActividades());
            preparedStatement.setString(2, planDeActividadesDTO.getMatricula());
            preparedStatement.setInt(3, planDeActividadesDTO.getIdProyecto());
            preparedStatement.setString(4, planDeActividadesDTO.getDescripcion());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    planDeActividadesDTO.setIdplanActividades(resultSet.getInt(1));
                }
            }
        } catch (Exception e) {
            throw new Exception("Error al agregar el plan de actividades: " + e.getMessage());

        }
    }

    @Override
    public void actualizarPlanDeActividades(PlanDeActividadesDTO planDeActividadesDTO) throws Exception{
        try(PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)){
            preparedStatement.setString(1, planDeActividadesDTO.getMatricula());
            preparedStatement.setInt(2, planDeActividadesDTO.getIdProyecto());
            preparedStatement.setString(3, planDeActividadesDTO.getDescripcion());
            preparedStatement.setInt(4, planDeActividadesDTO.getIdplanActividades());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Error al actualizar el plan de actividades: " + e.getMessage());
        }
    }

    @Override
    public List<PlanDeActividadesDTO> obtenerPlanesDeActividadesPorMatricula(String matricula) throws Exception{
        List<PlanDeActividadesDTO> planDeActividadesDTO = new ArrayList<>();
        try(PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_MATRICULA)){
            preparedStatement.setString(1, matricula);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    PlanDeActividadesDTO plan = new PlanDeActividadesDTO(
                            resultSet.getInt("idPlanActividades"),
                            resultSet.getString("Matricula"),
                            resultSet.getInt("idProyecto"),
                            resultSet.getString("Descripcion")
                    );
                    planDeActividadesDTO.add(plan);
                }
                return planDeActividadesDTO;
            }
        } catch (Exception e) {
            throw new Exception("Error al obtener los planes de actividades por matrícula: " + e.getMessage());
        }
    }

    @Override
    public List<PlanDeActividadesDTO> obtenerTodosLosPlanesDeActividades() throws Exception{
        List<PlanDeActividadesDTO> planDeActividadesDTO = new ArrayList<>();
        try(PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL)){
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while(resultSet.next()){
                    PlanDeActividadesDTO plan = new PlanDeActividadesDTO(
                            resultSet.getInt("idPlanActividades"),
                            resultSet.getString("Matricula"),
                            resultSet.getInt("idProyecto"),
                            resultSet.getString("Descripcion")
                    );
                    planDeActividadesDTO.add(plan);
                }
                return planDeActividadesDTO;
            }
        } catch (Exception e) {
            throw new Exception("Error al obtener todos los planes de actividades: " + e.getMessage());
        }
    }

    public PlanDeActividadesDTO obtenerPlanDeActividadesPorId(int idPlanDeActividades) throws Exception{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_ID)) {
            preparedStatement.setInt(1, idPlanDeActividades);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new PlanDeActividadesDTO(
                            resultSet.getInt("idPlanActividades"),
                            resultSet.getString("Matricula"),
                            resultSet.getInt("idProyecto"),
                            resultSet.getString("Descripcion")
                    );
                } else {
                    throw new Exception("No se encontró el plan de actividades con el ID proporcionado.");
                }
            }
        } catch (Exception e) {
            throw new Exception("Error al obtener el plan de actividades por ID: " + e.getMessage());
        }
    }
}
