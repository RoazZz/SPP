package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import logica.interfaces.CoordinadorAsignaProyectoDAOInterfaz;
import logica.dto.CoordinadorAsignaProyectoDTO;
import logica.enums.EstadoAsignacionProyecto;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoordinadorAsignaProyectoDAO implements CoordinadorAsignaProyectoDAOInterfaz {
    private final Connection conexion;
    private static final Logger REGISTRADOR = Logger.getLogger(CoordinadorAsignaProyectoDAO.class.getName());
    public static final String SQL_INSERT = "INSERT INTO Asigna (NumeroDePersonal, idProyecto, Estado)" +
            " VALUES (?, ?, ?)";
    public static final String SQL_UPDATE = "UPDATE Asigna SET Estado = ? WHERE idProyecto = ?";
    public static final String SQL_SELECT_BY_NUMERO_DE_PERSONAL = "SELECT * FROM Asigna WHERE NumeroDePersonal = ?";
    public static final String SQL_SELECT_BY_ID_PROYECTO = "SELECT * FROM Asigna WHERE idProyecto = ?";
    public static final String SQL_SELECT_ALL = "SELECT * FROM Asigna ";


    public CoordinadorAsignaProyectoDAO() throws DAOExcepcion {
        try{
        this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioExcepcion){
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de configuración", ioExcepcion);
            throw new DAOExcepcion("Error de configuracion", ioExcepcion);
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexion SQL en CoordinadorAsignaProyectoDAO", sqlExcepcion);
            throw new DAOExcepcion("Error de base de datos", sqlExcepcion);
        }
    }

   @Override
   public void insertarAsignacionDeProyecto(CoordinadorAsignaProyectoDTO coordinadorAsignaProyectoDTO) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT)) {
            sentenciaPreparada.setString(1, coordinadorAsignaProyectoDTO.getNumeroDePersonal());
            sentenciaPreparada.setInt(2, coordinadorAsignaProyectoDTO.getIdProyecto());
            sentenciaPreparada.setString(3, coordinadorAsignaProyectoDTO.getTipoEstado().name());
            sentenciaPreparada.executeUpdate();
            REGISTRADOR.log(Level.INFO, "Asignación de Proyecto creada exitosamente: " + coordinadorAsignaProyectoDTO.getNumeroDePersonal());
        } catch (SQLException sqlExcepcion){
            REGISTRADOR.log(Level.SEVERE, "Error al insertar Asignación de Proyecto", sqlExcepcion);
            throw new DAOExcepcion("Error al insertar la asignacion de proyecto: ", sqlExcepcion);
        }
    }

    @Override
    public void actualizarAsignacionDeProyecto(CoordinadorAsignaProyectoDTO coordinadorAsignaProyectoDTO) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE)) {
            sentenciaPreparada.setString(1, coordinadorAsignaProyectoDTO.getTipoEstado().name());
            sentenciaPreparada.setInt(2, coordinadorAsignaProyectoDTO.getIdProyecto());
            sentenciaPreparada.executeUpdate();
        } catch (SQLException ioExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al actualizar Asignación de Proyecto", ioExcepcion);
            throw new DAOExcepcion("Error al actualizar la asignacion del proyecto: ", ioExcepcion);
        }

    }

    @Override
    public List<CoordinadorAsignaProyectoDTO> obtenerAsignacionDeProyectoPorNumeroDePersonal(String numeroDePersonal) throws DAOExcepcion {
        List<CoordinadorAsignaProyectoDTO> listaAsignacionesProyecto = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_NUMERO_DE_PERSONAL)) {
            sentenciaPreparada.setString(1, numeroDePersonal); // ← aquí dentro, no en el try()
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                while (conjuntoResultado.next()) {
                    CoordinadorAsignaProyectoDTO asignacionProyecto = new CoordinadorAsignaProyectoDTO(
                            conjuntoResultado.getString("NumeroDePersonal"),
                            conjuntoResultado.getInt("idProyecto"),
                            EstadoAsignacionProyecto.valueOf(conjuntoResultado.getString("Estado").replace(" ", "_"))
                    );
                    listaAsignacionesProyecto.add(asignacionProyecto);
                }
            }
            return listaAsignacionesProyecto;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al listar las Asignaciones de Proyecto por numero de personal", sqlExcepcion);
            throw new DAOExcepcion("Error al obtener las asignaciones de proyecto por numero de personal: ", sqlExcepcion);
        }
    }

    @Override
    public List<CoordinadorAsignaProyectoDTO> obtenerAsignacionDeProyectoPorIdSeccion(int idSeccion) throws DAOExcepcion {
        List<CoordinadorAsignaProyectoDTO> listaAsignacionesProyecto = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_ID_PROYECTO);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            while (conjuntoResultado.next()) {
                CoordinadorAsignaProyectoDTO asignacionProyecto = new CoordinadorAsignaProyectoDTO(
                        conjuntoResultado.getString("NumeroDePersonal"),
                        conjuntoResultado.getInt("idProyecto"),
                        EstadoAsignacionProyecto.valueOf(conjuntoResultado.getString("Estado").replace(" ","_"))
                );
                listaAsignacionesProyecto.add(asignacionProyecto);
            }
            return listaAsignacionesProyecto;
        } catch (SQLException sqlExcepcion){
            REGISTRADOR.log(Level.SEVERE, "Error al listar las Asignaciones de Proyecto por id de proyecto", sqlExcepcion);
            throw new DAOExcepcion("Error al obtener las asignaciones de proyecto por id de Proyecto: ", sqlExcepcion);
        }
    }

    @Override
    public List<CoordinadorAsignaProyectoDTO> obtenerTodasLasAsignacionesDeProyecto() throws DAOExcepcion {
        List<CoordinadorAsignaProyectoDTO> listaAsignacionesProyecto = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            while (conjuntoResultado.next()) {
                CoordinadorAsignaProyectoDTO asignacionProyecto = new CoordinadorAsignaProyectoDTO(
                        conjuntoResultado.getString("NumeroDePersonal"),
                        conjuntoResultado.getInt("idProyecto"),
                        EstadoAsignacionProyecto.valueOf(conjuntoResultado.getString("Estado").replace(" ","_"))
                );
                listaAsignacionesProyecto.add(asignacionProyecto);
            }
            return listaAsignacionesProyecto;
        } catch (SQLException sqlExcepcion){
            REGISTRADOR.log(Level.SEVERE, "Error al listar las asignaciones de proyecto", sqlExcepcion);
            throw new DAOExcepcion("Error al obtener todas las asignaciones de proyecto: ", sqlExcepcion);
        }
    }

}
