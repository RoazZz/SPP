package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.AutoevaluacionDAOInterfaz;
import logica.dto.AutoevaluacionDTO;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoevaluacionDAO implements AutoevaluacionDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO autoevaluacion(Matricula, Calificacion, Comentarios) " +
            "VALUES (?,?,?)";
    private static final String SQL_SELECT_BY_MATRICULA = "SELECT * FROM autoevaluacion WHERE Matricula = ?";
    private static final String SQL_UPDATE = "UPDATE autoevaluacion SET Calificacion = ?, Comentarios = ?" +
            " WHERE Matricula = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM autoevaluacion";

    private Connection conexion;
    private static final Logger REGISTRADOR = Logger.getLogger(AutoevaluacionDAO.class.getName());

    public AutoevaluacionDAO() throws DAOExcepcion {
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioException){
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de cofniguración", ioException);
            throw new DAOExcepcion("Error de configuracion", ioException);
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexion SQL en ProfesorDAO", sqlException);
            throw new DAOExcepcion("Error de base de datos", sqlException);
        }
    }

    @Override
    public AutoevaluacionDTO agregarAutoevaluacion(AutoevaluacionDTO autoevaluacion) throws DAOExcepcion {
        try(PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)){
            sentenciaPreparada.setString(1, autoevaluacion.getMatricula());
            sentenciaPreparada.setBigDecimal(2, autoevaluacion.getCalificacion());
            sentenciaPreparada.setString(3, autoevaluacion.getComentarios());
            sentenciaPreparada.executeUpdate();

            try (ResultSet conjuntoResultado = sentenciaPreparada.getGeneratedKeys()) {
                if (conjuntoResultado.next()) {
                    autoevaluacion.setIdAutoevaluacion(conjuntoResultado.getInt(1));
                }
            }
            REGISTRADOR.log(Level.INFO, "Autoevaluacion registrada exitosamente para " + autoevaluacion.getMatricula());
            return autoevaluacion;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al agregar autoevaluacion", sqlException);
            throw new DAOExcepcion("Error al agregar autoevaluación", sqlException);
        }
    }

    @Override
    public boolean actualizarAutoevaluacion(AutoevaluacionDTO autoevaluacion) throws DAOExcepcion {
            try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE)) {
                sentenciaPreparada.setBigDecimal(1, autoevaluacion.getCalificacion());
                sentenciaPreparada.setString(2, autoevaluacion.getComentarios());
                sentenciaPreparada.setString(3, autoevaluacion.getMatricula());
                sentenciaPreparada.executeUpdate();

                int filasAfectadas = sentenciaPreparada.getUpdateCount();
                if (filasAfectadas > 0) {
                    REGISTRADOR.log(Level.INFO, "Autoevaluacion actualizada para matricula " + autoevaluacion.getMatricula());
                    return true;
                } else{
                    REGISTRADOR.log(Level.WARNING, "No se encontró Autoevaluación para actualizar con matricula " + autoevaluacion.getMatricula());
                    throw new EntidadNoEncontradaExcepcion("No se encontró autoevaluación para actualizar con matricula " + autoevaluacion.getMatricula());
                }
            } catch (SQLException sqlException) {
                REGISTRADOR.log(Level.SEVERE, "Error SQL al actualizar datos de autoevaluacion", sqlException);
                throw new DAOExcepcion("Error al actualizar autoevaluación", sqlException);
            }
    }

    @Override
    public AutoevaluacionDTO buscarAutoevaluacionPorMatricula(String matricula) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_MATRICULA)) {
            sentenciaPreparada.setString(1, matricula);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return new AutoevaluacionDTO(
                            conjuntoResultado.getInt("idAutoEvaluacion"),
                            conjuntoResultado.getString("Matricula"),
                            conjuntoResultado.getBigDecimal("Calificacion"),
                            conjuntoResultado.getString("Comentarios")
                    );
                } else{
                    REGISTRADOR.log(Level.WARNING, "No se encontró Autoevaluación con matricula " + matricula);
                    throw new EntidadNoEncontradaExcepcion("Autoevaluación no encontrada con matricula " + matricula);
                }
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al buscar autoevaluacion por matricula", sqlException);
            throw new DAOExcepcion("Error al buscar autoevaluación por matrícula", sqlException);
        }
    }

    @Override
    public List<AutoevaluacionDTO> obtenerTodasLasAutoevaluaciones() throws DAOExcepcion {
        List<AutoevaluacionDTO> autoevaluaciones = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            while (conjuntoResultado.next()) {
                AutoevaluacionDTO autoevaluacion = new AutoevaluacionDTO(
                        conjuntoResultado.getInt("idAutoEvaluacion"),
                        conjuntoResultado.getString("Matricula"),
                        conjuntoResultado.getBigDecimal("Calificacion"),
                        conjuntoResultado.getString("Comentarios")
                );
                autoevaluaciones.add(autoevaluacion);
            }
            return autoevaluaciones;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al listar todas las autoevaluaciones", sqlException);
            throw new DAOExcepcion("Error al obtener todas las autoevaluaciones", sqlException);
        }
    }
}
