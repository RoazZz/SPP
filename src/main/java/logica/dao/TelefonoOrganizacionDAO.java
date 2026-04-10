package logica.dao;

import interfaces.TelefonoOrganizacionDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.TelefonoOrganizacionDTO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class TelefonoOrganizacionDAO implements TelefonoOrganizacionDAOInterfaz {
    private final Connection conexion;
    private static final Logger logger = Logger.getLogger(TelefonoOrganizacionDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO Telefono_Organizacion (idOrganizacion, Telefono) VALUES ( ?, ?)";
    private static final String SQL_BUSCAR_POR_ID_TELEFONO_ORGANIZACION = "SELECT * FROM Telefono_Organizacion WHERE idOrganizacion = ?";
    private static final String SQL_UPDATE = "UPDATE Telefono_Organizacion SET Telefono = ? WHERE idOrganizacion = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Telefono_Organizacion";

    public TelefonoOrganizacionDAO() throws SQLException, IOException {
        this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
    }

    @Override
    public void agregarTelefonoOrganizacion(TelefonoOrganizacionDTO telefonoOrganizacion) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)) {
            for (String telefono : telefonoOrganizacion.getTelefono()) {
                preparedStatement.setString(1, telefonoOrganizacion.getIdOrganizacion());
                preparedStatement.setString(2, telefono);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new Exception("Error al insertar el teléfono: " + e.getMessage());
        }

    }

    @Override
    public void actualizarTelefonoOrganizacionVinculada(TelefonoOrganizacionDTO telefonoOrganizaciono) throws Exception {

    }

    @Override
    public TelefonoOrganizacionDTO buscarProyectoPorIdOrganizacion(String idOrganizacion) throws Exception {
        return null;
    }

    @Override
    public List<TelefonoOrganizacionDTO> listarTelefonosPorOrganizacion() throws Exception {
        return List.of();
    }
}
