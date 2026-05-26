package logica.dao;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.ActividadDAOInterfaz;
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
    private static final Logger REGISTRADOR = Logger.getLogger(ActividadDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO Actividad (Matricula, Nombre, Descripcion, FechaInicio," +
            " FechaCierre ) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID_ACTIVIDAD = "SELECT * FROM Actividad WHERE idActividad = ?";
    private static final String SQL_UPDATE = "UPDATE Actividad SET Matricula = ?, Nombre = ?, Descripcion = ?," +
            " FechaInicio = ?, FechaCierre = ? WHERE idActividad = ?";
    private static final String SQL_SELECT_BY_MATRICULA = "SELECT * FROM Actividad WHERE Matricula = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Actividad";

    public ActividadDAO() throws DAOExcepcion{
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioException){
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de configuración", ioException);
            throw new DAOExcepcion("Error de configuracion", ioException);
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexion SQL en ActividadDAO", sqlException);
            throw new DAOExcepcion("Error de base de datos", sqlException);
        }
    }

    @Override
    public boolean agregarActividad(ActividadDTO actividad) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            sentenciaPreparada.setString(1, actividad.getMatricula());
            sentenciaPreparada.setString(2, actividad.getNombre());
            sentenciaPreparada.setString(3, actividad.getDescripcion());
            sentenciaPreparada.setDate(4, java.sql.Date.valueOf(actividad.getFechaInicio()));
            sentenciaPreparada.setDate(5, java.sql.Date.valueOf(actividad.getFechaCierre()));
            sentenciaPreparada.executeUpdate();

            try (ResultSet conjuntoResultado = sentenciaPreparada.getGeneratedKeys()) {
                if (conjuntoResultado.next()) {
                    actividad.setIdActividad(conjuntoResultado.getInt(1));
                }
            }
            REGISTRADOR.log(Level.INFO, "Actividad Insertada correctamente " + actividad.getIdActividad());
        } catch (SQLException e) {
            REGISTRADOR.log(Level.SEVERE, "Error al agregar la actividad", e);
            throw new DAOExcepcion("Error al agregar la actividad ", e);
        }
        return true;
    }

    @Override
    public boolean actualizarActividad(ActividadDTO actividad) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE)) {
            sentenciaPreparada.setString(1, actividad.getMatricula());
            sentenciaPreparada.setString(2, actividad.getNombre());
            sentenciaPreparada.setString(3, actividad.getDescripcion());
            sentenciaPreparada.setDate(4, java.sql.Date.valueOf(actividad.getFechaInicio()));
            sentenciaPreparada.setDate(5, java.sql.Date.valueOf(actividad.getFechaCierre()));
            sentenciaPreparada.setInt(6, actividad.getIdActividad());
            int filasAfectadas = sentenciaPreparada.executeUpdate();
            if (filasAfectadas > 0) {
                REGISTRADOR.log(Level.INFO, "Actividad Actualizada correctamente " + actividad.getIdActividad());
            } else {
                REGISTRADOR.log(Level.WARNING, "No se encontro Actividad con el ID " + actividad.getIdActividad());
                throw new EntidadNoEncontradaExcepcion("No existe actividad con id " + actividad.getIdActividad());
            }
            return true;

        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al actualizar la actividad", sqlException);
            throw new DAOExcepcion("Error al actualizar la Actividad ", sqlException);
        }
    }


    @Override
    public ActividadDTO buscarActividadPorIdActividad(int idActividad) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_BUSCAR_POR_ID_ACTIVIDAD)) {
            sentenciaPreparada.setInt(1, idActividad);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return new ActividadDTO (
                            conjuntoResultado.getInt("idActividad"),
                            conjuntoResultado.getString("Matricula"),
                            conjuntoResultado.getString("Nombre"),
                            conjuntoResultado.getString("Descripcion"),
                            conjuntoResultado.getDate("FechaInicio").toLocalDate(),
                            conjuntoResultado.getDate("FechaCierre").toLocalDate()
                    );
                } else{
                    REGISTRADOR.log(Level.WARNING, "No se encontró actividad con id " + idActividad);
                    throw new EntidadNoEncontradaExcepcion("No existe actividad con id " + idActividad);
                }
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al buscar la actividad", sqlException);
            throw new DAOExcepcion("Error al buscar actividad por idActividad ", sqlException);
        }
    }

    @Override
    public List<ActividadDTO> listarActividadesPorMatricula(String matricula) throws DAOExcepcion {
        List<ActividadDTO> listaActividad = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_MATRICULA)) {
            sentenciaPreparada.setString(1, matricula);
            ResultSet conjuntoResultado = sentenciaPreparada.executeQuery();
            while (conjuntoResultado.next()) {
                ActividadDTO actividad = new ActividadDTO(
                        conjuntoResultado.getInt("idActividad"),
                        conjuntoResultado.getString("Matricula"),
                        conjuntoResultado.getString("Nombre"),
                        conjuntoResultado.getString("Descripcion"),
                        conjuntoResultado.getDate("FechaInicio").toLocalDate(),
                        conjuntoResultado.getDate("FechaCierre").toLocalDate()
                );
                listaActividad.add(actividad);
            }
            return listaActividad;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al listar actividades por matrícula", sqlException);
            throw new DAOExcepcion("Error al listar actividades por matrícula: ", sqlException);
        }
    }

    @Override
    public List<ActividadDTO> listarActividades() throws DAOExcepcion {
        List<ActividadDTO> listaActividad = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            while (conjuntoResultado.next()) {
                ActividadDTO actividad = new ActividadDTO(
                        conjuntoResultado.getInt("idActividad"),
                        conjuntoResultado.getString("Matricula"),
                        conjuntoResultado.getString("Nombre"),
                        conjuntoResultado.getString("Descripcion"),
                        conjuntoResultado.getDate("FechaInicio").toLocalDate(),
                        conjuntoResultado.getDate("FechaCierre").toLocalDate()
                );
                listaActividad.add(actividad);
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al listar actividades", sqlException);
            throw new DAOExcepcion("Error al listar las actividades ", sqlException);
        }
        return listaActividad;
    }
}
