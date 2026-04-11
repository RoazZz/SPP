package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.AutoevaluacionDAOInterfaz;
import logica.dto.AutoevaluacionDTO;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoevaluacionDAO implements AutoevaluacionDAOInterfaz {
    private static String SQL_INSERT = "INSERT INTO autoevaluacion(idAutoEvaluacion, Matricula, Calificacion, Comentarios) VALUES (?,?,?,?)";
    private static String SQL_SELECT_BY_MATRICULA = "SELECT * FROM autoevaluacion WHERE Matricula = ?";
    private static String SQL_UPDATE = "UPDATE autoevaluacion SET Calificacion = ?, Comentarios = ? WHERE Matricula = ?";
    private static String SQL_EXISTS_PRACTICANTE = "SELECT 1 FROM practicante WHERE Matricula = ?";
    private static String SQL_SELECT_ALL = "SELECT * FROM autoevaluacion";

    private Connection conexion;
    private static final Logger logger = Logger.getLogger(AutoevaluacionDAO.class.getName());

    public AutoevaluacionDAO() throws DAOExcepcion {
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error al leer archivo de cofniguración", e);
            throw new DAOExcepcion("Error de configuracion", e);
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error de conexion SQL en ProfesorDAO", e);
            throw new DAOExcepcion("Error de base de datos", e);
        }
    }

    @Override
    public void agregarAutoevalaucion(AutoevaluacionDTO autoevaluacion) throws DAOExcepcion {
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
            logger.log(Level.INFO, "Autoevaluacion registrada exitosamente para: " + autoevaluacion.getMatricula());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al agregar autoevaluacion", e);
            throw new DAOExcepcion("Error al agregar autoevaluación", e);
        }
    }

    @Override
    public void actualizarAutoevaluacion(AutoevaluacionDTO autoevaluacion) throws DAOExcepcion {
            try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
                preparedStatement.setBigDecimal(1, autoevaluacion.getCalificacion());
                preparedStatement.setString(2, autoevaluacion.getComentarios());
                preparedStatement.setString(3, autoevaluacion.getMatricula());
                preparedStatement.executeUpdate();
                logger.log(Level.INFO, "Autoevaluacion actualizada para matricula: " + autoevaluacion.getMatricula());
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error SQL al actualizar datos de autoevaluacion", e);
                throw new DAOExcepcion("Error al actualizar autoevaluación", e);
            }
    }

    @Override
    public AutoevaluacionDTO buscarAutoevaluacionPorMatricula(String matricula) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
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
                }else{
                    logger.log(Level.WARNING, "No se encontró Autoevaluación con matricula: " + matricula);
                    throw new EntidadNoEncontradaExcepcion("Autoevaluación no encontrada con matricula: " + matricula);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al buscar autoevaluacion por matricula", e);
            throw new DAOExcepcion("Error al buscar autoevaluación por matrícula", e);
        }
    }

    @Override
    public List<AutoevaluacionDTO> obtenerTodasLasAutoevaluaciones() throws DAOExcepcion {
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
            return autoevaluaciones;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al listar todas las autoevaluaciones", e);
            throw new DAOExcepcion("Error al obtener todas las autoevaluaciones", e);
        }
    }
}
