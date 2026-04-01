package logica.dao;

import interfaces.OrganizacionVinculadaDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.OrganizacionVinculadaDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrganizacionVinculadaDAO extends ConexionBD implements OrganizacionVinculadaDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO OrganizacionVinculada (idOrganizacion, Nombre, Direccion) VALUES ( ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID_ORGANIZACIONVINCULADA = "SELECT * FROM OrganizacionVinculada WHERE idOrganizacion = ?";
    private static final String SQL_UPDATE = "UPDATE OrganizacionVinculada SET Nombre = ?, Direccion = ? WHERE idOrganizacion = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM OrganizacionVinculada";

    public OrganizacionVinculadaDAO() {
        super();
    }

    @Override
    public void agregarOrganizacionVinculada(OrganizacionVinculadaDTO organizacionVinculada) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)){
            preparedStatement.setString(1, organizacionVinculada.getidOrganizacion());
            preparedStatement.setString(2, organizacionVinculada.getNombre());
            preparedStatement.setString(3, organizacionVinculada.getDireccion());
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            throw new Exception("Error al agregar la Organizacion Vinculada: " + e.getMessage());
        }
    }

    @Override
    public void actualizarOrganizacionVinculada(OrganizacionVinculadaDTO organizacionVinculada) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, organizacionVinculada.getidOrganizacion());
            preparedStatement.setString(2, organizacionVinculada.getNombre());
            preparedStatement.setString(3, organizacionVinculada.getDireccion());
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            throw new Exception("Error al actualizar a la Organizacion Vinculada: " + e.getMessage());
        }
    }

    @Override
    public OrganizacionVinculadaDTO buscarOrganizacionVinculadaPorIdProyecto(String idOrganizacion) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_ID_ORGANIZACIONVINCULADA)){
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                String idOrganizacionVinculada = resultSet.getString("idOrganizacion");
                String nombre = resultSet.getString("Nombre");
                String direccion = resultSet.getString("Direccion");
                return new OrganizacionVinculadaDTO(idOrganizacionVinculada, nombre, direccion);
            }else{
                return null;
            }
        } catch (SQLException e){
            throw new Exception("Error al buscar a la Organizacion Vinculada: " + e.getMessage());
        }
    }

    @Override
    public List<OrganizacionVinculadaDTO> listarOrganizacionesVinculadas() throws Exception {
        List<OrganizacionVinculadaDTO> listaOrganizacionVinculada = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL); ResultSet resultSet = preparedStatement.executeQuery();) {
            while (resultSet.next()) {
                OrganizacionVinculadaDTO organizacion = new OrganizacionVinculadaDTO(
                        resultSet.getString("idOrganizacion"),
                        resultSet.getString("Nombre"),
                        resultSet.getString("Descripcion")
                );
                listaOrganizacionVinculada.add(organizacion);
            }
            return listaOrganizacionVinculada;
        } catch (SQLException e){
            throw new Exception("Error al listar a las Organizaciones Vinculadas: " + e.getMessage());
        }
    }
}