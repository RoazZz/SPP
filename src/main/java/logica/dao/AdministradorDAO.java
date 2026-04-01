package logica.dao;

import accesodatos.ConexionBD;
import interfaces.AdministradorDAOInterfaz;
import logica.dto.AdministradorDTO;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

public class AdministradorDAO extends ConexionBD implements AdministradorDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO administrador (idUsuario) VALUES (?)";
    public AdministradorDAO() {
        super();
    }

    public AdministradorDAO(Connection conexionExistente) {
        this.conexion = conexionExistente;
    }

    @Override
    public void agregarAdministrador(AdministradorDTO admin) throws Exception {
        UsuarioDAO usuarioDAO = new UsuarioDAO(this.conexion);
        try {
            conexion.setAutoCommit(false);

            usuarioDAO.agregarUsuario(admin);
            int idGenerado = admin.getIdUsuario();

            if (idGenerado > 0) {
                try (PreparedStatement ps = conexion.prepareStatement(SQL_INSERT)) {
                    ps.setInt(1, idGenerado);
                    ps.executeUpdate();
                }
                conexion.commit();
            }
        } catch (SQLException e) {
            if (conexion != null) conexion.rollback();
            throw new Exception("Error al agregar administrador: " + e.getMessage());
        } finally {
            conexion.setAutoCommit(true);
        }
    }
}
