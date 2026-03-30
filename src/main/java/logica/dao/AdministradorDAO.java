package logica.dao;

import accesodatos.ConexionBD;
import interfaces.AdministradorDAOInterfaz;
import logica.dto.AdministradorDTO;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdministradorDAO extends ConexionBD implements AdministradorDAOInterfaz {
    private static final String  SQL_INSERT = "INSERT INTO administrador (idAdmin) VALUES (?)"; //EVALUAR SI NOS FALTAN CAMPOS...
    private static final String SQL_SELECT_ALL = "SELECT * FROM administrador";
    public AdministradorDAO() {
        super();
    }

    @Override
    public void agregarAdministrador(AdministradorDTO admin) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, admin.getIdAdmin());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    admin.setIdAdmin(resultSet.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al agregar administrador: " + e.getMessage()); //PONER LOGGERS EN LUAGR DE EXCEPTIONS
        }
    }
}
