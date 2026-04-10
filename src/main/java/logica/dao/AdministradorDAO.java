package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import interfaces.AdministradorDAOInterfaz;
import logica.dto.AdministradorDTO;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdministradorDAO implements AdministradorDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO administrador (idUsuario) VALUES (?)";

    private Connection conexion;
    private static final Logger logger = Logger.getLogger(AdministradorDAO.class.getName());

    public AdministradorDAO() throws DAOExcepcion {
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        }catch (IOException e){
            logger.log(Level.SEVERE, "Error de entrada/salida al configurar la conexión", e);
            throw new DAOExcepcion("Error al leer la configuración de la base de datos", e);
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error de SQL al intentar conectar", e);
            throw new DAOExcepcion("Error de acceso a la base de datos", e);
        }
    }

    @Override
    public void agregarAdministrador(AdministradorDTO admin) throws DAOExcepcion {
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
                logger.log(Level.INFO, "Administrador agregado correctamente: " + admin.getIdUsuario());
            }
        } catch (SQLException e) {
            try {
                if (conexion != null){
                    conexion.rollback();
                }
            } catch (SQLException exRollback) {
                logger.log(Level.SEVERE, "Error al hacer rollback", exRollback);
            }
            logger.log(Level.SEVERE, "Error SQL al agregar administrador", e);
            throw new DAOExcepcion("Error al agregar administrador", e);
        } catch (Exception e) {
            try {
                if (conexion != null) {
                    conexion.rollback();
                }
            } catch (SQLException exRollback) {
                logger.log(Level.SEVERE, "Error al hacer rollback tras error inesperado", exRollback);
            }
            logger.log(Level.SEVERE, "Error no esperado en AdministradorDAO", e);
            throw new DAOExcepcion("Ocurrió un error inesperado al registrar el administrador", e);
        } finally {
            try {
                conexion.setAutoCommit(true);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "No se pudo resetear autocommit", e);
            }
        }
    }
}
