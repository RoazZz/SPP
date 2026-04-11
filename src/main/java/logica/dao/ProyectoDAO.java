package logica.dao;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.ProyectoDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.ProyectoDTO;

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

public class ProyectoDAO implements ProyectoDAOInterfaz {
    private final Connection conexion;
    private static final Logger logger = Logger.getLogger(ProyectoDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO Proyecto (idOrganizacion, numeroDePersonal, Nombre, Descripcion) VALUES ( ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID_PROYECTO = "SELECT * FROM Proyecto WHERE idProyecto = ?";
    private static final String SQL_UPDATE = "UPDATE Proyecto SET Nombre = ?, Descripcion = ? WHERE idProyecto = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Proyecto";

    public ProyectoDAO() throws IOException, SQLException {
        this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
    }

    @Override
    public void agregarProyecto(ProyectoDTO proyecto) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, proyecto.getIdOrganizacion());
            preparedStatement.setString(2, proyecto.getNumeroDePersonal());
            preparedStatement.setString(3, proyecto.getNombre());
            preparedStatement.setString(4, proyecto.getDescripcion());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    proyecto.setIdProyecto(resultSet.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al agregar el proyecto", e);
            throw new DAOExcepcion("Error al agregar el proyecto: ", e);
        }
    }

    @Override
    public void actualizarProyecto(ProyectoDTO proyecto) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, proyecto.getNombre());
            preparedStatement.setString(2, proyecto.getDescripcion());
            preparedStatement.setInt(3, proyecto.getIdProyecto());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar proyecto", e);
            throw new DAOExcepcion("Error al actualizar el proyecto: ", e);
        }
    }

    @Override
    public ProyectoDTO buscarProyectoPorIdProyecto(int idProyecto) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_ID_PROYECTO)) {
            preparedStatement.setInt(1, idProyecto);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new ProyectoDTO(
                            resultSet.getInt("idProyecto"),
                            resultSet.getString("idOrganizacion"),
                            resultSet.getString("NumeroDePersonal"),
                            resultSet.getString("Nombre"),
                            resultSet.getString("Descripcion")
                    );
                } else {
                    logger.log(Level.WARNING, "No se encontro algun proyecto con el id: " + idProyecto);
                    throw new EntidadNoEncontradaExcepcion("No existe proyecto con el id: " + idProyecto);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar proyecto", e);
            throw new DAOExcepcion("Error al buscar Proyecto por idProyecto: ", e);
        }
    }

    @Override
    public List<ProyectoDTO> listarProyectos() throws DAOExcepcion {
        List<ProyectoDTO> listaProyectos = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                ProyectoDTO proyecto = new ProyectoDTO(
                        resultSet.getInt("idProyecto"),
                        resultSet.getString("idOrganizacion"),
                        resultSet.getString("NumeroDePersonal"),
                        resultSet.getString("Nombre"),
                        resultSet.getString("Descripcion")
                );
                listaProyectos.add(proyecto);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al listar los proyectos", e);
            throw new DAOExcepcion ("Error al listar los proyectos: ", e);
        }
        return listaProyectos;
    }
}