package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import interfaces.ReporteDAOInterfaz;
import logica.dto.ReporteDTO;
import logica.enums.TipoReporte;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReporteDAO implements ReporteDAOInterfaz {
    public static final String SQL_INSERT = "INSERT INTO reporte(idReporte, TipoReporte, Fecha, Ruta) VALUES (?, ?, ?, ?)";
    public static final String SQL_UPDATE = "UPDATE reporte SET TipoReporte = ?, Fecha = ?, Ruta = ? WHERE idReporte = ?";
    public static final String SQL_SELECT_BY_ID = "SELECT * FROM reporte WHERE idReporte = ?";
    public static final String SQL_SELECT_ALL = "SELECT * FROM reporte";

    private Connection conexion;
    private static final Logger logger = Logger.getLogger(ReporteDAO.class.getName());

    public ReporteDAO() throws DAOExcepcion {
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error de entrada/salida al configurar la conexión", e);
            throw new DAOExcepcion("Error al leer la configuración de la base de datos", e);
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error de SQL al intentar conectar", e);
            throw new DAOExcepcion("Error de acceso a la base de datos", e);
        }
    }

    @Override
    public void agregarReporte(ReporteDTO reporte) throws DAOExcepcion{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)) {
            preparedStatement.setInt(1, reporte.getIdReporte());
            preparedStatement.setString(2, reporte.getTipoReporte().name());
            preparedStatement.setDate(3, java.sql.Date.valueOf(reporte.getFecha()));
            preparedStatement.setString(4, reporte.getRuta());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    reporte.setIdReporte(resultSet.getInt(1));
                }
            }
            logger.log(Level.INFO, "Reporte agregado exitosamente. ID: " + reporte.getIdReporte());
        }catch (Exception e){
                logger.log(Level.SEVERE, "Error SQL al agregar reporte", e);
                throw new DAOExcepcion("Error al guardar el reporte en la base de datos", e);
        }
    }

    @Override
    public void actualizarReporte(ReporteDTO reporte) throws DAOExcepcion{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, reporte.getTipoReporte().name());
            preparedStatement.setDate(2, java.sql.Date.valueOf(reporte.getFecha()));
            preparedStatement.setString(3, reporte.getRuta());
            preparedStatement.setInt(4, reporte.getIdReporte());
            int filasAfectadas = preparedStatement.executeUpdate();
            if (filasAfectadas == 0) {
                throw new DAOExcepcion("No se encontró el reporte para actualizar con ID: " + reporte.getIdReporte(), null);
            }
            logger.log(Level.INFO, "Reporte actualizado exitosamente. ID: " + reporte.getIdReporte());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al actualizar reporte", e);
            throw new DAOExcepcion("Error al modificar los datos del reporte", e);
        }
    }

    @Override
    public ReporteDTO buscarReportePorId(int idReporte) throws DAOExcepcion{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_ID)) {
            preparedStatement.setInt(1, idReporte);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String tipoStr = resultSet.getString("TipoReporte");
                    return new ReporteDTO(
                            resultSet.getInt("idReporte"),
                            TipoReporte.valueOf(tipoStr),
                            resultSet.getDate("Fecha").toLocalDate(),
                            resultSet.getString("Ruta")
                    );
                }else{
                        logger.log(Level.WARNING, "No se encontró Reporte con ID: " + idReporte);
                        throw new DAOExcepcion("No se encontró el Reporte con el ID proporcionado", null);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al buscar reporte por ID: " + idReporte, e);
            throw new DAOExcepcion("Error al consultar el reporte", e);
        }
    }

    @Override
    public List<ReporteDTO> listarTodosReporte() throws DAOExcepcion{
        List<ReporteDTO> listaReporte = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL); ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    String tipoStr = resultSet.getString("TipoReporte");
                    ReporteDTO reporte = new ReporteDTO(
                            resultSet.getInt("idReporte"),
                            TipoReporte.valueOf(tipoStr),
                            resultSet.getDate("Fecha").toLocalDate(),
                            resultSet.getString("Reporte")
                    );
                    listaReporte.add(reporte);
                }
                return listaReporte;
            } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al listar todos los reportes", e);
            throw new DAOExcepcion("Error al obtener la lista de reportes", e);
        }
    }
}
