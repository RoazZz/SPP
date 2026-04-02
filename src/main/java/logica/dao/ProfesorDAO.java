package logica.dao;
import accesodatos.ConexionBD;
import interfaces.ProfesorDAOInterfaz;
import logica.dto.ProfesorDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import logica.enums.TipoTurno;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProfesorDAO extends ConexionBD implements ProfesorDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO profesor(idUsuario, NumeroDePersonal, Turno) VALUES (?, ?, ?)";
    private static final String SQL_BUSCAR_POR_NUM_PERSONAL =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoPaterno, usuario.apellidoMaterno, " +
            "usuario.contrasenia, usuario.tipoDeUsuario, usuario.estado, " +
            "profesor.NumeroDePersonal, profesor.Turno " +
            "FROM usuario JOIN profesor ON usuario.idUsuario = profesor.idUsuario " +
            "WHERE profesor.NumeroDePersonal = ?";
    private static final String SQL_UPDATE = "UPDATE profesor SET Turno = ?, Estado = ? WHERE NumeroDePersonal = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoPaterno, usuario.apellidoMaterno, " +
            "usuario.contrasenia, usuario.tipoDeUsuario, usuario.estado, " +
            "profesor.NumeroDePersonal, profesor.Turno " +
            "FROM usuario JOIN profesor ON usuario.idUsuario = profesor.idUsuario";

    public ProfesorDAO() {
        super();
    }

    @Override
    public void agregarProfesor(ProfesorDTO profesor) throws Exception {
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
            } else {
                throw new Exception("No se pudo crear el usuario base");
            }
        } catch (SQLException e) {
            conexion.rollback();
            throw new Exception("Error al agregar profesor: " + e.getMessage());
        } finally {
            conexion.setAutoCommit(true);
        }
    }

    @Override
    public void actualizarProfesor(ProfesorDTO profesor) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, profesor.getTurno().name());
            preparedStatement.setString(3, profesor.getNumeroDePersonal());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al actualizar el profesor: " + e.getMessage());
        }
    }

    @Override
    public ProfesorDTO buscarProfesorPorNumPersonal(String numPersonal) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_NUM_PERSONAL)) {
            preparedStatement.setString(1, numPersonal);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return new ProfesorDTO(
                        resultSet.getInt("idUsuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellidoPaterno"),
                        resultSet.getString("apellidoMaterno"),
                        resultSet.getString("contrasenia"),
                        TipoEstado.valueOf(resultSet.getString("estado")),
                        TipoDeUsuario.valueOf(resultSet.getString("tipoDeUsuario")),
                        resultSet.getString("NumeroDePersonal"),
                        TipoTurno.valueOf(resultSet.getString("Turno"))
                );
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new Exception("Error al buscar el profesor: " + e.getMessage());
        }
    }

    @Override
    public List<ProfesorDTO> listarProfesores() throws Exception {
        List<ProfesorDTO> listaProfesor = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);  ResultSet resultSet = preparedStatement.executeQuery();) {
            while (resultSet.next()) {
                ProfesorDTO profesor = new ProfesorDTO(
                        resultSet.getInt("idUsuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellidoPaterno"),
                        resultSet.getString("apellidoMaterno"),
                        resultSet.getString("contrasenia"),
                        TipoEstado.valueOf(resultSet.getString("estado")),
                        TipoDeUsuario.valueOf(resultSet.getString("tipoDeUsuario")),
                        resultSet.getString("NumeroDePersonal"),
                        TipoTurno.valueOf(resultSet.getString("Turno"))
                );
                listaProfesor.add(profesor);
            }
            return listaProfesor;
        } catch (SQLException e) {
            throw new Exception("Error al listar los profesores: " + e.getMessage());
        }
    }

}
