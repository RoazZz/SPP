package logica.dao;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.ProyectoDAOInterfaz;
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
    private static final Logger REGISTRADOR = Logger.getLogger(ProyectoDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO Proyecto (idOrganizacion, numeroDePersonal, Nombre," +
            " Descripcion) VALUES ( ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_ID_PROYECTO = "SELECT * FROM Proyecto WHERE idProyecto = ?";
    private static final String SQL_UPDATE = "UPDATE Proyecto SET Nombre = ?, Descripcion = ? WHERE idProyecto = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Proyecto";

    public ProyectoDAO() throws DAOExcepcion {
        try {
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de configuración", ioExcepcion);
            throw new DAOExcepcion("Error de configuracion", ioExcepcion);
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexion SQL en ProyectoDAO", sqlExcepcion);
            throw new DAOExcepcion("Error de base de datos", sqlExcepcion);
        }
    }

    @Override
    public void agregarProyecto(ProyectoDTO proyecto) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            sentenciaPreparada.setString(1, proyecto.getIdOrganizacion());
            sentenciaPreparada.setString(2, proyecto.getNumeroDePersonal());
            sentenciaPreparada.setString(3, proyecto.getNombre());
            sentenciaPreparada.setString(4, proyecto.getDescripcion());
            sentenciaPreparada.executeUpdate();

            try (ResultSet conjuntoResultado  = sentenciaPreparada.getGeneratedKeys()) {
                if (conjuntoResultado.next()) {
                    proyecto.setIdProyecto(conjuntoResultado.getInt(1));
                }
            }
            REGISTRADOR.log(Level.INFO, "Proyecto creado exitosamente: " + proyecto.getIdProyecto());
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al agregar el proyecto", sqlExcepcion);
            throw new DAOExcepcion("Error al agregar el proyecto: ", sqlExcepcion);
        }
    }

    @Override
    public void actualizarProyecto(ProyectoDTO proyecto) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE)) {
            sentenciaPreparada.setString(1, proyecto.getNombre());
            sentenciaPreparada.setString(2, proyecto.getDescripcion());
            sentenciaPreparada.setInt(3, proyecto.getIdProyecto());
            sentenciaPreparada.executeUpdate();
            REGISTRADOR.log(Level.INFO, "Proyecto actualizado exitosamente: " + proyecto.getIdProyecto());
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al actualizar proyecto", sqlExcepcion);
            throw new DAOExcepcion("Error al actualizar el proyecto: ", sqlExcepcion);
        }
    }

    @Override
    public ProyectoDTO buscarProyectoPorIdProyecto(int idProyecto) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_BUSCAR_POR_ID_PROYECTO)) {
            sentenciaPreparada.setInt(1, idProyecto);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return new ProyectoDTO(
                            conjuntoResultado.getInt("idProyecto"),
                            conjuntoResultado.getString("idOrganizacion"),
                            conjuntoResultado.getString("NumeroDePersonal"),
                            conjuntoResultado.getString("Nombre"),
                            conjuntoResultado.getString("Descripcion")
                    );
                } else {
                    REGISTRADOR.log(Level.WARNING, "No se encontro algun proyecto con el id " + idProyecto);
                    throw new EntidadNoEncontradaExcepcion("No existe proyecto con el id " + idProyecto);
                }
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al buscar proyecto", sqlExcepcion);
            throw new DAOExcepcion("Error al buscar Proyecto por idProyecto: ", sqlExcepcion);
        }
    }

    @Override
    public List<ProyectoDTO> listarProyectos() throws DAOExcepcion {
        List<ProyectoDTO> listaProyectos = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            while (conjuntoResultado.next()) {
                ProyectoDTO proyecto = new ProyectoDTO(
                        conjuntoResultado.getInt("idProyecto"),
                        conjuntoResultado.getString("idOrganizacion"),
                        conjuntoResultado.getString("NumeroDePersonal"),
                        conjuntoResultado.getString("Nombre"),
                        conjuntoResultado.getString("Descripcion")
                );
                listaProyectos.add(proyecto);
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al listar los proyectos", sqlExcepcion);
            throw new DAOExcepcion ("Error al listar los proyectos: ", sqlExcepcion);
        }
        return listaProyectos;
    }
}