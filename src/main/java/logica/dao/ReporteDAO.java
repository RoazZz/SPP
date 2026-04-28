package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.ReporteDAOInterfaz;
import logica.dto.ReporteDTO;
import logica.enums.TipoReporte;
import logica.enums.EstadoReporte;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReporteDAO implements ReporteDAOInterfaz {
    public static final String SQL_INSERT = "INSERT INTO reporte(TipoReporte, Fecha, Ruta, Estado) VALUES (?, ?, ?, ?)";
    public static final String SQL_UPDATE = "UPDATE reporte SET TipoReporte = ?, Fecha = ?, Ruta = ?, Estado = ? WHERE idReporte = ?";
    public static final String SQL_SELECT_BY_ID = "SELECT * FROM reporte WHERE idReporte = ?";
    public static final String SQL_SELECT_ALL = "SELECT * FROM reporte";

    private Connection conexion;
    private static final Logger logger = Logger.getLogger(ReporteDAO.class.getName());

    public ReporteDAO() throws DAOExcepcion {
        try {
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException | SQLException e) {
            logger.log(Level.SEVERE, "Error de conexión", e);
            throw new DAOExcepcion("Error al conectar con la base de datos", e);
        }
    }

    @Override
    public ReporteDTO agregarReporte(ReporteDTO reporte) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, reporte.getTipoReporte().name());
            preparedStatement.setDate(2, Date.valueOf(reporte.getFecha()));
            preparedStatement.setString(3, reporte.getRuta());
            preparedStatement.setString(4, reporte.getEstado().name());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    reporte.setIdReporte(resultSet.getInt(1));
                }
            }
            return reporte;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al insertar reporte", e);
            throw new DAOExcepcion("Error SQL al agregar reporte", e);
        }
    }

    @Override
    public boolean actualizarReporte(ReporteDTO reporte) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, reporte.getTipoReporte().name());
            preparedStatement.setDate(2, Date.valueOf(reporte.getFecha()));
            preparedStatement.setString(3, reporte.getRuta());
            preparedStatement.setString(4, reporte.getEstado().name());
            preparedStatement.setInt(5, reporte.getIdReporte());

            if (preparedStatement.executeUpdate() == 0) {
                throw new EntidadNoEncontradaExcepcion("Reporte no encontrado para actualizar");
            }
            return true;
        } catch (SQLException e) {
            throw new DAOExcepcion("Error al actualizar reporte", e);
        }
    }

    @Override
    public ReporteDTO buscarReportePorId(int idReporte) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_ID)) {
            preparedStatement.setInt(1, idReporte);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return crearDTO(rs);
                }
                throw new EntidadNoEncontradaExcepcion("Reporte no encontrado");
            }
        } catch (SQLException e) {
            throw new DAOExcepcion("Error al buscar reporte", e);
        }
    }

    @Override
    public List<ReporteDTO> listarTodosReporte() throws DAOExcepcion {
        List<ReporteDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(SQL_SELECT_ALL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(crearDTO(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOExcepcion("Error al listar reportes", e);
        }
    }

    private ReporteDTO crearDTO(ResultSet rs) throws SQLException {
        return new ReporteDTO(
                rs.getInt("idReporte"),
                TipoReporte.valueOf(rs.getString("TipoReporte")),
                rs.getDate("Fecha").toLocalDate(),
                rs.getString("Ruta"),
                EstadoReporte.valueOf(rs.getString("Estado"))
        );
    }
}