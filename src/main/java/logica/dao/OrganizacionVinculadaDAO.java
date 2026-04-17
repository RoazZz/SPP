package logica.dao;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.OrganizacionVinculadaDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.OrganizacionVinculadaDTO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrganizacionVinculadaDAO implements OrganizacionVinculadaDAOInterfaz {
    private final Connection conexion;
    private static final Logger logger = Logger.getLogger(OrganizacionVinculadaDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO OrganizacionVinculada (idOrganizacion, Nombre, Direccion) VALUES ( ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID_ORGANIZACIONVINCULADA = "SELECT * FROM OrganizacionVinculada WHERE idOrganizacion = ?";
    private static final String SQL_UPDATE = "UPDATE OrganizacionVinculada SET Nombre = ?, Direccion = ? WHERE idOrganizacion = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM OrganizacionVinculada";

    public OrganizacionVinculadaDAO() throws DAOExcepcion {
        try{
        this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error al leer archivo de configuración", e);
            throw new DAOExcepcion("Error de configuracion", e);
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error de conexion SQL en OrganizaciónVinculadaDAO", e);
            throw new DAOExcepcion("Error de base de datos", e);
        }
    }

    @Override
    public void agregarOrganizacionVinculada(OrganizacionVinculadaDTO organizacionVinculada) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)){
            preparedStatement.setString(1, organizacionVinculada.getidOrganizacion());
            preparedStatement.setString(2, organizacionVinculada.getNombre());
            preparedStatement.setString(3, organizacionVinculada.getDireccion());
            preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Organización Vinculada creada exitosamente: " + organizacionVinculada.getidOrganizacion());
        } catch (SQLException e){
            logger.log(Level.SEVERE, "Error al agregar Organizacion Vinculada", e);
            throw new DAOExcepcion("Error al agregar la Organizacion Vinculada: ", e);
        }
    }

    @Override
    public void actualizarOrganizacionVinculada(OrganizacionVinculadaDTO organizacionVinculada) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, organizacionVinculada.getNombre());
            preparedStatement.setString(2, organizacionVinculada.getDireccion());
            preparedStatement.setString(3, organizacionVinculada.getidOrganizacion());
            preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Organización Vinculada actualizada exitosamente: " + organizacionVinculada.getidOrganizacion());
        } catch (SQLException e){
            logger.log(Level.SEVERE, "Error al actualizar Organizacion Vinculada", e);
            throw new DAOExcepcion("Error al actualizar a la Organizacion Vinculada: ", e);
        }
    }

    @Override
    public OrganizacionVinculadaDTO buscarOrganizacionVinculadaPorIdProyecto(String idOrganizacion) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_ID_ORGANIZACIONVINCULADA)){
            preparedStatement.setString(1, idOrganizacion);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                String idOrganizacionVinculada = resultSet.getString("idOrganizacion");
                String nombre = resultSet.getString("Nombre");
                String direccion = resultSet.getString("Direccion");
                return new OrganizacionVinculadaDTO(idOrganizacionVinculada, nombre, direccion);
            }else{
                logger.log(Level.WARNING, "No se encontro alguna organizacion vinculada con el id: " + idOrganizacion);
                throw new EntidadNoEncontradaExcepcion("No existe organizacion vinculada con el id: " + idOrganizacion);
            }
        } catch (SQLException e){
            logger.log(Level.SEVERE, "Error al buscar a la Organizacion Vinculada", e);
            throw new DAOExcepcion("Error al buscar a la Organizacion Vinculada: ", e);
        }
    }

    @Override
    public List<OrganizacionVinculadaDTO> listarOrganizacionesVinculadas() throws DAOExcepcion {
        List<OrganizacionVinculadaDTO> listaOrganizacionVinculada = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL); ResultSet resultSet = preparedStatement.executeQuery();) {
            while (resultSet.next()) {
                OrganizacionVinculadaDTO organizacion = new OrganizacionVinculadaDTO(
                        resultSet.getString("idOrganizacion"),
                        resultSet.getString("Nombre"),
                        resultSet.getString("Direccion")
                );
                listaOrganizacionVinculada.add(organizacion);
            }
            return listaOrganizacionVinculada;
        } catch (SQLException e){
            logger.log(Level.SEVERE, "Error al listar las Organizacion Vinculadas", e);
            throw new DAOExcepcion("Error al listar a las Organizaciones Vinculadas: ", e);
        }
    }
}