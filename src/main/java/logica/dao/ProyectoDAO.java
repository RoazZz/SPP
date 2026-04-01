package logica.dao;

import interfaces.ProyectoDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.ProyectoDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProyectoDAO extends ConexionBD implements ProyectoDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO Proyecto (idOrganizacion, numeroDePersonal, Nombre, Descripcion) VALUES ( ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID_PROYECTO = "SELECT * FROM Proyecto WHERE idProyecto = ?";
    private static final String SQL_UPDATE = "UPDATE Proyecto SET Nombre = ?, Descripcion = ? WHERE idProyecto = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Proyecto";

    public ProyectoDAO() {
        super();
    }


    @Override
    public void agregarProyecto(ProyectoDTO proyecto) throws Exception {
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
            throw new Exception("Error al agregar el proyecto: " + e.getMessage());
        }
    }


    @Override
    public void actualizarProyecto(ProyectoDTO proyecto) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, proyecto.getNombre());
            preparedStatement.setString(2, proyecto.getDescripcion());
            preparedStatement.setInt(3, proyecto.getIdProyecto());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al actualizar el proyecto: " + e.getMessage());
        }
    }

    @Override
    public ProyectoDTO buscarProyectoPorIdProyecto(int idProyecto) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_ID_PROYECTO)) {
            preparedStatement.setInt(1, idProyecto);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new ProyectoDTO(
                            resultSet.getInt("idUsuario"),
                            resultSet.getString("idOrganizacion"),
                            resultSet.getString("idNumeroDePersonal"),
                            resultSet.getString("Nombre"),
                            resultSet.getString("Descripcion")
                    );
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar Proyecto por idProyecto: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<ProyectoDTO> listarProyectos() throws Exception {
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
            throw new Exception("Error al listar los proyectos: " + e.getMessage());
        }
        return listaProyectos;
    }
}