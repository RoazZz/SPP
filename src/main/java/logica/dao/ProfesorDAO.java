package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoCreadaExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.ProfesorDAOInterfaz;
import logica.dto.BuzonDTO;
import logica.dto.ProfesorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstadoUsuario;
import logica.enums.TipoTurno;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfesorDAO implements ProfesorDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO profesor(idUsuario, NumeroDePersonal, Turno, idSeccion)" +
            " VALUES (?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_NUM_PERSONAL =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoP, usuario.apellidoM, " +
            "usuario.contrasenia, usuario.TipoUsuario, usuario.estado, " +
            "profesor.NumeroDePersonal, profesor.Turno , profesor.IdSeccion " +
            "FROM usuario JOIN profesor ON usuario.idUsuario = profesor.idUsuario " +
            "WHERE profesor.NumeroDePersonal = ?";
    private static final String SQL_UPDATE = "UPDATE profesor SET Turno = ? WHERE NumeroDePersonal = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoP, usuario.apellidoM, " +
            "usuario.contrasenia, usuario.TipoUsuario, usuario.estado, " +
            "profesor.NumeroDePersonal, profesor.Turno, profesor.IdSeccion " +
            "FROM usuario JOIN profesor ON usuario.idUsuario = profesor.idUsuario";
    private static final String SQL_EXISTE_NUMERO_PERSONAL = "SELECT COUNT(*) FROM Profesor WHERE NumeroDePersonal = ?" +
            " AND idUsuario != ?";

    private Connection conexion;
    private static final Logger REGISTRADOR = Logger.getLogger(ProfesorDAO.class.getName());

    public ProfesorDAO() throws DAOExcepcion{
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioExcepcion){
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de cofniguración", ioExcepcion);
            throw new DAOExcepcion("Error de configuracion", ioExcepcion);
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexion SQL en ProfesorDAO", sqlExcepcion);
            throw new DAOExcepcion("Error de base de datos", sqlExcepcion);
        }
    }

    @Override
    public ProfesorDTO agregarProfesor(ProfesorDTO profesor) throws DAOExcepcion {
        UsuarioDAO usuarioDAO = new UsuarioDAO(this.conexion);
        BuzonDAO buzonDAO = new BuzonDAO(this.conexion);
        try {
            conexion.setAutoCommit(false);
            usuarioDAO.agregarUsuario(profesor);
            int idGenerado = profesor.getIdUsuario();

            if (idGenerado > 0) {
                try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT)) {
                    sentenciaPreparada.setInt(1, idGenerado);
                    sentenciaPreparada.setString(2, profesor.getNumeroDePersonal());
                    sentenciaPreparada.setString(3, profesor.getTurno().name());
                    sentenciaPreparada.setInt(4, profesor.getIdSeccion());
                    sentenciaPreparada.executeUpdate();
                }
                BuzonDTO buzonDTO = new BuzonDTO(idGenerado);
                buzonDAO.agregarBuzon(buzonDTO);
                conexion.commit();
                REGISTRADOR.log(Level.INFO, "Profesor agregado exitosamente: " + profesor.getNumeroDePersonal());
                return profesor;
            } else {
                REGISTRADOR.log(Level.WARNING, "Usuario base no generado para profesor");
                throw new EntidadNoCreadaExcepcion("Usuario base no creado correctamente");
            }
        } catch (SQLException sqlExcepcion) {
            try {
                if (conexion != null){
                    conexion.rollback();
                }
            } catch (SQLException sqlExcepcionRollback) {
                REGISTRADOR.log(Level.SEVERE, "Error al hacer rollback", sqlExcepcionRollback);
            }
            REGISTRADOR.log(Level.SEVERE, "Error SQL al agregar profesor", sqlExcepcion);
            throw new DAOExcepcion("Error al agregar profesor", sqlExcepcion);
        } catch (EntidadNoEncontradaExcepcion entidadNoEncontradaExcepcion) {
            try {
                if (conexion != null) {
                    conexion.rollback();
                }
            } catch (SQLException sqlExcepcionRollback) {
                REGISTRADOR.log(Level.SEVERE, "Error al hacer rollback tras error inesperado", sqlExcepcionRollback);
            }
            REGISTRADOR.log(Level.SEVERE, "Error no esperado en ProfesorDAO", entidadNoEncontradaExcepcion);
            throw new DAOExcepcion("Ocurrió un error inesperado al registrar el profesor", entidadNoEncontradaExcepcion);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException sqlExcepcion) {
                REGISTRADOR.log(Level.WARNING, "No se pudo resetear autocommit", sqlExcepcion);
            }
        }
    }

    @Override
    public boolean actualizarProfesor(ProfesorDTO profesor) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE)) {
            sentenciaPreparada.setString(1, profesor.getTurno().name());
            sentenciaPreparada.setString(2, profesor.getNumeroDePersonal());
            int filasAfectadas = sentenciaPreparada.executeUpdate();
            if (filasAfectadas > 0){
                REGISTRADOR.log(Level.INFO, "Profesor actualizado en tabla profesor con numero personal " + profesor.getNumeroDePersonal());
                return true;
            } else {
                REGISTRADOR.log(Level.WARNING, "No se encontró profesor para actualizar con numero de personal " + profesor.getNumeroDePersonal());
                throw new EntidadNoEncontradaExcepcion("Profesor no encontrado para actualizar con numero de personal " + profesor.getNumeroDePersonal());
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al actualizar profesor", sqlExcepcion);
            throw new DAOExcepcion("Error al actualizar el profesor", sqlExcepcion);
        }
    }

    @Override
    public ProfesorDTO buscarProfesorPorNumPersonal(String numPersonal) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_BUSCAR_POR_NUM_PERSONAL)) {
            sentenciaPreparada.setString(1, numPersonal);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return new ProfesorDTO(
                            conjuntoResultado.getInt("idUsuario"),
                            conjuntoResultado.getString("nombre"),
                            conjuntoResultado.getString("apellidoP"),
                            conjuntoResultado.getString("apellidoM"),
                            conjuntoResultado.getString("contrasenia"),
                            TipoEstadoUsuario.valueOf(conjuntoResultado.getString("estado")),
                            TipoDeUsuario.valueOf(conjuntoResultado.getString("TipoUsuario")),
                            conjuntoResultado.getString("NumeroDePersonal"),
                            TipoTurno.valueOf(conjuntoResultado.getString("Turno")),
                            conjuntoResultado.getInt("idSeccion")
                    );
                } else{
                    REGISTRADOR.log(Level.WARNING, "No se encontró profesor con numero de personal " + numPersonal);
                    throw new EntidadNoEncontradaExcepcion("Profesor no encontrado con numero de personal " + numPersonal);
                }
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al buscar profesor por numero personal", sqlExcepcion);
            throw new DAOExcepcion("Error al buscar el profesor", sqlExcepcion);
        }
    }

    @Override
    public List<ProfesorDTO> listarProfesores() throws DAOExcepcion {
        List<ProfesorDTO> listaProfesor = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery();) {
            while (conjuntoResultado.next()) {
                ProfesorDTO profesor = new ProfesorDTO(
                        conjuntoResultado.getInt("idUsuario"),
                        conjuntoResultado.getString("nombre"),
                        conjuntoResultado.getString("apellidoP"),
                        conjuntoResultado.getString("apellidoM"),
                        conjuntoResultado.getString("contrasenia"),
                        TipoEstadoUsuario.valueOf(conjuntoResultado.getString("estado")),
                        TipoDeUsuario.valueOf(conjuntoResultado.getString("tipoUsuario")),
                        conjuntoResultado.getString("NumeroDePersonal"),
                        TipoTurno.valueOf(conjuntoResultado.getString("Turno")),
                        conjuntoResultado.getInt("idSeccion")
                );
                listaProfesor.add(profesor);
            }
            return listaProfesor;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al listar profesores", sqlExcepcion);
            throw new DAOExcepcion("Error al listar los profesores", sqlExcepcion);
        }
    }

    @Override
    public boolean existeProfesorConNumeroPersonal(String numeroPersonal, int idExcluir) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_EXISTE_NUMERO_PERSONAL)) {
            sentenciaPreparada.setString(1, numeroPersonal);
            sentenciaPreparada.setInt(2, idExcluir);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return conjuntoResultado.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al verificar número de personal duplicado en profesor", sqlException);
            throw new DAOExcepcion("Error al verificar si existe el número de personal", sqlException);
        }
    }

}
