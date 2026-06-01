package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.SolicitudProyectoDAOInterfaz;
import logica.dto.SolicitaProyectoDTO;
import logica.enums.TipoEstadoSolicitud;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SolicitaProyectoDAO implements SolicitudProyectoDAOInterfaz {
    public static final String SQL_INSERT = "INSERT INTO solicita(Matricula, idProyecto, EstadoProyecto, Periodo, Prioridad) " +
            "VALUES (?, ?, 'Pendiente', ?, ?)";
    public static final String SQL_UPDATE = "UPDATE solicita SET EstadoProyecto = ? WHERE idProyecto = ?";
    public static final String SQL_SELECT_BY_MATRICULA = "SELECT * FROM solicita WHERE Matricula = ? ORDER BY Prioridad";
    public static final String SQL_SELECT_BY_ID_PROYECTO = "SELECT * FROM solicita WHERE idProyecto = ?";
    public static final String SQL_SELECT_BY_PERIODO = "SELECT * FROM solicita WHERE Periodo = ? ORDER BY Matricula, Prioridad";
    public static final String SQL_SELECT_ALL = "SELECT * FROM solicita ORDER BY Matricula, Prioridad";
    public static final String SQL_SELECT_BY_PROFESOR =
            "SELECT s.Matricula, s.idProyecto, s.EstadoProyecto, s.Periodo, s.Prioridad " +
                    "FROM solicita s " +
                    "JOIN practicante pr ON s.Matricula = pr.Matricula " +
                    "JOIN profesor pf ON pr.idSeccion = pf.idSeccion " +
                    "WHERE pf.NumeroDePersonal = ? AND s.Periodo = ? " +
                    "ORDER BY s.Matricula, s.Prioridad";


    private Connection conexion;
    private static final Logger REGISTRADOR = Logger.getLogger(SolicitaProyectoDAO.class.getName());

    public SolicitaProyectoDAO() throws DAOExcepcion {
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioException){
            REGISTRADOR.log(Level.SEVERE, "Error de entrada/salida al configurar la conexión", ioException);
            throw new DAOExcepcion("Error al leer la configuración de la base de datos", ioException);
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error de SQL al intentar conectar", sqlException);
            throw new DAOExcepcion("Error de acceso a la base de datos", sqlException);
        }
    }

    @Override
    public SolicitaProyectoDTO insertarSolicitudProyecto(SolicitaProyectoDTO solicitaProyectoDTO) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT)) {
            sentenciaPreparada.setString(1, solicitaProyectoDTO.getMatricula());
            sentenciaPreparada.setInt(2, solicitaProyectoDTO.getIdProyecto());
            sentenciaPreparada.setString(3, solicitaProyectoDTO.getPeriodo());
            sentenciaPreparada.setInt(4, solicitaProyectoDTO.getPrioridad());
            sentenciaPreparada.executeUpdate();

            REGISTRADOR.log(Level.INFO, "Solicitud de proyecto insertada. Proyecto ID " + solicitaProyectoDTO.getIdProyecto());
            return solicitaProyectoDTO;
        } catch (SQLException sqlException){
            REGISTRADOR.log(Level.SEVERE, "Error SQL al insertar solicitud de proyecto", sqlException);
            throw new DAOExcepcion("Error al insertar la solicitud de proyecto", sqlException);
        }
    }

    @Override
    public boolean actualizarSolicitudProyecto(SolicitaProyectoDTO solicitaProyectoDTO) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE)) {
            sentenciaPreparada.setString(1, solicitaProyectoDTO.getTipoEstadoSolicitud().name());
            sentenciaPreparada.setInt(2, solicitaProyectoDTO.getIdProyecto());
            int filasAfectadas = sentenciaPreparada.executeUpdate();
            if (filasAfectadas > 0) {
                REGISTRADOR.log(Level.INFO, "Solicitud de proyecto actualizada exitosamente. Proyecto ID "
                        + solicitaProyectoDTO.getIdProyecto());
                return true;
            } else {
                REGISTRADOR.log(Level.WARNING, "No se encontró la solicitud de proyecto para actualizar. Proyecto ID "
                        + solicitaProyectoDTO.getIdProyecto());
                throw new EntidadNoEncontradaExcepcion("No se encontró la solicitud de proyecto para actualizar con ID "
                        + solicitaProyectoDTO.getIdProyecto());
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al actualizar solicitud de proyecto", sqlException);
            throw new DAOExcepcion("Error al actualizar la solicitud de proyecto", sqlException);
        }
    }

    @Override
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorMatricula(String matricula) throws DAOExcepcion {
        List<SolicitaProyectoDTO> listaSolicitudesProyecto = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_MATRICULA)) {
            sentenciaPreparada.setString(1, matricula);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                while (conjuntoResultado.next()) {
                    listaSolicitudesProyecto.add(construirSolicitud(conjuntoResultado));
                }
            }
            return listaSolicitudesProyecto;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al obtener solicitudes por matricula " + matricula, sqlException);
            throw new DAOExcepcion("Error al obtener las solicitudes de proyecto por matrícula", sqlException);
        }
    }

    @Override
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorIdProyecto(int idProyecto) throws DAOExcepcion {
        List<SolicitaProyectoDTO> listaSolicitudesProyecto = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_ID_PROYECTO)) {
            sentenciaPreparada.setInt(1, idProyecto);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                while (conjuntoResultado.next()) {
                    listaSolicitudesProyecto.add(construirSolicitud(conjuntoResultado));
                }
            }
            return listaSolicitudesProyecto;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al obtener solicitudes por ID proyecto: " + idProyecto, sqlException);
            throw new DAOExcepcion("Error al obtener las solicitudes de proyecto por ID de proyecto", sqlException);
        }
    }

    @Override
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorPeriodo(String periodo) throws DAOExcepcion {
        List<SolicitaProyectoDTO> listaSolicitudesProyecto = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_PERIODO)) {
            sentenciaPreparada.setString(1, periodo);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                while (conjuntoResultado.next()) {
                    listaSolicitudesProyecto.add(construirSolicitud(conjuntoResultado));
                }
            }
            return listaSolicitudesProyecto;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al obtener solicitudes por periodo: " + periodo, sqlException);
            throw new DAOExcepcion("Error al obtener las solicitudes de proyecto por período", sqlException);
        }
    }


    @Override
    public List<SolicitaProyectoDTO> obtenerTodasLasSolicitudesProyecto() throws DAOExcepcion {
        List<SolicitaProyectoDTO> listaSolicitudesProyecto = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            while (conjuntoResultado.next()) {
                listaSolicitudesProyecto.add(construirSolicitud(conjuntoResultado));
            }
            return listaSolicitudesProyecto;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al obtener todas las solicitudes", sqlException);
            throw new DAOExcepcion("Error al obtener todas las solicitudes de proyecto", sqlException);
        }
    }

    @Override
    public List<SolicitaProyectoDTO> obtenerSolicitudesProyectoPorProfesor(String numeroDePersonalProfesor, String periodo) throws DAOExcepcion {
        List<SolicitaProyectoDTO> listaSolicitudesProyecto = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_PROFESOR)) {
            sentenciaPreparada.setString(1, numeroDePersonalProfesor);
            sentenciaPreparada.setString(2, periodo);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                while (conjuntoResultado.next()) {
                    listaSolicitudesProyecto.add(construirSolicitud(conjuntoResultado));
                }
            }
            return listaSolicitudesProyecto;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al obtener solicitudes por profesor: " + numeroDePersonalProfesor, sqlException);
            throw new DAOExcepcion("Error al obtener las solicitudes de proyecto por profesor", sqlException);
        }
    }

    private List<SolicitaProyectoDTO> ejecutarConsultaConUnParametro(String consultaSql, String parametro, String mensajeError) throws DAOExcepcion {
        List<SolicitaProyectoDTO> listaSolicitudesProyecto = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(consultaSql)) {
            sentenciaPreparada.setString(1, parametro);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                while (conjuntoResultado.next()) {
                    listaSolicitudesProyecto.add(construirSolicitud(conjuntoResultado));
                }
            }
            return listaSolicitudesProyecto;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, mensajeError + ": " + parametro, sqlException);
            throw new DAOExcepcion(mensajeError, sqlException);
        }
    }

    private SolicitaProyectoDTO construirSolicitud(ResultSet conjuntoResultado) throws SQLException {
        return new SolicitaProyectoDTO(
                conjuntoResultado.getString("Matricula"),
                conjuntoResultado.getInt("idProyecto"),
                TipoEstadoSolicitud.valueOf(conjuntoResultado.getString("EstadoProyecto")),
                conjuntoResultado.getString("Periodo"),
                conjuntoResultado.getInt("Prioridad")
        );
    }

}
