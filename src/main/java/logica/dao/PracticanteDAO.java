package logica.dao;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.PracticanteDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.BuzonDTO;
import logica.dto.PracticanteDTO;
import logica.enums.GeneroDelPracticante;
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

public class PracticanteDAO implements PracticanteDAOInterfaz {
    private final Connection conexion;
    private static final Logger REGISTRADOR = Logger.getLogger(PracticanteDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO Practicante (idUsuario, Matricula, idSeccion, Semestre, " +
            "Genero, Edad, LenguaIndigena) VALUES ( ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_MATRICULA =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoP, usuario.apellidoM, " +
            "usuario.contrasenia, usuario.tipoUsuario, usuario.Estado, " +
            "practicante.matricula, practicante.idSeccion, practicante.semestre, practicante.Genero," +
            "practicante.edad, practicante.lenguaIndigena " +
            "FROM usuario JOIN practicante ON usuario.idUsuario = practicante.idUsuario " +
            "WHERE practicante.Matricula = ?";
    private static final String SQL_UPDATE = "UPDATE Practicante SET idSeccion = ?, Semestre = ?, Genero = ?, Edad = ?," +
            " LenguaIndigena = ? WHERE Matricula = ?";
    private static final String SQL_SELECT_ALL =
            "SELECT usuario.idUsuario, usuario.nombre, usuario.apellidoP, usuario.apellidoM, " +
            "usuario.contrasenia, usuario.tipoUsuario, usuario.Estado, " +
            "practicante.matricula, practicante.idSeccion, practicante.semestre, practicante.Genero," +
            "practicante.edad, practicante.lenguaIndigena " +
                    "FROM usuario JOIN practicante ON usuario.idUsuario = practicante.idUsuario ";
    private static final String SQL_EXISTE_MATRICULA = "SELECT COUNT(*) FROM Practicante WHERE Matricula = ?";


    public PracticanteDAO() throws DAOExcepcion {
        try{
        this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioExcepcion){
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de configuración", ioExcepcion);
            throw new DAOExcepcion("Error de configuracion", ioExcepcion);
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexion SQL en PracticanteDAO", sqlExcepcion);
            throw new DAOExcepcion("Error de base de datos", sqlExcepcion);
        }
    }

    @Override
    public boolean agregarPracticante(PracticanteDTO practicante) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        UsuarioDAO usuarioDAO = new UsuarioDAO(this.conexion);
        BuzonDAO buzonDAO = new BuzonDAO(this.conexion);
        try {
            conexion.setAutoCommit(false);
            usuarioDAO.agregarUsuario(practicante);
            int idGenerado = practicante.getIdUsuario();
            if (idGenerado > 0) {
                try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT)) {
                    sentenciaPreparada.setInt(1, idGenerado);
                    sentenciaPreparada.setString(2, practicante.getMatricula());
                    sentenciaPreparada.setInt(3, practicante.getIdSeccion());
                    sentenciaPreparada.setString(4, practicante.getSemestre());
                    sentenciaPreparada.setString(5, practicante.getGeneroDelPracticante().name());
                    sentenciaPreparada.setInt(6, practicante.getEdad());
                    sentenciaPreparada.setBoolean(7, practicante.isLenguaIndigena());
                    sentenciaPreparada.executeUpdate();
                }
                BuzonDTO buzonDTO = new BuzonDTO(idGenerado);
                buzonDAO.agregarBuzon(buzonDTO);
                conexion.commit();
                REGISTRADOR.log(Level.INFO, "Practicante agregado exitosamente " + practicante.getMatricula());
                return true;
            } else {
                REGISTRADOR.log(Level.SEVERE, "No se pudo crear usuario base para practicante");
                throw new EntidadNoEncontradaExcepcion( "No se pudo crear el usuario base");
            }
        } catch (SQLException sqlExcepcion) {
            try {
                conexion.rollback();
            } catch (SQLException sqlExcepcionRollback) {
                REGISTRADOR.log(Level.SEVERE, "Error al hacer rollback", sqlExcepcionRollback);
            }
            REGISTRADOR.log(Level.SEVERE, "Error al agregar practicante", sqlExcepcion);
            throw new DAOExcepcion("Error al agregar practicante: ", sqlExcepcion);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException sqlExcepcion) {
                REGISTRADOR.log(Level.SEVERE, "Error al restaurar AutoCommit", sqlExcepcion);
            }
        }
    }

    @Override
    public boolean actualizarPracticante(PracticanteDTO practicante) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE)) {
            sentenciaPreparada.setInt(1, practicante.getIdSeccion());
            sentenciaPreparada.setString(2, practicante.getSemestre());
            sentenciaPreparada.setString(3, practicante.getGeneroDelPracticante().name());
            sentenciaPreparada.setInt(4, practicante.getEdad());
            sentenciaPreparada.setBoolean(5, practicante.isLenguaIndigena());
            sentenciaPreparada.setString(6, practicante.getMatricula());
            sentenciaPreparada.executeUpdate();
            REGISTRADOR.log(Level.INFO, "Practicante actualizado correctamente " + practicante.getMatricula());
            return true;
        } catch (SQLException sqlExcepcion){
            REGISTRADOR.log(Level.SEVERE, "Error al actualizar al practicante", sqlExcepcion);
            throw new DAOExcepcion("Error al actualizar al Practicante ", sqlExcepcion);
        }
    }

    @Override
    public PracticanteDTO buscarPracticantePorMatricula(String matricula) throws DAOExcepcion, EntidadNoEncontradaExcepcion{
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_BUSCAR_POR_MATRICULA)) {
            sentenciaPreparada.setString(1, matricula);
            ResultSet conjuntoResultado = sentenciaPreparada.executeQuery();
            if (conjuntoResultado.next()) {
                PracticanteDTO practicante = new PracticanteDTO(
                        conjuntoResultado.getInt("idUsuario"),
                        conjuntoResultado.getString("nombre"),
                        conjuntoResultado.getString("apellidoP"),
                        conjuntoResultado.getString("apellidoM"),
                        conjuntoResultado.getString("contrasenia"),
                        TipoEstadoUsuario.valueOf(conjuntoResultado.getString("estado")),
                        TipoDeUsuario.valueOf(conjuntoResultado.getString("tipoUsuario"))
                );

                practicante.setMatricula(conjuntoResultado.getString("Matricula"));
                practicante.setIdSeccion(conjuntoResultado.getInt("idSeccion"));
                practicante.setSemestre(conjuntoResultado.getString("Semestre"));
                practicante.setGeneroDelPracticante(GeneroDelPracticante.valueOf(conjuntoResultado.getString("Genero")));
                practicante.setEdad(conjuntoResultado.getInt("Edad"));
                practicante.setLenguaIndigena(conjuntoResultado.getBoolean("LenguaIndigena"));

                return practicante;
            } else {
                REGISTRADOR.log(Level.WARNING, "No se encontró practicante con matricula " + matricula);
                throw new EntidadNoEncontradaExcepcion("No existe practicante con matricula " + matricula);
            }

        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al buscar al practicante", sqlExcepcion);
            throw new DAOExcepcion("Error al buscar al Practicante: ", sqlExcepcion);
        }
    }

    @Override
    public List<PracticanteDTO> listarPracticantes() throws DAOExcepcion {
        List<PracticanteDTO> listaPracticante = new ArrayList<>();

        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {

            while (conjuntoResultado.next()) {
                PracticanteDTO practicante = new PracticanteDTO(
                        conjuntoResultado.getInt("idUsuario"),
                        conjuntoResultado.getString("nombre"),
                        conjuntoResultado.getString("apellidoP"),
                        conjuntoResultado.getString("apellidoM"),
                        conjuntoResultado.getString("contrasenia"),
                        TipoEstadoUsuario.valueOf(conjuntoResultado.getString("estado")),
                        TipoDeUsuario.valueOf(conjuntoResultado.getString("tipoUsuario"))
                );

                practicante.setMatricula(conjuntoResultado.getString("Matricula"));
                practicante.setIdSeccion(conjuntoResultado.getInt("idSeccion"));
                practicante.setSemestre(conjuntoResultado.getString("Semestre"));
                practicante.setGeneroDelPracticante(GeneroDelPracticante.valueOf(conjuntoResultado.getString("Genero")));
                practicante.setEdad(conjuntoResultado.getInt("Edad"));
                practicante.setLenguaIndigena(conjuntoResultado.getBoolean("LenguaIndigena"));

                listaPracticante.add(practicante);
            }

            return listaPracticante;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al listar los practicantes", sqlExcepcion);
            throw new DAOExcepcion("Error al listar los Practicantes: ", sqlExcepcion);
        }
    }

    @Override
    public boolean existePracticanteConMatricula(String matricula) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_EXISTE_MATRICULA)) {
            sentenciaPreparada.setString(1, matricula);
            try (ResultSet resultSet = sentenciaPreparada.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al verificar matrícula duplicada", sqlExcepcion);
            throw new DAOExcepcion("Error al verificar si existe la matrícula", sqlExcepcion);
        }
    }
}
