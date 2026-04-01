package logica.dao;

import interfaces.CoordinadorDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.CoordinadorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CoordinadorDAO extends ConexionBD implements CoordinadorDAOInterfaz{
    private static final String SQL_INSERT  = "INSERT INTO Coordinador (idUsuario, NumeroDePersonal) VALUES (?, ?)";
    private static final String SQL_SELECT_ALL =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoPaterno, usuario.apellidoMaterno, " +
            "usuario.contrasenia, usuario.tipoDeUsuario, usuario.estado, " +
            "coordinador.NumeroDePersonal" +
            "FROM usuario JOIN coordinador ON usuario.idUsuario = coordinador.idUsuario";

    public CoordinadorDAO(){
        super();
    }

    @Override
    public void agregarCoordinador(CoordinadorDTO coordinador) throws Exception {
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
                throw new Exception("No se pudo crear el usuario base");
            }
        } catch (SQLException e) {
            conexion.rollback();
            throw new Exception("Error al agregar coordinador: " + e.getMessage());
        } finally {
            conexion.setAutoCommit(true);
        }
    }

    @Override
    public List<CoordinadorDTO> listarCoordinador() throws Exception {
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
            throw new Exception("Error al listar los coordinadores: " + e.getMessage());
        }
    }
}
