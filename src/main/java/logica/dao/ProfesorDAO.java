package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoCreadaExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.ProfesorDAOInterfaz;
import logica.dto.ProfesorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
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
    private static final String SQL_INSERT = "INSERT INTO profesor(idUsuario, NumeroDePersonal, Turno) VALUES (?, ?, ?)";
    private static final String SQL_BUSCAR_POR_NUM_PERSONAL =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoP, usuario.apellidoM, " +
            "usuario.contrasenia, usuario.TipoUsuario, usuario.estado, " +
            "profesor.NumeroDePersonal, profesor.Turno " +
            "FROM usuario JOIN profesor ON usuario.idUsuario = profesor.idUsuario " +
            "WHERE profesor.NumeroDePersonal = ?";
    private static final String SQL_UPDATE = "UPDATE profesor SET Turno = ? WHERE NumeroDePersonal = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoP, usuario.apellidoM, " +
            "usuario.contrasenia, usuario.TipoUsuario, usuario.estado, " +
            "profesor.NumeroDePersonal, profesor.Turno " +
            "FROM usuario JOIN profesor ON usuario.idUsuario = profesor.idUsuario";

    private Connection conexion;
    private static final Logger logger = Logger.getLogger(ProfesorDAO.class.getName());

    public ProfesorDAO() throws DAOExcepcion{
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error al leer archivo de cofniguración", e);
            throw new DAOExcepcion("Error de configuracion", e);
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error de conexion SQL en ProfesorDAO", e);
            throw new DAOExcepcion("Error de base de datos", e);
        }
    }

    @Override
    public void agregarProfesor(ProfesorDTO profesor) throws DAOExcepcion {
        UsuarioDAO usuarioDAO = new UsuarioDAO(this.conexion);

        try {
            conexion.setAutoCommit(false);
            usuarioDAO.agregarUsuario(profesor);
            int idGenerado = profesor.getIdUsuario();

            if (idGenerado > 0) {
                try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)) {
                    preparedStatement.setInt(1, idGenerado);
                    preparedStatement.setString(2, profesor.getNumeroDePersonal());
                    preparedStatement.setString(3, profesor.getTurno().name());
                    preparedStatement.executeUpdate();
                }
                conexion.commit();
                logger.log(Level.INFO, "Profesor agregado exitosamente: " + profesor.getNumeroDePersonal());
            } else {
                logger.log(Level.WARNING, "Usuario base no generado para profesor");
                throw new EntidadNoCreadaExcepcion("Usuario base no creado correctamente");
            }
        } catch (SQLException e) {
            try {
                if (conexion != null){
                    conexion.rollback();
                }
            } catch (SQLException exRollback) {
                logger.log(Level.SEVERE, "Error al hacer rollback", exRollback);
            }
            logger.log(Level.SEVERE, "Error SQL al agregar profesor", e);
            throw new DAOExcepcion("Error al agregar profesor", e);
        } catch (Exception e) {
            try {
                if (conexion != null) {
                    conexion.rollback();
                }
            } catch (SQLException exRollback) {
                logger.log(Level.SEVERE, "Error al hacer rollback tras error inesperado", exRollback);
            }
            logger.log(Level.SEVERE, "Error no esperado en ProfesorDAO", e);
            throw new DAOExcepcion("Ocurrió un error inesperado al registrar el profesor", e);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "No se pudo resetear autocommit", e);
            }
        }
    }

    @Override
    public void actualizarProfesor(ProfesorDTO profesor) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, profesor.getTurno().name());
            preparedStatement.setString(2, profesor.getNumeroDePersonal());
            preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Profesor actualizado correctamente: " + profesor.getNumeroDePersonal());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al actualizar profesor", e);
            throw new DAOExcepcion("Error al actualizar el profesor", e);
        }
    }

    @Override
    public ProfesorDTO buscarProfesorPorNumPersonal(String numPersonal) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_NUM_PERSONAL)) {
            preparedStatement.setString(1, numPersonal);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new ProfesorDTO(
                            resultSet.getInt("idUsuario"),
                            resultSet.getString("nombre"),
                            resultSet.getString("apellidoP"),
                            resultSet.getString("apellidoM"),
                            resultSet.getString("contrasenia"),
                            TipoEstado.valueOf(resultSet.getString("estado")),
                            TipoDeUsuario.valueOf(resultSet.getString("TipoUsuario")),
                            resultSet.getString("NumeroDePersonal"),
                            TipoTurno.valueOf(resultSet.getString("Turno"))
                    );
                }else{
                    logger.log(Level.WARNING, "No se encontró profesor con numero de personal: " + numPersonal);
                    throw new EntidadNoEncontradaExcepcion("Profesor no encontrado con numero de personal: " + numPersonal);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar profesor por numero personal", e);
            throw new DAOExcepcion("Error al buscar el profesor", e);
        }
    }

    @Override
    public List<ProfesorDTO> listarProfesores() throws DAOExcepcion {
        List<ProfesorDTO> listaProfesor = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);  ResultSet resultSet = preparedStatement.executeQuery();) {
            while (resultSet.next()) {
                ProfesorDTO profesor = new ProfesorDTO(
                        resultSet.getInt("idUsuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellidoP"),
                        resultSet.getString("apellidoM"),
                        resultSet.getString("contrasenia"),
                        TipoEstado.valueOf(resultSet.getString("estado")),
                        TipoDeUsuario.valueOf(resultSet.getString("tipoUsuario")),
                        resultSet.getString("NumeroDePersonal"),
                        TipoTurno.valueOf(resultSet.getString("Turno"))
                );
                listaProfesor.add(profesor);
            }
            return listaProfesor;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al listar profesores", e);
            throw new DAOExcepcion("Error al listar los profesores", e);
        }
    }

}
