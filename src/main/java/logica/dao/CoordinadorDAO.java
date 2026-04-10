package logica.dao;

import excepciones.DAOExcepcion;
import interfaces.CoordinadorDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.CoordinadorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;

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
    private static final Logger logger = Logger.getLogger(CoordinadorDAO.class.getName());
    private static final String SQL_INSERT  = "INSERT INTO Coordinador (idUsuario, NumeroDePersonal) VALUES (?, ?)";
    private static final String SQL_SELECT_ALL =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoPaterno, usuario.apellidoMaterno, " +
            "usuario.contrasenia, usuario.tipoDeUsuario, usuario.estado, " +
            "coordinador.NumeroDePersonal " +
            "FROM usuario JOIN coordinador ON usuario.idUsuario = coordinador.idUsuario";

    public CoordinadorDAO() throws IOException, SQLException {
        this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
    }

    @Override
    public void agregarCoordinador(CoordinadorDTO coordinador) throws DAOExcepcion {
        UsuarioDAO usuarioDAO = new UsuarioDAO(this.conexion);
        try {
            conexion.setAutoCommit(false);
            usuarioDAO.agregarUsuario(coordinador);
            int idGenerado = coordinador.getIdUsuario();

            if (idGenerado > 0) {
                try (PreparedStatement ps = conexion.prepareStatement(SQL_INSERT)) {
                    ps.setInt(1, idGenerado);
                    ps.setString(2, coordinador.getNumeroPersonal());
                    ps.executeUpdate();
                }
                conexion.commit();
            } else {
                throw new DAOExcepcion("No se pudo crear el usuario base", null);
            }
        } catch (SQLException e) {
            try {
                conexion.rollback();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error al hacer rollback", ex);
            }
            logger.log(Level.SEVERE, "Error al agregar coordinador", e);
            throw new DAOExcepcion("Error al agregar coordinador: ", e);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error al restaurar AutoCommit", ex);
            }
        }
    }

    @Override
    public List<CoordinadorDTO> listarCoordinador() throws DAOExcepcion {
        List<CoordinadorDTO> listaCoordinador = new ArrayList<>();
        try {
            try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while(resultSet.next()) {
                    CoordinadorDTO coordinador = new CoordinadorDTO(
                            resultSet.getInt("idUsuario"),
                            resultSet.getString("nombre"),
                            resultSet.getString("apellidoPaterno"),
                            resultSet.getString("apellidoMaterno"),
                            resultSet.getString("contrasenia"),
                            TipoEstado.valueOf(resultSet.getString("estado")),
                            TipoDeUsuario.valueOf(resultSet.getString("tipoDeUsuario")),
                            resultSet.getString("NumeroDePersonal")
                    );
                    listaCoordinador.add(coordinador);
                }
            }
            return listaCoordinador;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al listar a los coordinadores", e);
            throw new DAOExcepcion("Error al listar los coordinadores: ", e);
        }
    }
}
