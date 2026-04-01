package logica.dao;

import interfaces.PracticanteDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.PracticanteDTO;
import logica.enums.EstadoDelPracticante;
import logica.enums.GeneroDelPracticante;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PracticanteDAO extends ConexionBD implements PracticanteDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO Practicante (Matricula, idSeccion, Semestre, Estado, Genero, Edad, LenguaIndigena) VALUES ( ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_MATRICULA = "SELECT * FROM Practicante WHERE Matricula = ?";
    private static final String SQL_UPDATE = "UPDATE Practicante SET idSeccion = ?, Semestre = ?, Estado = ?, Genero = ?, Edad = ?, LenguaIndigena = ? WHERE Matricula = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Matricula";

    public PracticanteDAO() {
        super();
    }

    @Override
    public void agregarPracticante(PracticanteDTO practicante) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)){
            preparedStatement.setString(1, practicante.getMatricula());
            preparedStatement.setInt(2, practicante.getIdSeccion());
            preparedStatement.setString(3, practicante.getSemestre());
            preparedStatement.setString(4, practicante.getEstadoDelPracticante().name());
            preparedStatement.setString(5, practicante.getGeneroDelPracticante().name());
            preparedStatement.setInt(6, practicante.getEdad());
            preparedStatement.setBoolean(7, practicante.isLenguaIndigena());
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            throw new Exception("Error al agregar el Practicante: " + e.getMessage());
        }
    }

    @Override
    public void actualizarPracticante(PracticanteDTO practicante) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, practicante.getMatricula());
            preparedStatement.setInt(2, practicante.getIdSeccion());
            preparedStatement.setString(3, practicante.getSemestre());
            preparedStatement.setString(4, practicante.getEstadoDelPracticante().name());
            preparedStatement.setString(5, practicante.getGeneroDelPracticante().name());
            preparedStatement.setInt(6, practicante.getEdad());
            preparedStatement.setBoolean(7, practicante.isLenguaIndigena());
            preparedStatement.executeUpdate();
        } catch (SQLException e){
            throw new Exception("Error al actualizar al Practicante: " + e.getMessage());
        }
    }

    @Override
    public PracticanteDTO buscarPracticantePorIdPracticante(int idPracticante) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_BUSCAR_POR_MATRICULA)){
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                String matricula = resultSet.getString("Matricula");
                int idSeccion = resultSet.getInt("idSeccion");
                String semestre = resultSet.getString("Semestre");
                String estado = resultSet.getString("Estado");
                String genero = resultSet.getString("Genero");
                int edad = resultSet.getInt("Edad");
                boolean lenguaIndigena = resultSet.getBoolean("LenguaIndigena");
                return new PracticanteDTO(matricula, idSeccion, semestre, EstadoDelPracticante.valueOf(estado), GeneroDelPracticante.valueOf(genero), edad, lenguaIndigena);
            }else{
                return null;
            }
        } catch (SQLException e){
            throw new Exception("Error al buscar al Practicante: " + e.getMessage());
        }
    }

    @Override
    public List<PracticanteDTO> listarPracticantes() throws Exception {
        List<PracticanteDTO> listaPracticante = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);  ResultSet resultSet = preparedStatement.executeQuery();) {
            while (resultSet.next()) {
                PracticanteDTO practicante = new PracticanteDTO(
                        resultSet.getString("Matricula"),
                        resultSet.getInt("idSeccion"),
                        resultSet.getString("Semestre"),
                        EstadoDelPracticante.valueOf(resultSet.getString("Estado")),
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
