package logica.dao;
import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.AutoevaluacionDTO;
import logica.interfaces.AutoevaluacionDAOInterfaz;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
public class AutoevaluacionDAO implements AutoevaluacionDAOInterfaz {
    private static final Logger REGISTRADOR = Logger.getLogger(AutoevaluacionDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO autoevaluacion(Matricula, Calificacion, " +
            "Comentarios, RutaDocumento) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE autoevaluacion SET Calificacion = ?, Comentarios = ?, " +
            "RutaDocumento = ? WHERE Matricula = ?";
    private static final String SQL_CALIFICAR = "UPDATE autoevaluacion SET Calificacion = ? WHERE Matricula = ?";
    private static final String SQL_SELECT_BY_MATRICULA = "SELECT idAutoEvaluacion, Matricula, Calificacion, " +
            "Comentarios, RutaDocumento FROM autoevaluacion WHERE Matricula = ?";
    private static final String SQL_SELECT_ALL = "SELECT idAutoEvaluacion, Matricula, Calificacion, " +
            "Comentarios, RutaDocumento FROM autoevaluacion";
    private static final String SQL_EXISTE_POR_MATRICULA =
            "SELECT COUNT(*) FROM autoevaluacion WHERE Matricula = ?";
    private Connection conexion;
    public AutoevaluacionDAO() throws DAOExcepcion {
        try {
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioException) {
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de configuracion", ioException);
            throw new DAOExcepcion("Error de configuracion", ioException);
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexion SQL en AutoevaluacionDAO", sqlException);
            throw new DAOExcepcion("Error de base de datos", sqlException);
        }
    }
    @Override
    public AutoevaluacionDTO agregarAutoevaluacion(AutoevaluacionDTO autoevaluacion) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada =
                     conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            sentenciaPreparada.setString(1, autoevaluacion.getMatricula());
            sentenciaPreparada.setBigDecimal(2, autoevaluacion.getCalificacion());
            sentenciaPreparada.setString(3, autoevaluacion.getComentarios());
            sentenciaPreparada.setString(4, autoevaluacion.getRutaArchivo());
            sentenciaPreparada.executeUpdate();
            try (ResultSet conjuntoResultado = sentenciaPreparada.getGeneratedKeys()) {
                if (conjuntoResultado.next()) {
                    autoevaluacion.setIdAutoevaluacion(conjuntoResultado.getInt(1));
                }
            }
            REGISTRADOR.log(Level.INFO, "Autoevaluacion registrada para " + autoevaluacion.getMatricula());
            return autoevaluacion;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al agregar autoevaluacion", sqlException);
            throw new DAOExcepcion("Error al agregar autoevaluacion", sqlException);
        }
    }
    @Override
    public boolean actualizarAutoevaluacion(AutoevaluacionDTO autoevaluacion) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE)) {
            sentenciaPreparada.setBigDecimal(1, autoevaluacion.getCalificacion());
            sentenciaPreparada.setString(2, autoevaluacion.getComentarios());
            sentenciaPreparada.setString(3, autoevaluacion.getRutaArchivo());
            sentenciaPreparada.setString(4, autoevaluacion.getMatricula());
            sentenciaPreparada.executeUpdate();
            int filasAfectadas = sentenciaPreparada.getUpdateCount();
            if (filasAfectadas > 0) {
                REGISTRADOR.log(Level.INFO, "Autoevaluacion actualizada para " + autoevaluacion.getMatricula());
                return true;
            } else {
                REGISTRADOR.log(Level.WARNING, "No se encontro autoevaluacion para " + autoevaluacion.getMatricula());
                throw new EntidadNoEncontradaExcepcion("No existe autoevaluacion con matricula " + autoevaluacion.getMatricula());
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al actualizar autoevaluacion", sqlException);
            throw new DAOExcepcion("Error al actualizar autoevaluacion", sqlException);
        }
    }
    @Override
    public boolean calificarAutoevaluacion(String matricula, double calificacion) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_CALIFICAR)) {
            sentenciaPreparada.setDouble(1, calificacion);
            sentenciaPreparada.setString(2, matricula);
            sentenciaPreparada.executeUpdate();
            int filasAfectadas = sentenciaPreparada.getUpdateCount();
            if (filasAfectadas > 0) {
                REGISTRADOR.log(Level.INFO, "Autoevaluacion calificada para " + matricula);
                return true;
            }
            REGISTRADOR.log(Level.WARNING, "No se encontro autoevaluacion para calificar " + matricula);
            return false;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al calificar autoevaluacion", sqlException);
            throw new DAOExcepcion("Error al calificar autoevaluacion", sqlException);
        }
    }
    @Override
    public AutoevaluacionDTO buscarAutoevaluacionPorMatricula(String matricula)
            throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_MATRICULA)) {
            sentenciaPreparada.setString(1, matricula);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return crearDTO(conjuntoResultado);
                } else {
                    REGISTRADOR.log(Level.WARNING, "No se encontro autoevaluacion con matricula " + matricula);
                    throw new EntidadNoEncontradaExcepcion("Autoevaluacion no encontrada con matricula " + matricula);
                }
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al buscar autoevaluacion", sqlException);
            throw new DAOExcepcion("Error al buscar autoevaluacion por matricula", sqlException);
        }
    }
    @Override
    public List<AutoevaluacionDTO> obtenerTodasLasAutoevaluaciones() throws DAOExcepcion {
        List<AutoevaluacionDTO> autoevaluaciones = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            while (conjuntoResultado.next()) {
                autoevaluaciones.add(crearDTO(conjuntoResultado));
            }
            return autoevaluaciones;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al listar autoevaluaciones", sqlException);
            throw new DAOExcepcion("Error al obtener todas las autoevaluaciones", sqlException);
        }
    }
    private AutoevaluacionDTO crearDTO(ResultSet conjuntoResultado) throws SQLException {
        return new AutoevaluacionDTO(
                conjuntoResultado.getInt("idAutoEvaluacion"),
                conjuntoResultado.getString("Matricula"),
                conjuntoResultado.getBigDecimal("Calificacion"),
                conjuntoResultado.getString("Comentarios"),
                conjuntoResultado.getString("RutaDocumento")
        );
    }

    @Override
    public boolean existeAutoevaluacionPorMatricula(String matricula) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_EXISTE_POR_MATRICULA)) {
            sentenciaPreparada.setString(1, matricula);

            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                return conjuntoResultado.next() && conjuntoResultado.getInt(1) > 0;
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al verificar existencia de autoevaluación", sqlExcepcion);
            throw new DAOExcepcion("Error al verificar existencia de autoevaluación", sqlExcepcion);
        }
    }
}