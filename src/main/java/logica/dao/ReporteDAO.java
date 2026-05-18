package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.ReporteDAOInterfaz;
import logica.dto.ReporteDTO;
import logica.enums.TipoReporte;
import logica.enums.EstadoReporte;

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
    public static final String SQL_INSERT = "INSERT INTO reporte(idUsuario, TipoReporte, Fecha, Ruta, Estado, mes, hashArchivo, hashContenido) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SQL_UPDATE = "UPDATE reporte SET idUsuario = ?, TipoReporte = ?, Fecha = ?, Ruta = ?, Estado = ? WHERE idReporte = ?";
    public static final String SQL_SELECT_BY_ID = "SELECT * FROM reporte WHERE idReporte = ?";
    public static final String SQL_SELECT_ALL = "SELECT * FROM reporte";
    public static final String SQL_SELECT_BY_USUARIO = "SELECT * FROM reporte WHERE idUsuario = ?";
    public static final String SQL_EXISTE_DUPLICADO = "SELECT COUNT(*) FROM reporte WHERE idUsuario = ? AND TipoReporte = ? AND (mes = ? OR (mes IS NULL AND ? IS NULL)) AND Estado = ?";
    public static final String SQL_EXISTE_HASH = "SELECT COUNT(*) FROM reporte WHERE hashArchivo = ? OR hashContenido = ?";

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
            preparedStatement.setInt(1, reporte.getIdUsuario());
            preparedStatement.setString(2, reporte.getTipoReporte().name());
            preparedStatement.setDate(3, Date.valueOf(reporte.getFecha()));
            preparedStatement.setString(4, reporte.getRuta());
            preparedStatement.setString(5, reporte.getEstado().name());
            preparedStatement.setString(6, reporte.getMes());
            preparedStatement.setString(7, reporte.getHashArchivo());
            preparedStatement.setString(8, reporte.getHashContenido());
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
            preparedStatement.setInt(1, reporte.getIdUsuario());
            preparedStatement.setString(2, reporte.getTipoReporte().name());
            preparedStatement.setDate(3, Date.valueOf(reporte.getFecha()));
            preparedStatement.setString(4, reporte.getRuta());
            preparedStatement.setString(5, reporte.getEstado().name());
            preparedStatement.setInt(6, reporte.getIdReporte());
            preparedStatement.setString(7, reporte.getMes());

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
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return crearDTO(resultSet);
                }
                throw new EntidadNoEncontradaExcepcion("Reporte no encontrado con id: " + idReporte);
            }
        } catch (SQLException e) {
            throw new DAOExcepcion("Error al buscar reporte", e);
        }
    }

    @Override
    public List<ReporteDTO> listarTodosReporte() throws DAOExcepcion {
        List<ReporteDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                lista.add(crearDTO(resultSet));
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOExcepcion("Error al listar reportes", e);
        }
    }

    public List<ReporteDTO> listarReportesPorUsuario(int idUsuario) throws DAOExcepcion {
        List<ReporteDTO> lista = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(SQL_SELECT_BY_USUARIO)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(crearDTO(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al listar reportes por usuario", e);
            throw new DAOExcepcion("Error al listar reportes por usuario", e);
        }
    }

    private ReporteDTO crearDTO(ResultSet resultSet) throws SQLException {
        return new ReporteDTO(
                resultSet.getInt("idReporte"),
                resultSet.getInt("idUsuario"),
                TipoReporte.valueOf(resultSet.getString("TipoReporte")),
                resultSet.getDate("Fecha").toLocalDate(),
                resultSet.getString("Ruta"),
                EstadoReporte.valueOf(resultSet.getString("Estado")),
                resultSet.getString("mes"),
                resultSet.getString("hashArchivo"),
                resultSet.getString("hashContenido")
        );
    }

    @Override
    public boolean existeDuplicado(int idUsuario, TipoReporte tipo, String mes, EstadoReporte estado) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_EXISTE_DUPLICADO)) {
            preparedStatement.setInt(1, idUsuario);
            preparedStatement.setString(2, tipo.name());
            preparedStatement.setString(3, mes);
            preparedStatement.setString(4, mes);
            preparedStatement.setString(5, estado.name());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOExcepcion("Error al verificar duplicado", e);
        }
    }

    @Override
    public boolean existeHashDuplicado(String hashArchivo, String hashContenido) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_EXISTE_HASH)) {
            preparedStatement.setString(1, hashArchivo);
            preparedStatement.setString(2, hashContenido);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOExcepcion("Error al verificar hash duplicado", e);
        }
    }

}