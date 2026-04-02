package logica.dao;

import interfaces.PracticanteDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.PracticanteDTO;
import logica.dto.ProfesorDTO;
import logica.enums.GeneroDelPracticante;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import logica.enums.TipoTurno;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PracticanteDAO extends ConexionBD implements PracticanteDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO Practicante (idUsuario, Matricula, idSeccion, Semestre, Genero, Edad, LenguaIndigena) VALUES ( ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_MATRICULA =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoPaterno, usuario.apellidoMaterno, " +
            "usuario.contrasenia, usuario.tipoDeUsuario, usuario.estado, " +
            "practicante.matricula, practicante.idSeccion, practicante.semestre, practicante.generoDelPracticante," +
            "practicante.edad, practicante.lenguaIndigena" +
            "FROM usuario JOIN practicante ON usuario.idUsuario = practicante.idUsuario " +
            "WHERE practicante.Matricula = ?";
    private static final String SQL_UPDATE = "UPDATE Practicante SET idSeccion = ?, Semestre = ?, Genero = ?, Edad = ?, LenguaIndigena = ? WHERE Matricula = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoPaterno, usuario.apellidoMaterno, " +
            "usuario.contrasenia, usuario.tipoDeUsuario, usuario.estado, " +
            "practicante.matricula, practicante.idSeccion, practicante.semestre, practicante.generoDelPracticante," +
            "practicante.edad, practicante.lenguaIndigena" +
            "FROM usuario JOIN practicante ON usuario.idUsuario = practicante.idUsuario " +
            "WHERE practicante.Matricula = ?";

    public PracticanteDAO() {
        super();
    }

    @Override
    public void agregarPracticante(PracticanteDTO practicante) throws Exception {
        UsuarioDAO usuarioDAO = new UsuarioDAO(this.conexion);
        try {
            conexion.setAutoCommit(false);
            usuarioDAO.agregarUsuario(practicante);
            int idGenerado = practicante.getIdUsuario();

            if (idGenerado > 0) {
                try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)) {
                    preparedStatement.setInt(1, idGenerado);
                    preparedStatement.setString(2, practicante.getMatricula());
                    preparedStatement.setString(3, practicante.getGeneroDelPracticante().name());
                    preparedStatement.setInt(4, practicante.getEdad());
                    preparedStatement.setBoolean(5, practicante.isLenguaIndigena());
                    preparedStatement.executeUpdate();
                }
                conexion.commit();
            } else {
                throw new Exception("No se pudo crear el usuario base");
            }
        } catch (SQLException e) {
            conexion.rollback();
            throw new Exception("Error al agregar practicante: " + e.getMessage());
        } finally {
            conexion.setAutoCommit(true);
        }
    }

    @Override
    public void actualizarPracticante(PracticanteDTO practicante) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, practicante.getMatricula());
            preparedStatement.setInt(2, practicante.getIdSeccion());
            preparedStatement.setString(3, practicante.getSemestre());
            preparedStatement.setString(4, practicante.getGeneroDelPracticante().name());
            preparedStatement.setInt(5, practicante.getEdad());
            preparedStatement.setBoolean(6, practicante.isLenguaIndigena());
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            throw new Exception("Error al actualizar al Practicante: " + e.getMessage());
        }
    }

    @Override
    public PracticanteDTO buscarPracticantePorMatricula(String matricula) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_MATRICULA)) {
            preparedStatement.setString(1, matricula);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new PracticanteDTO(
                        resultSet.getInt("idUsuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellidoPaterno"),
                        resultSet.getString("apellidoMaterno"),
                        resultSet.getString("contrasenia"),
                        TipoEstado.valueOf(resultSet.getString("estado")),
                        TipoDeUsuario.valueOf(resultSet.getString("tipoDeUsuario")),
                        resultSet.getString("Matricula"),
                        resultSet.getInt("idSeccion"),
                        resultSet.getString("Semestre"),
                        GeneroDelPracticante.valueOf(resultSet.getString("Genero")),
                        resultSet.getInt("Edad"),
                        resultSet.getBoolean("LenguaIndigena")
                );
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new Exception("Error al buscar al Practicante: " + e.getMessage());
        }
    }

    @Override
    public List<PracticanteDTO> listarPracticantes() throws Exception {
        List<PracticanteDTO> listaPracticante = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);  ResultSet resultSet = preparedStatement.executeQuery();) {
            while (resultSet.next()) {
                PracticanteDTO practicante = new PracticanteDTO(
                        resultSet.getInt("idUsuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellidoPaterno"),
                        resultSet.getString("apellidoMaterno"),
                        resultSet.getString("contrasenia"),
                        TipoEstado.valueOf(resultSet.getString("estado")),
                        TipoDeUsuario.valueOf(resultSet.getString("tipoDeUsuario")),
                        resultSet.getString("Matricula"),
                        resultSet.getInt("idSeccion"),
                        resultSet.getString("Semestre"),
                        GeneroDelPracticante.valueOf(resultSet.getString("Genero")),
                        resultSet.getInt("Edad"),
                        resultSet.getBoolean("LenguaIndigena")
                );
                listaPracticante.add(practicante);
            }
            return listaPracticante;
        } catch (SQLException e) {
            throw new Exception("Error al listar los Practicantes: " + e.getMessage());
        }
    }
}
