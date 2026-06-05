package logica.dao;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.OrganizacionVinculadaDAOInterfaz;
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
    private static final Logger REGISTRADOR = Logger.getLogger(OrganizacionVinculadaDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO OrganizacionVinculada (idOrganizacion, Nombre, Direccion) " +
            "VALUES ( ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID_ORGANIZACIONVINCULADA = "SELECT * FROM OrganizacionVinculada " +
            "WHERE idOrganizacion = ?";
    private static final String SQL_UPDATE = "UPDATE OrganizacionVinculada SET Nombre = ?, Direccion = ?" +
            " WHERE idOrganizacion = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM OrganizacionVinculada";

    public OrganizacionVinculadaDAO() throws DAOExcepcion {
        try{
        this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioExcepcion){
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de configuración", ioExcepcion);
            throw new DAOExcepcion("Error de configuracion", ioExcepcion);
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexion SQL en OrganizaciónVinculadaDAO", sqlExcepcion);
            throw new DAOExcepcion("Error de base de datos", sqlExcepcion);
        }
    }

    @Override
    public boolean agregarOrganizacionVinculada(OrganizacionVinculadaDTO organizacionVinculada) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT)){
            sentenciaPreparada.setString(1, organizacionVinculada.getIdOrganizacion());
            sentenciaPreparada.setString(2, organizacionVinculada.getNombre());
            sentenciaPreparada.setString(3, organizacionVinculada.getDireccion());
            sentenciaPreparada.executeUpdate();
            REGISTRADOR.log(Level.INFO, "Organización Vinculada creada exitosamente: " + organizacionVinculada.getIdOrganizacion());
            return true;
        } catch (SQLException sqlExcepcion){
            REGISTRADOR.log(Level.SEVERE, "Error al agregar Organizacion Vinculada", sqlExcepcion);
            throw new DAOExcepcion("Error al agregar la Organizacion Vinculada: ", sqlExcepcion);
        }
    }

    @Override
    public boolean actualizarOrganizacionVinculada(OrganizacionVinculadaDTO organizacionVinculada) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE)) {
            sentenciaPreparada.setString(1, organizacionVinculada.getNombre());
            sentenciaPreparada.setString(2, organizacionVinculada.getDireccion());
            sentenciaPreparada.setString(3, organizacionVinculada.getIdOrganizacion());
            sentenciaPreparada.executeUpdate();
            REGISTRADOR.log(Level.INFO, "Organización Vinculada actualizada exitosamente: " + organizacionVinculada.getIdOrganizacion());
            return true;
        } catch (SQLException sqlExcepcion){
            REGISTRADOR.log(Level.SEVERE, "Error al actualizar Organizacion Vinculada", sqlExcepcion);
            throw new DAOExcepcion("Error al actualizar a la Organizacion Vinculada: ", sqlExcepcion);
        }
    }

    @Override
    public OrganizacionVinculadaDTO buscarOrganizacionVinculadaPorIdProyecto(String idOrganizacion) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_BUSCAR_POR_ID_ORGANIZACIONVINCULADA)){
            sentenciaPreparada.setString(1, idOrganizacion);
            ResultSet conjuntoResultado = sentenciaPreparada.executeQuery();
            if (conjuntoResultado.next()){
                String idOrganizacionVinculada = conjuntoResultado.getString("idOrganizacion");
                String nombre = conjuntoResultado.getString("Nombre");
                String direccion = conjuntoResultado.getString("Direccion");
                return new OrganizacionVinculadaDTO(idOrganizacionVinculada, nombre, direccion);
            } else{
                REGISTRADOR.log(Level.WARNING, "No se encontro alguna organizacion vinculada con el id " + idOrganizacion);
                throw new EntidadNoEncontradaExcepcion("No existe organizacion vinculada con el id " + idOrganizacion);
            }
        } catch (SQLException sqlExcepcion){
            REGISTRADOR.log(Level.SEVERE, "Error al buscar a la Organizacion Vinculada", sqlExcepcion);
            throw new DAOExcepcion("Error al buscar a la Organizacion Vinculada: ", sqlExcepcion);
        }
    }

    @Override
    public List<OrganizacionVinculadaDTO> listarOrganizacionesVinculadas() throws DAOExcepcion {
        List<OrganizacionVinculadaDTO> listaOrganizacionVinculada = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery();) {
            while (conjuntoResultado.next()) {
                OrganizacionVinculadaDTO organizacion = new OrganizacionVinculadaDTO(
                        conjuntoResultado.getString("idOrganizacion"),
                        conjuntoResultado.getString("Nombre"),
                        conjuntoResultado.getString("Direccion")
                );
                listaOrganizacionVinculada.add(organizacion);
            }
            return listaOrganizacionVinculada;
        } catch (SQLException sqlExcepcion){
            REGISTRADOR.log(Level.SEVERE, "Error al listar las Organizacion Vinculadas", sqlExcepcion);
            throw new DAOExcepcion("Error al listar a las Organizaciones Vinculadas: ", sqlExcepcion);
        }
    }
}