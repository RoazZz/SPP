package logica.dao;

import accesodatos.ConexionBD;
import interfaces.CoordinadorAsignaProyectoDAOInterfaz;
import logica.dto.CoordinadorAsignaProyectoDTO;
import logica.enums.TipoEstado;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CoordinadorAsignaProyectoDAO implements CoordinadorAsignaProyectoDAOInterfaz {
    private final Connection conexion;
    private static final Logger logger = Logger.getLogger(CoordinadorAsignaProyectoDAO.class.getName());
    public static final String SQL_INSERT = "INSERT INTO Asigna (NumeroDePersonal, idProyecto, Estado) VALUES (?, ?, ?)";
    public static final String SQL_UPDATE = "UPDATE Asigna SET Estado = ? WHERE idProyecto = ?";
    public static final String SQL_SELECT_BY_NUMERO_DE_PERSONAL = "SELECT * FROM Asigna WHERE NumeroDePersonal = ?";
    public static final String SQL_SELECT_BY_ID_PROYECTO = "SELECT * FROM Asigna WHERE idProyecto = ?";
    public static final String SQL_SELECT_ALL = "SELECT * FROM Asigna ";


    public CoordinadorAsignaProyectoDAO() throws SQLException, IOException {
        this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
    }

    @Override
    public void insertarAsignacionDeProyecto(CoordinadorAsignaProyectoDTO coordinadorAsignaProyectoDTO) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)) {
            preparedStatement.setString(1, coordinadorAsignaProyectoDTO.getNumeroDePersonal());
            preparedStatement.setInt(2, coordinadorAsignaProyectoDTO.getIdProyecto());
            preparedStatement.setString(3, coordinadorAsignaProyectoDTO.getTipoEstado().name());
            preparedStatement.executeUpdate();
        }catch (Exception e){
            throw new Exception("Error al insertar la asignacion de proyecto: " + e.getMessage());
        }
    }

    @Override
    public void actualizarAsigancionDeProyecto(CoordinadorAsignaProyectoDTO coordinadorAsignaProyectoDTO) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, coordinadorAsignaProyectoDTO.getTipoEstado().name());
            preparedStatement.setInt(2, coordinadorAsignaProyectoDTO.getIdProyecto());
            preparedStatement.executeUpdate();
        }catch (Exception e){
            throw new Exception("Error al actualizar la asignacion del proyecto: " + e.getMessage());
        }

    }

    @Override
    public List<CoordinadorAsignaProyectoDTO> obtenerAsignacionDeProyectoPorNumeroDePersonal(String numeroDePersonal) throws Exception {
        List<CoordinadorAsignaProyectoDTO> listaAsignacionesProyecto = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_NUMERO_DE_PERSONAL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                CoordinadorAsignaProyectoDTO asignacionProyecto = new CoordinadorAsignaProyectoDTO(
                        resultSet.getString("NumeroDePersonal"),
                        resultSet.getInt("idProyecto"),
                        TipoEstado.valueOf(resultSet.getString("EstadoProyecto"))
                );
                listaAsignacionesProyecto.add(asignacionProyecto);
            }
            return listaAsignacionesProyecto;
        }catch (Exception e){
            throw new Exception("Error al obtener las asignaciones de proyecto por numero de personal: " + e.getMessage());
        }
    }

    @Override
    public List<CoordinadorAsignaProyectoDTO> obtenerAsignacionDeProyectoPorIdSeccion(int idSeccion) throws Exception {
        List<CoordinadorAsignaProyectoDTO> listaAsignacionesProyecto = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_ID_PROYECTO);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                CoordinadorAsignaProyectoDTO asignacionProyecto = new CoordinadorAsignaProyectoDTO(
                        resultSet.getString("NumeroDePersonal"),
                        resultSet.getInt("idProyecto"),
                        TipoEstado.valueOf(resultSet.getString("EstadoProyecto"))
                );
                listaAsignacionesProyecto.add(asignacionProyecto);
            }
            return listaAsignacionesProyecto;
        }catch (Exception e){
            throw new Exception("Error al obtener las asignaciones de proyecto por id de Proyecto: " + e.getMessage());
        }
    }

    @Override
    public List<CoordinadorAsignaProyectoDTO> obtenerTodasLasAsignacionesDeProyecto() throws Exception {
        List<CoordinadorAsignaProyectoDTO> listaAsignacionesProyecto = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                CoordinadorAsignaProyectoDTO asignacionProyecto = new CoordinadorAsignaProyectoDTO(
                        resultSet.getString("NumeroDePersonal"),
                        resultSet.getInt("idProyecto"),
                        TipoEstado.valueOf(resultSet.getString("EstadoProyecto"))
                );
                listaAsignacionesProyecto.add(asignacionProyecto);
            }
            return listaAsignacionesProyecto;
        }catch (Exception e){
            throw new Exception("Error al obtener todas las asignaciones de proyecto: " + e.getMessage());
        }
    }
}
