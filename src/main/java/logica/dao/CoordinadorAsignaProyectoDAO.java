package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.CoordinadorAsignaProyectoDAOInterfaz;
import logica.dto.CoordinadorAsignaProyectoDTO;
import logica.enums.EstadoAsignacionProyecto;
import logica.enums.TipoEstado;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoordinadorAsignaProyectoDAO implements CoordinadorAsignaProyectoDAOInterfaz {
    private final Connection conexion;
    private static final Logger logger = Logger.getLogger(CoordinadorAsignaProyectoDAO.class.getName());
    public static final String SQL_INSERT = "INSERT INTO Asigna (NumeroDePersonal, idProyecto, Estado) VALUES (?, ?, ?)";
    public static final String SQL_UPDATE = "UPDATE Asigna SET Estado = ? WHERE idProyecto = ?";
    public static final String SQL_SELECT_BY_NUMERO_DE_PERSONAL = "SELECT * FROM Asigna WHERE NumeroDePersonal = ?";
    public static final String SQL_SELECT_BY_ID_PROYECTO = "SELECT * FROM Asigna WHERE idProyecto = ?";
    public static final String SQL_SELECT_ALL = "SELECT * FROM Asigna ";


    public CoordinadorAsignaProyectoDAO() throws DAOExcepcion {
        try{
        this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error al leer archivo de configuración", e);
            throw new DAOExcepcion("Error de configuracion", e);
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error de conexion SQL en CoordinadorAsignaProyectoDAO", e);
            throw new DAOExcepcion("Error de base de datos", e);
        }
    }

   @Override
   public void insertarAsignacionDeProyecto(CoordinadorAsignaProyectoDTO coordinadorAsignaProyectoDTO) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)) {
            preparedStatement.setString(1, coordinadorAsignaProyectoDTO.getNumeroDePersonal());
            preparedStatement.setInt(2, coordinadorAsignaProyectoDTO.getIdProyecto());
            preparedStatement.setString(3, coordinadorAsignaProyectoDTO.getTipoEstado().name());
            preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Asignación de Proyecto creada exitosamente: " + coordinadorAsignaProyectoDTO.getNumeroDePersonal());
        }catch (Exception e){
            logger.log(Level.SEVERE, "Error al insertar Asignación de Proyecto", e);
            throw new DAOExcepcion("Error al insertar la asignacion de proyecto: ", e);
        }
    }

    @Override
    public void actualizarAsignacionDeProyecto(CoordinadorAsignaProyectoDTO coordinadorAsignaProyectoDTO) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, coordinadorAsignaProyectoDTO.getTipoEstado().name());
            preparedStatement.setInt(2, coordinadorAsignaProyectoDTO.getIdProyecto());
            preparedStatement.executeUpdate();
        }catch (Exception e){
            logger.log(Level.SEVERE, "Error al actualizar Asignación de Proyecto", e);
            throw new DAOExcepcion("Error al actualizar la asignacion del proyecto: ", e);
        }

    }

    @Override
    public List<CoordinadorAsignaProyectoDTO> obtenerAsignacionDeProyectoPorNumeroDePersonal(String numeroDePersonal) throws DAOExcepcion {
        List<CoordinadorAsignaProyectoDTO> listaAsignacionesProyecto = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_NUMERO_DE_PERSONAL)) {
            preparedStatement.setString(1, numeroDePersonal); // ← aquí dentro, no en el try()
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    CoordinadorAsignaProyectoDTO asignacionProyecto = new CoordinadorAsignaProyectoDTO(
                            resultSet.getString("NumeroDePersonal"),
                            resultSet.getInt("idProyecto"),
                            EstadoAsignacionProyecto.valueOf(resultSet.getString("Estado").replace(" ", "_"))
                    );
                    listaAsignacionesProyecto.add(asignacionProyecto);
                }
            }
            return listaAsignacionesProyecto;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al listar las Asignaciones de Proyecto por numero de personal", e);
            throw new DAOExcepcion("Error al obtener las asignaciones de proyecto por numero de personal: ", e);
        }
    }

    @Override
    public List<CoordinadorAsignaProyectoDTO> obtenerAsignacionDeProyectoPorIdSeccion(int idSeccion) throws DAOExcepcion {
        List<CoordinadorAsignaProyectoDTO> listaAsignacionesProyecto = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_ID_PROYECTO);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                CoordinadorAsignaProyectoDTO asignacionProyecto = new CoordinadorAsignaProyectoDTO(
                        resultSet.getString("NumeroDePersonal"),
                        resultSet.getInt("idProyecto"),
                        EstadoAsignacionProyecto.valueOf(resultSet.getString("Estado").replace(" ","_"))
                );
                listaAsignacionesProyecto.add(asignacionProyecto);
            }
            return listaAsignacionesProyecto;
        }catch (Exception e){
            logger.log(Level.SEVERE, "Error al listar las Asignaciones de Proyecto por id de proyecto", e);
            throw new DAOExcepcion("Error al obtener las asignaciones de proyecto por id de Proyecto: ", e);
        }
    }

    @Override
    public List<CoordinadorAsignaProyectoDTO> obtenerTodasLasAsignacionesDeProyecto() throws DAOExcepcion {
        List<CoordinadorAsignaProyectoDTO> listaAsignacionesProyecto = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                CoordinadorAsignaProyectoDTO asignacionProyecto = new CoordinadorAsignaProyectoDTO(
                        resultSet.getString("NumeroDePersonal"),
                        resultSet.getInt("idProyecto"),
                        EstadoAsignacionProyecto.valueOf(resultSet.getString("Estado").replace(" ","_"))
                );
                listaAsignacionesProyecto.add(asignacionProyecto);
            }
            return listaAsignacionesProyecto;
        }catch (Exception e){
            logger.log(Level.SEVERE, "Error al listar las asignaciones de proyecto", e);
            throw new DAOExcepcion("Error al obtener todas las asignaciones de proyecto: ", e);
        }
    }
}
