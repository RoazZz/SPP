package logica.dao;

import interfaces.CoordinadorDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.CoordinadorDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CoordinadorDAO extends ConexionBD implements CoordinadorDAOInterfaz{
    private static final String SQL_INSERT  = "INSERT INTO Coordinador (NumeroDePersonal) VALUES (?)";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Coordinador";
    public CoordinadorDAO(){
        super();
    }

    @Override
    public void agregarCoordinador(CoordinadorDTO coordinador) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)){
            preparedStatement.setString(1, coordinador.getNumeroPersonal());
        } catch (SQLException e) {
            throw new Exception("Error al agregar el coordinador: " + e.getMessage());
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
