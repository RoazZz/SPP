package logica.dao;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.ActividadDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.ActividadDTO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActividadDAO implements ActividadDAOInterfaz {
    private final Connection conexion;
    private static final Logger logger = Logger.getLogger(ActividadDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO Actividad (Matricula, Nombre, Descripcion, Fecha ) VALUES (?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID_ACTIVIDAD = "SELECT * FROM Actividad WHERE idActividad = ?";
    private static final String SQL_UPDATE = "UPDATE Actividad SET Matricula = ?, Nombre = ?, Descripcion = ?, Fecha = ? WHERE idActividad = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Actividad";

    public ActividadDAO() throws DAOExcepcion{
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error al leer archivo de configuración", e);
            throw new DAOExcepcion("Error de configuracion", e);
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error de conexion SQL en ActividadDAO", e);
            throw new DAOExcepcion("Error de base de datos", e);
        }
    }

    @Override
    public boolean agregarActividad(ActividadDTO actividad) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, actividad.getMatricula());
            preparedStatement.setString(2, actividad.getNombre());
            preparedStatement.setString(3, actividad.getDescripcion());
            preparedStatement.setDate(4, actividad.getFecha());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    actividad.setIdActividad(resultSet.getInt(1));
                }
            }
            logger.log(Level.INFO, "Actividad Insertada correctamente: " + actividad.getIdActividad());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al agregar la actividad", e);
            throw new DAOExcepcion("Error al agregar la actividad: ", e);
        }
        return false;
    }

    @Override
    public boolean actualizarActividad(ActividadDTO actividad) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, actividad.getMatricula());
            preparedStatement.setString(2, actividad.getNombre());
            preparedStatement.setString(3, actividad.getDescripcion());
            preparedStatement.setDate(4, actividad.getFecha());
            preparedStatement.setInt(5, actividad.getIdActividad());
            int filasAfectadas = preparedStatement.executeUpdate();
            if (filasAfectadas > 0) {
                logger.log(Level.SEVERE, "Actividad Actualizada correctamente: " + actividad.getIdActividad());
            } else {
                logger.log(Level.WARNING, "No se encontro Actividad con el ID: " + actividad.getIdActividad());
                throw new EntidadNoEncontradaExcepcion("No existe actividad con id: " + actividad.getIdActividad());
            }
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar la actividad", e);
            throw new DAOExcepcion("Error al actualizar la Actividad: ", e);
        }
    }


    @Override
    public ActividadDTO buscarActividadPorIdActividad(int idActividad) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
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
                }else{
                    logger.log(Level.WARNING, "No se encontró actividad con id: " + idActividad);
                    throw new EntidadNoEncontradaExcepcion("No existe actividad con id: " + idActividad);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar la actividad", e);
            throw new DAOExcepcion("Error al buscar actividad por idActividad: ", e);
        }
    }

    @Override
    public List<ActividadDTO> listarActividades() throws DAOExcepcion {
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
            logger.log(Level.SEVERE, "Error al listar actividades", e);
            throw new DAOExcepcion("Error al listar las actividades: ", e);
        }
        return listaActividad;
    }
}
