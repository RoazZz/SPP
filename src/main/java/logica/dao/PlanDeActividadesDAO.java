package logica.dao;

import accesodatos.ConexionBD;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.PlanDeActivadesDAOInterfaz;
import logica.dto.PlanDeActividadesDTO;
import excepciones.DAOExcepcion;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PlanDeActividadesDAO implements PlanDeActivadesDAOInterfaz {
    public static final String SQL_INSERT = "INSERT INTO plandeactividades(Matricula, idProyecto, Descripcion) VALUES (?, ?, ?)";
    public static final String SQL_UPDATE = "UPDATE plandeactividades SET Matricula = ?, idProyecto = ?, Descripcion = ? WHERE idPlanActividades = ?";
    public static final String SQL_SELECT_BY_MATRICULA = "SELECT * FROM plandeactividades WHERE Matricula = ?";
    public static final String SQL_SELECT_ALL = "SELECT * FROM plandeactividades";
    public static final String SQL_SELECT_BY_ID = "SELECT * FROM plandeactividades WHERE idPlanActividades = ?";

    private Connection conexion;
    private static final Logger logger = Logger.getLogger(PlanDeActividadesDAO.class.getName());

    public PlanDeActividadesDAO() throws DAOExcepcion {
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
    public PlanDeActividadesDTO agregarPlanDeActividades(PlanDeActividadesDTO planDeActividadesDTO) throws DAOExcepcion{
        try(PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)){
            preparedStatement.setString(1, planDeActividadesDTO.getMatricula());
            preparedStatement.setInt(2, planDeActividadesDTO.getIdProyecto());
            preparedStatement.setString(3, planDeActividadesDTO.getDescripcion());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    planDeActividadesDTO.setIdplanActividades(resultSet.getInt(1));
                }
            }
            logger.info("Plan de actividades agregado exitosamente");
            return planDeActividadesDTO;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al agregar plan de actividades", e);
            throw new DAOExcepcion("Error al agregar el plan de actividades: ", e);
        }
    }

    @Override
    public boolean actualizarPlanDeActividades(PlanDeActividadesDTO planDeActividadesDTO) throws DAOExcepcion {
        try(PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)){
            preparedStatement.setString(1, planDeActividadesDTO.getMatricula());
            preparedStatement.setInt(2, planDeActividadesDTO.getIdProyecto());
            preparedStatement.setString(3, planDeActividadesDTO.getDescripcion());
            preparedStatement.setInt(4, planDeActividadesDTO.getIdplanActividades());

            int filasAfectadas = preparedStatement.executeUpdate();
            if (filasAfectadas > 0) {
                logger.info("Plan de actividades actualizado exitosamente, ID: " + planDeActividadesDTO.getIdplanActividades());
                return true;
            } else {
                logger.log(Level.WARNING, "No se encontró el plan de actividades para actualizar, ID: " + planDeActividadesDTO.getIdplanActividades());
                throw new EntidadNoEncontradaExcepcion("No se encontró el plan de actividades con el ID: " + planDeActividadesDTO.getIdplanActividades());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar plan ID: " + planDeActividadesDTO.getIdplanActividades(), e);
            throw new DAOExcepcion("Error al modificar los datos del plan", e);
        }
    }

    @Override
    public List<PlanDeActividadesDTO> obtenerPlanesDeActividadesPorMatricula(String matricula) throws DAOExcepcion{
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
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar planes por matrícula: " + matricula, e);
            throw new DAOExcepcion("Error al consultar planes por matrícula", e);        }
    }

    @Override
    public List<PlanDeActividadesDTO> obtenerTodosLosPlanesDeActividades() throws DAOExcepcion{
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
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener todos los planes", e);
            throw new DAOExcepcion("Error al obtener la lista completa de planes", e);
        }
    }

    public PlanDeActividadesDTO obtenerPlanDeActividadesPorId(int idPlanDeActividades) throws DAOExcepcion{
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
                }else{
                    logger.log(Level.WARNING, "No se encontró Plan de Actividades con ID: " + idPlanDeActividades);
                    throw new EntidadNoEncontradaExcepcion("No se encontró el Plan de Actividades con el ID proporcionado");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar plan por ID: " + idPlanDeActividades, e);
            throw new DAOExcepcion("Error al buscar el plan específico", e);
        }
    }
}
