package logica.dao;
import accesodatos.ConexionBD;
import interfaces.ProfesorDAOInterfaz;
import logica.dto.ProfesorDTO;
import logica.enums.TipoEstado;
import logica.enums.TipoTurno;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProfesorDAO extends ConexionBD implements ProfesorDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO profesor(NumeroDePersonal, Turno) VALUES (?, ?)";
    private static final String SQL_BUSCAR_POR_NUM_PERSONAL = "SELECT * FROM profesor WHERE NumeroDePersonal = ?";
    private static final String SQL_UPDATE = "UPDATE profesor SET Turno = ?, Estado = ? WHERE NumeroDePersonal = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM profesor";

    public ProfesorDAO() {
        super();
    }

    @Override
    public void agregarProfesor(ProfesorDTO profesor) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)) {
            preparedStatement.setString(1, profesor.getNumeroDePersonal());
            preparedStatement.setString(2, profesor.getTurno().name());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error al agregar el profesor: " + e.getMessage());
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
    public ProfesorDTO buscarProfesorPorNumPersonal(String numPersonal) throws Exception{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_NUM_PERSONAL)) {
            preparedStatement.setString(1, numPersonal);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String numeroDePersonal = resultSet.getString("NumeroDePersonal");
                String turnoStr = resultSet.getString("Turno");
                String estadoStr = resultSet.getString("Estado");
                return new ProfesorDTO(numeroDePersonal, TipoTurno.valueOf(turnoStr), TipoEstado.valueOf(estadoStr));
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
                    resultSet.getString("NumeroDePersonal"),
                    TipoTurno.valueOf(resultSet.getString("Turno")),
                    TipoEstado.valueOf(resultSet.getString("Estado"))
                );
                listaProfesor.add(profesor);
            }
            return listaProfesor;
        } catch (SQLException e) {
            throw new Exception("Error al listar los profesores: " + e.getMessage());
        }
    }



}
