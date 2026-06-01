package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.ReporteDAOInterfaz;
import logica.dto.ReporteDTO;
import logica.enums.EstadoReporte;
import logica.enums.TipoReporte;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReporteDAO implements ReporteDAOInterfaz {

    public static final String SQL_INSERT = "INSERT INTO reporte (idUsuario, TipoReporte, Fecha, Ruta, Estado, mes) " + "VALUES (?, ?, ?, ?, ?, ?)";
    public static final String SQL_UPDATE = "UPDATE reporte SET idUsuario = ?, TipoReporte = ?, Fecha = ?, Ruta = ?, " +
            "Estado = ? " + "WHERE idReporte = ?";
    public static final String SQL_UPDATE_CALIFICACION = "UPDATE reporte SET Calificacion = ?, Estado = ?" +
            " WHERE idReporte = ?";
    public static final String SQL_SELECT_BY_ID = "SELECT idReporte, idUsuario, TipoReporte, Fecha, Ruta, Estado, mes," +
            " Calificacion " + "FROM reporte WHERE idReporte = ?";
    public static final String SQL_SELECT_ALL = "SELECT idReporte, idUsuario, TipoReporte, Fecha, Ruta, Estado, mes," +
            "Calificacion " + "FROM reporte";
    public static final String SQL_SELECT_BY_USUARIO = "SELECT idReporte, idUsuario, TipoReporte, Fecha, Ruta, Estado," +
            " mes, Calificacion " + "FROM reporte WHERE idUsuario = ?";
    public static final String SQL_EXISTE_DUPLICADO = "SELECT COUNT(*) FROM reporte " + "WHERE idUsuario = ?" +
            " AND TipoReporte = ? AND (mes = ? OR (mes IS NULL AND ? IS NULL)) AND Estado = ?";
    public static final String SQL_SELECT_POR_SECCION =
            "SELECT r.idReporte, r.idUsuario, r.TipoReporte, r.Fecha, r.Ruta, r.Estado, " +
                    "r.mes, r.Calificacion " +
                    "FROM reporte r " +
                    "JOIN practicante p ON r.idUsuario = p.idUsuario " +
                    "WHERE p.idSeccion = ? AND r.Estado = 'ENTREGADO'";
    public static final String SQL_SELECT_ALL_ENTREGADOS =
            "SELECT idReporte, idUsuario, TipoReporte, Fecha, Ruta, Estado, mes, Calificacion " +
                    "FROM reporte WHERE Estado = 'ENTREGADO'";

    private final Connection conexion;
    private static final Logger REGISTRADOR = Logger.getLogger(ReporteDAO.class.getName());

    public ReporteDAO() throws DAOExcepcion {
        try {
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException | SQLException ioExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexión", ioExcepcion);
            throw new DAOExcepcion("Error al conectar con la base de datos", ioExcepcion);
        }
    }

    @Override
    public ReporteDTO agregarReporte(ReporteDTO reporte) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            sentenciaPreparada.setInt(1, reporte.getIdUsuario());
            sentenciaPreparada.setString(2, reporte.getTipoReporte().name());
            sentenciaPreparada.setDate(3, Date.valueOf(reporte.getFecha()));
            sentenciaPreparada.setString(4, reporte.getRuta());
            sentenciaPreparada.setString(5, reporte.getEstado().name());
            sentenciaPreparada.setString(6, reporte.getMes());
            sentenciaPreparada.executeUpdate();

            try (ResultSet conjuntoResultado = sentenciaPreparada.getGeneratedKeys()) {
                if (conjuntoResultado.next()) {
                    reporte.setIdReporte(conjuntoResultado.getInt(1));
                }
            }

            return reporte;
        } catch (SQLException sqlExcecion) {
            REGISTRADOR.log(Level.SEVERE, "Error al insertar reporte", sqlExcecion);
            throw new DAOExcepcion("Error SQL al agregar reporte", sqlExcecion);
        }
    }

    @Override
    public boolean actualizarReporte(ReporteDTO reporte) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE)) {
            sentenciaPreparada.setInt(1, reporte.getIdUsuario());
            sentenciaPreparada.setString(2, reporte.getTipoReporte().name());
            sentenciaPreparada.setDate(3, Date.valueOf(reporte.getFecha()));
            sentenciaPreparada.setString(4, reporte.getRuta());
            sentenciaPreparada.setString(5, reporte.getEstado().name());
            sentenciaPreparada.setInt(6, reporte.getIdReporte());

            if (sentenciaPreparada.executeUpdate() == 0) {
                throw new EntidadNoEncontradaExcepcion("Reporte no encontrado para actualizar");
            }

            return true;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al actualizar reporte", sqlExcepcion);
            throw new DAOExcepcion("Error al actualizar reporte", sqlExcepcion);
        }
    }

    public boolean calificarReporte(int idReporte, double calificacion) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE_CALIFICACION)) {
            sentenciaPreparada.setDouble(1, calificacion);
            sentenciaPreparada.setString(2, EstadoReporte.CALIFICADO.name());
            sentenciaPreparada.setInt(3, idReporte);

            if (sentenciaPreparada.executeUpdate() == 0) {
                throw new EntidadNoEncontradaExcepcion("Reporte no encontrado para calificar: " + idReporte);
            }

            return true;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al calificar reporte", sqlExcepcion);
            throw new DAOExcepcion("Error al calificar el reporte", sqlExcepcion);
        }
    }

    @Override
    public ReporteDTO buscarReportePorId(int idReporte) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_ID)) {
            sentenciaPreparada.setInt(1, idReporte);

            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return crearDTO(conjuntoResultado);
                }

                throw new EntidadNoEncontradaExcepcion("Reporte no encontrado con id: " + idReporte);
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al buscar reporte por id", sqlExcepcion);
            throw new DAOExcepcion("Error al buscar reporte", sqlExcepcion);
        }
    }

    @Override
    public List<ReporteDTO> listarTodosReporte() throws DAOExcepcion {
        List<ReporteDTO> listaReportes = new ArrayList<>();

        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            while (conjuntoResultado.next()) {
                listaReportes.add(crearDTO(conjuntoResultado));
            }

            return listaReportes;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al listar todos los reportes", sqlExcepcion);
            throw new DAOExcepcion("Error al listar reportes", sqlExcepcion);
        }
    }

    public List<ReporteDTO> listarReportesPorUsuario(int idUsuario) throws DAOExcepcion {
        List<ReporteDTO> listaReportes = new ArrayList<>();

        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_USUARIO)) {
            sentenciaPreparada.setInt(1, idUsuario);

            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                while (conjuntoResultado.next()) {
                    listaReportes.add(crearDTO(conjuntoResultado));
                }
            }

            return listaReportes;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al listar reportes por usuario", sqlExcepcion);
            throw new DAOExcepcion("Error al listar reportes por usuario", sqlExcepcion);
        }
    }

    public List<ReporteDTO> listarReportesPorSeccion(int idSeccion) throws DAOExcepcion {
        List<ReporteDTO> listaReportes = new ArrayList<>();

        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_POR_SECCION)) {
            sentenciaPreparada.setInt(1, idSeccion);

            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                while (conjuntoResultado.next()) {
                    listaReportes.add(crearDTO(conjuntoResultado));
                }
            }

            return listaReportes;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al listar reportes por seccion", sqlExcepcion);
            throw new DAOExcepcion("Error al listar reportes por seccion", sqlExcepcion);
        }
    }

    @Override
    public boolean existeDuplicado(int idUsuario, TipoReporte tipo, String mes, EstadoReporte estado) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_EXISTE_DUPLICADO)) {
            sentenciaPreparada.setInt(1, idUsuario);
            sentenciaPreparada.setString(2, tipo.name());
            sentenciaPreparada.setString(3, mes);
            sentenciaPreparada.setString(4, mes);
            sentenciaPreparada.setString(5, estado.name());

            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                return conjuntoResultado.next() && conjuntoResultado.getInt(1) > 0;
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al verificar duplicado", sqlExcepcion);
            throw new DAOExcepcion("Error al verificar duplicado", sqlExcepcion);
        }
    }



    private ReporteDTO crearDTO(ResultSet conjuntoResultado) throws SQLException {
        double calificacionRaw = conjuntoResultado.getDouble("Calificacion");
        Double calificacion = conjuntoResultado.wasNull() ? null : calificacionRaw;

        return new ReporteDTO(
                conjuntoResultado.getInt("idReporte"),
                conjuntoResultado.getInt("idUsuario"),
                TipoReporte.valueOf(conjuntoResultado.getString("TipoReporte")),
                conjuntoResultado.getDate("Fecha").toLocalDate(),
                conjuntoResultado.getString("Ruta"),
                EstadoReporte.valueOf(conjuntoResultado.getString("Estado")),
                conjuntoResultado.getString("mes"),
                calificacion
        );
    }

    public List<ReporteDTO> listarReportesEntregados() throws DAOExcepcion {
        List<ReporteDTO> listaReportes = new ArrayList<>();

        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL_ENTREGADOS);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            while (conjuntoResultado.next()) {
                listaReportes.add(crearDTO(conjuntoResultado));
            }

            return listaReportes;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al listar reportes entregados", sqlExcepcion);
            throw new DAOExcepcion("Error al listar reportes entregados", sqlExcepcion);
        }
    }
}