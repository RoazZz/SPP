package logica.dao;
import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.CalificacionFinalDTO;
import logica.enums.EstadoCalificacionFinal;
import logica.interfaces.CalificacionFinalDAOInterfaz;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;
import java.util.logging.Logger;
public class CalificacionFinalDAO implements CalificacionFinalDAOInterfaz {
    private static final Logger REGISTRADOR = Logger.getLogger(CalificacionFinalDAO.class.getName());
    private static final String SQL_UPSERT = "INSERT INTO calificacionfinal (Matricula, " +
            "DocumentosIniciales, ReportesMensuales, PrimerInforme210, PrimeraPresentacionColegiado, " +
            "PrimeraEvaluacionOV, SegundoInforme420, SegundaPresentacionColegiado, SegundaEvaluacionOV, " +
            "Autoevaluacion, CalificacionPorcentaje, CalificacionFinal, Estado) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE DocumentosIniciales = VALUES(DocumentosIniciales), " +
            "ReportesMensuales = VALUES(ReportesMensuales), PrimerInforme210 = VALUES(PrimerInforme210), " +
            "PrimeraPresentacionColegiado = VALUES(PrimeraPresentacionColegiado), " +
            "PrimeraEvaluacionOV = VALUES(PrimeraEvaluacionOV), SegundoInforme420 = VALUES(SegundoInforme420), " +
            "SegundaPresentacionColegiado = VALUES(SegundaPresentacionColegiado), " +
            "SegundaEvaluacionOV = VALUES(SegundaEvaluacionOV), Autoevaluacion = VALUES(Autoevaluacion), " +
            "CalificacionPorcentaje = VALUES(CalificacionPorcentaje), " +
            "CalificacionFinal = VALUES(CalificacionFinal), Estado = VALUES(Estado)";
    private static final String SQL_SELECT_BY_MATRICULA = "SELECT idCalificacionFinal, Matricula, " +
            "DocumentosIniciales, ReportesMensuales, PrimerInforme210, PrimeraPresentacionColegiado, " +
            "PrimeraEvaluacionOV, SegundoInforme420, SegundaPresentacionColegiado, SegundaEvaluacionOV, " +
            "Autoevaluacion, CalificacionPorcentaje, CalificacionFinal, Estado FROM calificacionfinal " +
            "WHERE Matricula = ?";
    private Connection conexion;
    public CalificacionFinalDAO() throws DAOExcepcion {
        try {
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioException) {
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de configuracion", ioException);
            throw new DAOExcepcion("Error de configuracion", ioException);
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexion SQL en CalificacionFinalDAO", sqlException);
            throw new DAOExcepcion("Error de base de datos", sqlException);
        }
    }
    @Override
    public boolean guardarCalificacionFinal(CalificacionFinalDTO calificacion) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPSERT)) {
            sentenciaPreparada.setString(1, calificacion.getMatricula());
            asignarCalificacion(sentenciaPreparada, 2, calificacion.getDocumentosIniciales());
            asignarCalificacion(sentenciaPreparada, 3, calificacion.getReportesMensuales());
            asignarCalificacion(sentenciaPreparada, 4, calificacion.getPrimerInforme210());
            asignarCalificacion(sentenciaPreparada, 5, calificacion.getPrimeraPresentacionColegiado());
            asignarCalificacion(sentenciaPreparada, 6, calificacion.getPrimeraEvaluacionOrganizacion());
            asignarCalificacion(sentenciaPreparada, 7, calificacion.getSegundoInforme420());
            asignarCalificacion(sentenciaPreparada, 8, calificacion.getSegundaPresentacionColegiado());
            asignarCalificacion(sentenciaPreparada, 9, calificacion.getSegundaEvaluacionOrganizacion());
            asignarCalificacion(sentenciaPreparada, 10, calificacion.getAutoevaluacion());
            asignarCalificacion(sentenciaPreparada, 11, calificacion.getCalificacionPorcentaje());
            asignarCalificacion(sentenciaPreparada, 12, calificacion.getCalificacionFinal());
            sentenciaPreparada.setString(13, calificacion.getEstado().name());
            sentenciaPreparada.executeUpdate();
            REGISTRADOR.log(Level.INFO, "Calificacion final guardada para " + calificacion.getMatricula());
            return true;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al guardar calificacion final", sqlException);
            throw new DAOExcepcion("Error al guardar la calificacion final", sqlException);
        }
    }
    @Override
    public CalificacionFinalDTO buscarPorMatricula(String matricula)
            throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_MATRICULA)) {
            sentenciaPreparada.setString(1, matricula);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return crearDTO(conjuntoResultado);
                }
                REGISTRADOR.log(Level.WARNING, "No existe calificacion final para " + matricula);
                throw new EntidadNoEncontradaExcepcion("No existe calificacion final para " + matricula);
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al buscar calificacion final", sqlException);
            throw new DAOExcepcion("Error al buscar la calificacion final", sqlException);
        }
    }
    private void asignarCalificacion(PreparedStatement sentenciaPreparada, int indice, Double calificacion)
            throws SQLException {
        if (calificacion == null) {
            sentenciaPreparada.setNull(indice, Types.DECIMAL);
        } else {
            sentenciaPreparada.setDouble(indice, calificacion);
        }
    }
    private CalificacionFinalDTO crearDTO(ResultSet conjuntoResultado) throws SQLException {
        return new CalificacionFinalDTO(
                conjuntoResultado.getInt("idCalificacionFinal"),
                conjuntoResultado.getString("Matricula"),
                leerCalificacion(conjuntoResultado, "DocumentosIniciales"),
                leerCalificacion(conjuntoResultado, "ReportesMensuales"),
                leerCalificacion(conjuntoResultado, "PrimerInforme210"),
                leerCalificacion(conjuntoResultado, "PrimeraPresentacionColegiado"),
                leerCalificacion(conjuntoResultado, "PrimeraEvaluacionOV"),
                leerCalificacion(conjuntoResultado, "SegundoInforme420"),
                leerCalificacion(conjuntoResultado, "SegundaPresentacionColegiado"),
                leerCalificacion(conjuntoResultado, "SegundaEvaluacionOV"),
                leerCalificacion(conjuntoResultado, "Autoevaluacion"),
                leerCalificacion(conjuntoResultado, "CalificacionPorcentaje"),
                leerCalificacion(conjuntoResultado, "CalificacionFinal"),
                EstadoCalificacionFinal.valueOf(conjuntoResultado.getString("Estado"))
        );
    }
    private Double leerCalificacion(ResultSet conjuntoResultado, String nombreColumna) throws SQLException {
        double valor = conjuntoResultado.getDouble(nombreColumna);
        if (conjuntoResultado.wasNull()) {
            return null;
        }
        return valor;
    }
}
