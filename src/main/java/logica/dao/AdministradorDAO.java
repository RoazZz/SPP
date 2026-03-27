package logica.dao;

import accesodatos.ConexionBD;
import logica.dto.AdministradorDTO;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdministradorDAO extends ConexionBD {
    private static String  SQL_INSERT = "INSERT INTO administrador (idAdmin) VALUES (?)";
    public AdministradorDAO() {
        super();
    }

    public void agregar(AdministradorDTO admin) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, admin.getIdAdmin());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    admin.setIdAdmin(resultSet.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al agregar administrador: " + e.getMessage());
        }
    }
}
