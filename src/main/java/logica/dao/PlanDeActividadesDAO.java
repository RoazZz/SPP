package logica.dao;

import accesodatos.ConexionBD;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.PlanDeActivadesDAOInterfaz;
import logica.dto.PlanDeActividadesDTO;
import excepciones.DAOExcepcion;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PlanDeActividadesDAO implements PlanDeActivadesDAOInterfaz {
    public static final String SQL_INSERT = "INSERT INTO plandeactividades(Matricula, idProyecto, Descripcion) VALUES (?, ?, ?)";
    public static final String SQL_UPDATE = "UPDATE plandeactividades SET Matricula = ?, idProyecto = ?, Descripcion = ? WHERE idPlanActividades = ?";
    public static final String SQL_SELECT_BY_MATRICULA = "SELECT * FROM plandeactividades WHERE Matricula = ?";
    public static final String SQL_SELECT_ALL = "SELECT * FROM plandeactividades";
    public static final String SQL_SELECT_BY_ID = "SELECT * FROM plandeactividades WHERE idPlanActividades = ?";

    private Connection conexion;
    private static final Logger REGISTRADOR = Logger.getLogger(PlanDeActividadesDAO.class.getName());

    public PlanDeActividadesDAO() throws DAOExcepcion {
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioExcepcion){
            REGISTRADOR.log(Level.SEVERE, "Error de entrada/salida al configurar la conexión", ioExcepcion);
            throw new DAOExcepcion("Error al leer la configuración de la base de datos", ioExcepcion);
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error de SQL al intentar conectar", sqlExcepcion);
            throw new DAOExcepcion("Error de acceso a la base de datos", sqlExcepcion);
        }
    }

    @Override
    public PlanDeActividadesDTO agregarPlanDeActividades(PlanDeActividadesDTO planDeActividadesDTO) throws DAOExcepcion{
        try(PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)){
            sentenciaPreparada.setString(1, planDeActividadesDTO.getMatricula());
            sentenciaPreparada.setInt(2, planDeActividadesDTO.getIdProyecto());
            sentenciaPreparada.setString(3, planDeActividadesDTO.getDescripcion());
            sentenciaPreparada.executeUpdate();

            try (ResultSet conjuntoResultado = sentenciaPreparada.getGeneratedKeys()) {
                if (conjuntoResultado.next()) {
                    planDeActividadesDTO.setIdPlanActividades(conjuntoResultado.getInt(1));
                }
            }
            REGISTRADOR.info("Plan de actividades agregado exitosamente");
            return planDeActividadesDTO;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al agregar plan de actividades", sqlExcepcion);
            throw new DAOExcepcion("Error al agregar el plan de actividades: ", sqlExcepcion);
        }
    }

    @Override
    public boolean actualizarPlanDeActividades(PlanDeActividadesDTO planDeActividadesDTO) throws DAOExcepcion {
        try(PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE)){
            sentenciaPreparada.setString(1, planDeActividadesDTO.getMatricula());
            sentenciaPreparada.setInt(2, planDeActividadesDTO.getIdProyecto());
            sentenciaPreparada.setString(3, planDeActividadesDTO.getDescripcion());
            sentenciaPreparada.setInt(4, planDeActividadesDTO.getIdPlanActividades());

            int filasAfectadas = sentenciaPreparada.executeUpdate();
            if (filasAfectadas > 0) {
                REGISTRADOR.info("Plan de actividades actualizado exitosamente, ID" + planDeActividadesDTO.getIdPlanActividades());
                return true;
            } else {
                REGISTRADOR.log(Level.WARNING, "No se encontró el plan de actividades para actualizar, ID " + planDeActividadesDTO.getIdPlanActividades());
                throw new EntidadNoEncontradaExcepcion("No se encontró el plan de actividades con el ID " + planDeActividadesDTO.getIdPlanActividades());
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al actualizar plan ID: " + planDeActividadesDTO.getIdPlanActividades(), sqlExcepcion);
            throw new DAOExcepcion("Error al modificar los datos del plan", sqlExcepcion);
        }
    }

    @Override
    public List<PlanDeActividadesDTO> obtenerPlanesDeActividadesPorMatricula(String matricula) throws DAOExcepcion{
        List<PlanDeActividadesDTO> planDeActividadesDTO = new ArrayList<>();
        try(PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_MATRICULA)){
            sentenciaPreparada.setString(1, matricula);
            try(ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()){
                while (conjuntoResultado.next()){
                    PlanDeActividadesDTO plan = new PlanDeActividadesDTO(
                            conjuntoResultado.getInt("idPlanActividades"),
                            conjuntoResultado.getString("Matricula"),
                            conjuntoResultado.getInt("idProyecto"),
                            conjuntoResultado.getString("Descripcion")
                    );
                    planDeActividadesDTO.add(plan);
                }
                return planDeActividadesDTO;
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al buscar planes por matrícula: " + matricula, sqlExcepcion);
            throw new DAOExcepcion("Error al consultar planes por matrícula", sqlExcepcion);        }
    }

    @Override
    public List<PlanDeActividadesDTO> obtenerTodosLosPlanesDeActividades() throws DAOExcepcion{
        List<PlanDeActividadesDTO> planDeActividadesDTO = new ArrayList<>();
        try(PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL)){
            try(ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()){
                while (conjuntoResultado.next()){
                    PlanDeActividadesDTO plan = new PlanDeActividadesDTO(
                            conjuntoResultado.getInt("idPlanActividades"),
                            conjuntoResultado.getString("Matricula"),
                            conjuntoResultado.getInt("idProyecto"),
                            conjuntoResultado.getString("Descripcion")
                    );
                    planDeActividadesDTO.add(plan);
                }
                return planDeActividadesDTO;
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al obtener todos los planes", sqlExcepcion);
            throw new DAOExcepcion("Error al obtener la lista completa de planes", sqlExcepcion);
        }
    }

    public PlanDeActividadesDTO obtenerPlanDeActividadesPorId(int idPlanDeActividades) throws DAOExcepcion{
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_ID)) {
            sentenciaPreparada.setInt(1, idPlanDeActividades);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return new PlanDeActividadesDTO(
                            conjuntoResultado.getInt("idPlanActividades"),
                            conjuntoResultado.getString("Matricula"),
                            conjuntoResultado.getInt("idProyecto"),
                            conjuntoResultado.getString("Descripcion")
                    );
                } else{
                    REGISTRADOR.log(Level.WARNING, "No se encontró Plan de Actividades con ID: " + idPlanDeActividades);
                    throw new EntidadNoEncontradaExcepcion("No se encontró el Plan de Actividades con el ID proporcionado");
                }
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al buscar plan por ID: " + idPlanDeActividades, sqlExcepcion);
            throw new DAOExcepcion("Error al buscar el plan específico", sqlExcepcion);
        }
    }
}
