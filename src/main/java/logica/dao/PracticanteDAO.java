package logica.dao;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.PracticanteDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.PracticanteDTO;
import logica.enums.GeneroDelPracticante;
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

public class PracticanteDAO implements PracticanteDAOInterfaz {
    private final Connection conexion;
    private static final Logger logger = Logger.getLogger(PracticanteDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO Practicante (idUsuario, Matricula, idSeccion, Semestre, Genero, Edad, LenguaIndigena) VALUES ( ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_MATRICULA =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoP, usuario.apellidoM, " +
            "usuario.contrasenia, usuario.tipoUsuario, usuario.Estado, " +
            "practicante.matricula, practicante.idSeccion, practicante.semestre, practicante.Genero," +
            "practicante.edad, practicante.lenguaIndigena " +
            "FROM usuario JOIN practicante ON usuario.idUsuario = practicante.idUsuario " +
            "WHERE practicante.Matricula = ?";
    private static final String SQL_UPDATE = "UPDATE Practicante SET idSeccion = ?, Semestre = ?, Genero = ?, Edad = ?, LenguaIndigena = ? WHERE Matricula = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoP, usuario.apellidoM, " +
            "usuario.contrasenia, usuario.tipoUsuario, usuario.Estado, " +
            "practicante.matricula, practicante.idSeccion, practicante.semestre, practicante.Genero," +
            "practicante.edad, practicante.lenguaIndigena " +
                    "FROM usuario JOIN practicante ON usuario.idUsuario = practicante.idUsuario ";

    public PracticanteDAO() throws DAOExcepcion {
        try{
        this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error al leer archivo de configuración", e);
            throw new DAOExcepcion("Error de configuracion", e);
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error de conexion SQL en PracticanteDAO", e);
            throw new DAOExcepcion("Error de base de datos", e);
        }
    }

    @Override
    public boolean agregarPracticante(PracticanteDTO practicante) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        UsuarioDAO usuarioDAO = new UsuarioDAO(this.conexion);
        try {
            conexion.setAutoCommit(false);
            usuarioDAO.agregarUsuario(practicante);
            int idGenerado = practicante.getIdUsuario();
            if (idGenerado > 0) {
                try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)) {
                    preparedStatement.setInt(1, idGenerado);
                    preparedStatement.setString(2, practicante.getMatricula());
                    preparedStatement.setInt(3, practicante.getIdSeccion());
                    preparedStatement.setString(4, practicante.getSemestre());
                    preparedStatement.setString(5, practicante.getGeneroDelPracticante().name());
                    preparedStatement.setInt(6, practicante.getEdad());
                    preparedStatement.setBoolean(7, practicante.isLenguaIndigena());
                    preparedStatement.executeUpdate();
                }
                conexion.commit();
                logger.log(Level.INFO, "Practicante agregado exitosamente: " + practicante.getMatricula());
                return true;
            } else {
                logger.log(Level.SEVERE, "No se pudo crear usuario base para practicante");
                throw new EntidadNoEncontradaExcepcion( "No se pudo crear el usuario base");
            }
        } catch (SQLException e) {
            try {
                conexion.rollback();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error al hacer rollback", ex);
            }
            logger.log(Level.SEVERE, "Error al agregar practicante", e);
            throw new DAOExcepcion("Error al agregar practicante: ", e);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error al restaurar AutoCommit", ex);
            }
        }
    }

    @Override
    public boolean actualizarPracticante(PracticanteDTO practicante) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setInt(1, practicante.getIdSeccion());
            preparedStatement.setString(2, practicante.getSemestre());
            preparedStatement.setString(3, practicante.getGeneroDelPracticante().name());
            preparedStatement.setInt(4, practicante.getEdad());
            preparedStatement.setBoolean(5, practicante.isLenguaIndigena());
            preparedStatement.setString(6, practicante.getMatricula());
            preparedStatement.executeUpdate();
            logger.log(Level.INFO, "Practicante actualizado correctamente: " + practicante.getMatricula());
            return true;
        } catch (SQLException e){
            logger.log(Level.SEVERE, "Error al actualizar al practicante", e);
            throw new DAOExcepcion("Error al actualizar al Practicante: ", e);
        }
    }

    @Override
    public PracticanteDTO buscarPracticantePorMatricula(String matricula) throws DAOExcepcion, EntidadNoEncontradaExcepcion{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_MATRICULA)) {
            preparedStatement.setString(1, matricula);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new PracticanteDTO(
                        resultSet.getInt("idUsuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellidoP"),
                        resultSet.getString("apellidoM"),
                        resultSet.getString("contrasenia"),
                        TipoEstado.valueOf(resultSet.getString("estado")),
                        TipoDeUsuario.valueOf(resultSet.getString("tipoUsuario")),
                        resultSet.getString("Matricula"),
                        resultSet.getInt("idSeccion"),
                        resultSet.getString("Semestre"),
                        GeneroDelPracticante.valueOf(resultSet.getString("Genero")),
                        resultSet.getInt("Edad"),
                        resultSet.getBoolean("LenguaIndigena")
                );
            } else {
                logger.log(Level.WARNING, "No se encontró practicante con matricula: " + matricula);
                throw new EntidadNoEncontradaExcepcion("No existe practicante con matricula: " + matricula);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar al practicante", e);
            throw new DAOExcepcion("Error al buscar al Practicante: ", e);
        }
    }

    @Override
    public List<PracticanteDTO> listarPracticantes() throws DAOExcepcion {
        List<PracticanteDTO> listaPracticante = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);  ResultSet resultSet = preparedStatement.executeQuery();) {
            while (resultSet.next()) {
                PracticanteDTO practicante = new PracticanteDTO(
                        resultSet.getInt("idUsuario"),
                        resultSet.getString("nombre"),
                        resultSet.getString("apellidoP"),
                        resultSet.getString("apellidoM"),
                        resultSet.getString("contrasenia"),
                        TipoEstado.valueOf(resultSet.getString("estado")),
                        TipoDeUsuario.valueOf(resultSet.getString("tipoUsuario")),
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
            logger.log(Level.SEVERE, "Error al listar los practicantes", e);
            throw new DAOExcepcion("Error al listar los Practicantes: ", e);
        }
    }
}
