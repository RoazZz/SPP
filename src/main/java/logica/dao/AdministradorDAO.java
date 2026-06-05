package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoCreadaExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.AdministradorDAOInterfaz;
import logica.dto.AdministradorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstadoUsuario;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdministradorDAO implements AdministradorDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO administrador (idUsuario) VALUES (?)";
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoP, usuario.apellidoM, " +
                    "usuario.contrasenia, usuario.TipoUsuario, usuario.estado, " +
                    "administrador.idAdministrador" +
                    " FROM usuario JOIN administrador ON usuario.idUsuario = administrador.idUsuario " +
                    "WHERE administrador.idAdministrador = ?";
    private static final String SQL_BUSCAR_POR_NOMBRE =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoP, usuario.apellidoM, " +
                    "usuario.contrasenia, usuario.TipoUsuario, usuario.estado, " +
                    "administrador.idAdministrador" +
                    " FROM usuario JOIN administrador ON usuario.idUsuario = administrador.idUsuario " +
                    "WHERE usuario.nombre = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoP, usuario.apellidoM, " +
                    "usuario.contrasenia, usuario.TipoUsuario, usuario.estado, " +
                    "administrador.idAdministrador " +
                    "FROM usuario JOIN administrador ON usuario.idUsuario = administrador.idUsuario";
    public static final String SQL_EXISTE_ADMIN = "SELECT COUNT(*) FROM administrador";

    private Connection conexion;
    private static final Logger REGISTRADOR = Logger.getLogger(AdministradorDAO.class.getName());

    public AdministradorDAO() throws DAOExcepcion {
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
    public AdministradorDTO agregarAdministrador(AdministradorDTO administrador) throws DAOExcepcion {
        UsuarioDAO usuarioDAO = new UsuarioDAO(this.conexion);
        try {
            conexion.setAutoCommit(false);

            usuarioDAO.agregarUsuario(administrador);
            int idGenerado = administrador.getIdUsuario();

            if (idGenerado > 0) {
                try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                    sentenciaPreparada.setInt(1, idGenerado);
                    sentenciaPreparada.executeUpdate();

                    try (ResultSet llavesGeneradas = sentenciaPreparada.getGeneratedKeys()) {
                        if (llavesGeneradas.next()) {
                            int idAdminGenerado = llavesGeneradas.getInt(1);
                            administrador.setIdAdministrador(idAdminGenerado);
                            REGISTRADOR.log(Level.INFO, "Admin vinculado con ID de Usuario " + idGenerado + " y ID de Admin " + idAdminGenerado);
                        }
                    }
                }
                conexion.commit();
                REGISTRADOR.log(Level.INFO, "Administrador agregado correctamente " + administrador.getIdUsuario());
                return administrador;
            } else{
                REGISTRADOR.log(Level.WARNING, "Usuario base no generado para el Administrador");
                throw new EntidadNoCreadaExcepcion("Usuario base no creado correctamente");
            }
        } catch (SQLException sqlException) {
            try {
                if (conexion != null){
                    conexion.rollback();
                }
            } catch (SQLException sqlExceptionRollBack) {
                REGISTRADOR.log(Level.SEVERE, "Error al hacer rollback", sqlExceptionRollBack);
            }
            REGISTRADOR.log(Level.SEVERE, "Error SQL al agregar administrador", sqlException);
            throw new DAOExcepcion("Error al agregar administrador", sqlException);
        } catch (EntidadNoEncontradaExcepcion entidadNoEncontradaExcepcion) {
            try {
                if (conexion != null) {
                    conexion.rollback();
                }
            } catch (SQLException sqlExceptionRollback) {
                REGISTRADOR.log(Level.SEVERE, "Error al hacer rollback tras error inesperado", sqlExceptionRollback);
            }
            REGISTRADOR.log(Level.SEVERE, "Error no esperado en AdministradorDAO", entidadNoEncontradaExcepcion);
            throw new DAOExcepcion("Ocurrió un error inesperado al registrar el administrador", entidadNoEncontradaExcepcion);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException sqlException) {
                REGISTRADOR.log(Level.WARNING, "No se pudo resetear autocommit", sqlException);
            }
        }
    }

    @Override
    public AdministradorDTO buscarAdministradorPorId(int idAdministrador) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_BUSCAR_POR_ID)) {
            sentenciaPreparada.setInt(1, idAdministrador);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return new AdministradorDTO(
                    conjuntoResultado.getInt("idUsuario"),
                    conjuntoResultado.getString("nombre"),
                    conjuntoResultado.getString("apellidoP"),
                    conjuntoResultado.getString("apellidoM"),
                    conjuntoResultado.getString("contrasenia"), TipoEstadoUsuario.valueOf(conjuntoResultado.getString("estado")),
                    TipoDeUsuario.valueOf(conjuntoResultado.getString("TipoUsuario")),
                    conjuntoResultado.getInt("idAdministrador")
                    );
                } else {
                    REGISTRADOR.log(Level.INFO, "Administrador no encontrado con ID " + idAdministrador);
                    throw new EntidadNoEncontradaExcepcion("Administrador no encontrado con ID " + idAdministrador);
                }
            }
        } catch (SQLException sqlException){
            REGISTRADOR.log(Level.SEVERE, "Error SQL al buscar administrador por ID", sqlException);
            throw new DAOExcepcion("Error al buscar administrador por ID", sqlException);
        }
    }

    @Override
    public AdministradorDTO buscarAdministradorPorNombre(String nombre) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_BUSCAR_POR_NOMBRE)) {
            sentenciaPreparada.setString(1, nombre);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return new AdministradorDTO(
                            conjuntoResultado.getInt("idUsuario"),
                            conjuntoResultado.getString("nombre"),
                            conjuntoResultado.getString("apellidoP"),
                            conjuntoResultado.getString("apellidoM"),
                            conjuntoResultado.getString("contrasenia"), TipoEstadoUsuario.valueOf(conjuntoResultado.getString("estado")),
                            TipoDeUsuario.valueOf(conjuntoResultado.getString("TipoUsuario")),
                            conjuntoResultado.getInt("idAdministrador")
                    );
                } else {
                    REGISTRADOR.log(Level.INFO, "Administrador no encontrado con nombre " + nombre);
                    throw new EntidadNoEncontradaExcepcion("Administrador no encontrado con nombre " + nombre);
                }
            }
        } catch (SQLException sqlException){
            REGISTRADOR.log(Level.SEVERE, "Error SQL al buscar administrador por nombre", sqlException);
            throw new DAOExcepcion("Error al buscar administrador por nombre", sqlException);
        }
    }

    @Override
    public List<AdministradorDTO> listarAdministradores() throws DAOExcepcion {
        List<AdministradorDTO> listaAdministrador = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            while (conjuntoResultado.next()) {
                AdministradorDTO administrador = new AdministradorDTO(
                        conjuntoResultado.getInt("idUsuario"),
                        conjuntoResultado.getString("nombre"),
                        conjuntoResultado.getString("apellidoP"),
                        conjuntoResultado.getString("apellidoM"),
                        conjuntoResultado.getString("contrasenia"),
                        TipoEstadoUsuario.valueOf(conjuntoResultado.getString("estado")),
                        TipoDeUsuario.valueOf(conjuntoResultado.getString("TipoUsuario")),
                        conjuntoResultado.getInt("idAdministrador")
                );
                listaAdministrador.add(administrador);
            }
            return listaAdministrador;
        } catch (SQLException sqlExceptioN) {
            REGISTRADOR.log(Level.SEVERE, "Error al listar administradores", sqlExceptioN);
            throw new DAOExcepcion("Error al listar los administradores", sqlExceptioN);
        }
    }

    @Override
    public boolean existeAlgunAdministrador() throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada =
                     conexion.prepareStatement(SQL_EXISTE_ADMIN);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            if (conjuntoResultado.next()) {
                return conjuntoResultado.getInt(1) > 0;
            }
            return false;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al verificar la existencia de administrador", sqlExcepcion);
            throw new DAOExcepcion("Error al verificar la existencia de administrador", sqlExcepcion);
        }
    }
}