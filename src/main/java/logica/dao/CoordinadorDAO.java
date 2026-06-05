package logica.dao;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.CoordinadorDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.BuzonDTO;
import logica.dto.CoordinadorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstadoUsuario;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoordinadorDAO implements CoordinadorDAOInterfaz{
    private final Connection conexion;
    private static final Logger REGISTRADOR = Logger.getLogger(CoordinadorDAO.class.getName());
    private static final String SQL_INSERT  = "INSERT INTO Coordinador (idUsuario, NumeroDePersonal) VALUES (?, ?)";
    private static final String SQL_UPDATE = "UPDATE Coordinador SET NumeroDePersonal = ? WHERE idUsuario = ?";
    private static final String SQL_BUSCAR_POR_NUM_PERSONAL =
            "SELECT usuario.idUsuario, usuario.Nombre, usuario.ApellidoP, usuario.ApellidoM, " +
                    "usuario.Contrasenia, usuario.Estado, usuario.TipoUsuario, " +
                    "coordinador.NumeroDePersonal " +
                    "FROM usuario JOIN coordinador ON usuario.idUsuario = coordinador.idUsuario " +
                    "WHERE coordinador.NumeroDePersonal = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoP, usuario.apellidoM, " +
                    "usuario.contrasenia, usuario.estado, usuario.tipoUsuario, " +
                    "coordinador.NumeroDePersonal " +
                    "FROM usuario JOIN coordinador ON usuario.idUsuario = coordinador.idUsuario " +
                    "WHERE usuario.estado = 'ACTIVO'";
    private static final String SQL_EXISTE_NUMERO_PERSONAL = "SELECT COUNT(*) FROM Coordinador" +
            " WHERE NumeroDePersonal = ? AND idUsuario != ?";

    public CoordinadorDAO() throws DAOExcepcion {
        try {
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioExcepcion){
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de configuración", ioExcepcion);
            throw new DAOExcepcion("Error de configuracion", ioExcepcion);
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexion SQL en CoordinadorDAO", sqlExcepcion);
            throw new DAOExcepcion("Error de base de datos", sqlExcepcion);
        }
    }

    @Override
    public boolean agregarCoordinador(CoordinadorDTO coordinador) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        UsuarioDAO usuarioDAO = new UsuarioDAO(this.conexion);
        BuzonDAO buzonDAO = new BuzonDAO(this.conexion);
        try {
            conexion.setAutoCommit(false);
            usuarioDAO.agregarUsuario(coordinador);
            int idGenerado = coordinador.getIdUsuario();

            if (idGenerado > 0) {
                try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT)) {
                    sentenciaPreparada.setInt(1, idGenerado);
                    sentenciaPreparada.setString(2, coordinador.getNumeroPersonal());
                    sentenciaPreparada.executeUpdate();
                }
                BuzonDTO buzonDTO = new BuzonDTO(idGenerado);
                buzonDAO.agregarBuzon(buzonDTO);
                conexion.commit();
                REGISTRADOR.log(Level.INFO, "Coordinador agregado exitosamente: " + coordinador.getNumeroPersonal());
                return true;
            } else {
                REGISTRADOR.log(Level.SEVERE, "No se pudo crear usuario base para coordinador");
                throw new EntidadNoEncontradaExcepcion( "No se pudo crear el usuario base");
            }
        } catch (SQLException sqlExcepcion) {
            try {
                conexion.rollback();
            } catch (SQLException sqlRollbackExcepcion) {
                REGISTRADOR.log(Level.SEVERE, "Error al hacer rollback", sqlRollbackExcepcion);
            }
            REGISTRADOR.log(Level.SEVERE, "Error al agregar coordinador", sqlExcepcion);
            throw new DAOExcepcion("Error al agregar coordinador: ", sqlExcepcion);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException sqlExcepcionAutoCommit) {
                REGISTRADOR.log(Level.SEVERE, "Error al restaurar AutoCommit", sqlExcepcionAutoCommit);
            }
        }
    }

    @Override
    public boolean actualizarCoordinador(CoordinadorDTO coordinador) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1,coordinador.getNumeroPersonal());
            preparedStatement.setInt(2, coordinador.getIdUsuario());
            preparedStatement.executeUpdate();
            REGISTRADOR.log(Level.INFO, "Coordinador actualizado correctamente: " + coordinador.getNumeroPersonal());
            return true;
        } catch (SQLException e){
            REGISTRADOR.log(Level.SEVERE, "Error al actualizar al Coordinador", e);
            throw new DAOExcepcion("Error al actualizar al Coordinador: ", e);
        }
    }

    @Override
    public CoordinadorDTO buscarCoordinadorPorNumeroDePersonal(String numeroPersonal) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_BUSCAR_POR_NUM_PERSONAL)) {
            sentenciaPreparada.setString(1, numeroPersonal);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return new CoordinadorDTO(
                            conjuntoResultado.getInt("idUsuario"),
                            conjuntoResultado.getString("nombre"),
                            conjuntoResultado.getString("apellidoP"),
                            conjuntoResultado.getString("apellidoM"),
                            conjuntoResultado.getString("contrasenia"),
                            TipoEstadoUsuario.valueOf(conjuntoResultado.getString("estado")),
                            TipoDeUsuario.valueOf(conjuntoResultado.getString("TipoUsuario")),
                            conjuntoResultado.getString("NumeroDePersonal")
                    );
                } else{
                    REGISTRADOR.log(Level.WARNING, "No se encontró coordinado con numero de personal: " + numeroPersonal);
                    throw new EntidadNoEncontradaExcepcion("Coordinador no encontrado con numero de personal: " + numeroPersonal);
                }
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al buscar coordinador por numero personal", sqlExcepcion);
            throw new DAOExcepcion("Error al buscar coordinador", sqlExcepcion);
        }
    }

    @Override
    public List<CoordinadorDTO> listarCoordinador() throws DAOExcepcion {
        List<CoordinadorDTO> listaCoordinador = new ArrayList<>();
        try {
            try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    CoordinadorDTO coordinador = new CoordinadorDTO(
                            resultSet.getInt("idUsuario"),
                            resultSet.getString("nombre"),
                            resultSet.getString("apellidoP"),
                            resultSet.getString("apellidoM"),
                            resultSet.getString("contrasenia"),
                            TipoEstadoUsuario.valueOf(resultSet.getString("estado")),
                            TipoDeUsuario.valueOf(resultSet.getString("tipoUsuario")),
                            resultSet.getString("NumeroDePersonal")
                    );
                    listaCoordinador.add(coordinador);
                }
            }
            return listaCoordinador;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al listar a los coordinadores", sqlExcepcion);
            throw new DAOExcepcion("Error al listar los coordinadores: ", sqlExcepcion);
        }
    }

    @Override
    public boolean existeCoordinadorConNumeroPersonal(String numeroPersonal, int idExcluir) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_EXISTE_NUMERO_PERSONAL)) {
            sentenciaPreparada.setString(1, numeroPersonal);
            sentenciaPreparada.setInt(2, idExcluir);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return conjuntoResultado.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al verificar número de personal duplicado en coordinador", sqlExcepcion);
            throw new DAOExcepcion("Error al verificar si existe el número de personal", sqlExcepcion);
        }
    }
}
