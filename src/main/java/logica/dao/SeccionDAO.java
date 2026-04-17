package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import interfaces.SeccionDAOInterfaz;
import logica.dto.SeccionDTO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SeccionDAO implements SeccionDAOInterfaz {
    public static final String SQL_INSERT = "INSERT INTO seccion(idSeccion, Nombre) VALUES (?, ?)";
    public static final String SQL_UPDATE = "UPDATE seccion SET Nombre = ? WHERE idSeccion = ?";
    public static final String SQL_SELECT_BY_ID = "SELECT * FROM seccion WHERE idSeccion = ?";
    public static final String SQL_SELECT_ALL = "SELECT * FROM seccion";

    private Connection conexion;
    private static final Logger logger = Logger.getLogger(SeccionDAO.class.getName());

    public SeccionDAO() throws DAOExcepcion {
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
    public void agregarSeccion(logica.dto.SeccionDTO seccionDTO) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT)) {
            preparedStatement.setInt(1, seccionDTO.getIdSeccion());
            preparedStatement.setString(2, seccionDTO.getNombre());
            preparedStatement.executeUpdate();

            logger.log(Level.INFO, "Seccion agregada exitosamente: " + seccionDTO.getNombre());
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al agregar seccion", e);
            throw new DAOExcepcion("Error al guardar la sección en la base de datos", e);
        }
    }

    @Override
    public void actualizarSeccion(logica.dto.SeccionDTO seccionDTO) throws DAOExcepcion {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, seccionDTO.getNombre());
            preparedStatement.setInt(2, seccionDTO.getIdSeccion());
            int filasAfectadas = preparedStatement.executeUpdate();
            if (filasAfectadas == 0) {
                throw new DAOExcepcion("No se encontró la sección para actualizar con ID: " + seccionDTO.getIdSeccion(), null);
            }
            logger.log(Level.INFO, "Seccion actualizada exitosamente. ID: " + seccionDTO.getIdSeccion());

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error SQL al actualizar seccion", e);
            throw new DAOExcepcion("Error al modificar los datos de la sección", e);
        }
    }

    @Override
    public SeccionDTO obtenerSeccionPorId(int idSeccion) throws DAOExcepcion, EntidadNoEncontradaExcepcion{
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_ID)) {
            preparedStatement.setInt(1, idSeccion);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new SeccionDTO(
                            resultSet.getInt("idSeccion"),
                            resultSet.getString("Nombre")
                    );
                }else{
                    logger.log(Level.WARNING, "No se encontró la Sección con ID: " + idSeccion);
                    throw new EntidadNoEncontradaExcepcion("No se encontró la Sección con el ID proporcionado");
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error SQL al obtener seccion por ID: " + idSeccion, e);
            throw new DAOExcepcion("Error al consultar la sección", e);
        }
    }

    @Override
    public List<SeccionDTO> obtenerTodasLasSecciones() throws DAOExcepcion {
        List<SeccionDTO> lista = new ArrayList<>();
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                SeccionDTO seccion = new SeccionDTO(
                        resultSet.getInt("idSeccion"),
                        resultSet.getString("Nombre")
                );
                lista.add(seccion);
            }
            return lista;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error SQL al listar todas las secciones", e);
            throw new DAOExcepcion("Error al obtener la lista de secciones", e);
        }
    }
}
