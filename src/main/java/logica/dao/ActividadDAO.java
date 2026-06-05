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

    private static final String SQL_INSERT = "INSERT INTO Actividad (Matricula, titulo, Descripcion, Fecha, " +
            "rutaDocumento) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID_ACTIVIDAD = "SELECT * FROM Actividad WHERE idActividad = ?";
    private static final String SQL_UPDATE = "UPDATE Actividad SET Matricula = ?, titulo = ?, Descripcion = ?, " +
            "Fecha = ?, rutaDocumento = ? WHERE idActividad = ?";
    private static final String SQL_SELECT_BY_MATRICULA = "SELECT * FROM Actividad WHERE Matricula = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Actividad";

    public ActividadDAO() throws DAOExcepcion {
        try {
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioException) {
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de configuración", ioException);
            throw new DAOExcepcion("Error de configuración", ioException);
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexión SQL en ActividadDAO", sqlException);
            throw new DAOExcepcion("Error de base de datos", sqlException);
        }
    }

    @Override
    public boolean registrarActividad(ActividadDTO actividad) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            sentenciaPreparada.setString(1, actividad.getMatricula());
            sentenciaPreparada.setString(2, actividad.getTitulo());
            sentenciaPreparada.setString(3, actividad.getDescripcion());
            sentenciaPreparada.setDate(4, java.sql.Date.valueOf(actividad.getFecha()));
            sentenciaPreparada.setString(5, actividad.getRutaDocumento());
            sentenciaPreparada.executeUpdate();

            try (ResultSet conjuntoResultado = sentenciaPreparada.getGeneratedKeys()) {
                if (conjuntoResultado.next()) {
                    actividad.setIdActividad(conjuntoResultado.getInt(1));
                }
            }
            REGISTRADOR.log(Level.INFO, "Actividad insertada correctamente " + actividad.getIdActividad());
        } catch (SQLException excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al agregar la actividad", excepcionCapturada);
            throw new DAOExcepcion("Error al agregar la actividad", excepcionCapturada);
        }
        return true;
    }

    @Override
    public boolean actualizarActividad(ActividadDTO actividad) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE)) {
            sentenciaPreparada.setString(1, actividad.getMatricula());
            sentenciaPreparada.setString(2, actividad.getTitulo());
            sentenciaPreparada.setString(3, actividad.getDescripcion());
            sentenciaPreparada.setDate(4, java.sql.Date.valueOf(actividad.getFecha()));
            sentenciaPreparada.setString(5, actividad.getRutaDocumento());
            sentenciaPreparada.setInt(6, actividad.getIdActividad());

            int filasAfectadas = sentenciaPreparada.executeUpdate();
            if (filasAfectadas == 0) {
                REGISTRADOR.log(Level.WARNING, "No se encontró Actividad con el ID " + actividad.getIdActividad());
                throw new EntidadNoEncontradaExcepcion("No existe actividad con id " + actividad.getIdActividad());
            }

            REGISTRADOR.log(Level.INFO, "Actividad actualizada correctamente " + actividad.getIdActividad());
            return true;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al actualizar la actividad", sqlException);
            throw new DAOExcepcion("Error al actualizar la Actividad", sqlException);
        }
    }

    @Override
    public ActividadDTO buscarActividadPorIdActividad(int idActividad) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_BUSCAR_POR_ID_ACTIVIDAD)) {
            sentenciaPreparada.setInt(1, idActividad);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (!conjuntoResultado.next()) {
                    REGISTRADOR.log(Level.WARNING, "No se encontró actividad con id " + idActividad);
                    throw new EntidadNoEncontradaExcepcion("No existe actividad con id " + idActividad);
                }

                return new ActividadDTO(
                        conjuntoResultado.getInt("idActividad"),
                        conjuntoResultado.getString("Matricula"),
                        conjuntoResultado.getString("titulo"),
                        conjuntoResultado.getString("Descripcion"),
                        conjuntoResultado.getDate("Fecha").toLocalDate(),
                        conjuntoResultado.getString("rutaDocumento")
                );
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al buscar la actividad", sqlException);
            throw new DAOExcepcion("Error al buscar actividad por idActividad", sqlException);
        }
    }

    @Override
    public List<ActividadDTO> listarActividadesPorMatricula(String matricula) throws DAOExcepcion {
        List<ActividadDTO> listaActividad = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_MATRICULA)) {
            sentenciaPreparada.setString(1, matricula);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                while (conjuntoResultado.next()) {
                    ActividadDTO actividad = new ActividadDTO(
                            conjuntoResultado.getInt("idActividad"),
                            conjuntoResultado.getString("Matricula"),
                            conjuntoResultado.getString("titulo"),
                            conjuntoResultado.getString("Descripcion"),
                            conjuntoResultado.getDate("Fecha").toLocalDate(),
                            conjuntoResultado.getString("rutaDocumento")
                    );
                    listaActividad.add(actividad);
                }
            }
            return listaActividad;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al listar actividades por matrícula", sqlException);
            throw new DAOExcepcion("Error al listar actividades por matrícula:", sqlException);
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
                        conjuntoResultado.getString("titulo"),
                        conjuntoResultado.getString("Descripcion"),
                        conjuntoResultado.getDate("Fecha").toLocalDate(),
                        conjuntoResultado.getString("rutaDocumento")
                );
                listaActividad.add(actividad);
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al listar actividades", sqlException);
            throw new DAOExcepcion("Error al listar las actividades", sqlException);
        }
        return listaActividad;
    }
}