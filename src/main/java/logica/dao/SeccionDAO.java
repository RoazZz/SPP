package logica.dao;

import accesodatos.ConexionBD;
import interfaces.SeccionDAOInterfaz;
import logica.dto.SeccionDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SeccionDAO extends ConexionBD implements SeccionDAOInterfaz {
    public static final String SQL_INSERT = "INSERT INTO seccion(idSeccion, Nombre) VALUES (?, ?)";
    public static final String SQL_UPDATE = "UPDATE seccion SET Nombre = ? WHERE idSeccion = ?";
    public static final String SQL_SELECT_BY_ID = "SELECT * FROM seccion WHERE idSeccion = ?";
    public static final String SQL_SELECT_ALL = "SELECT * FROM seccion";

    @Override
    public void agregarSeccion(logica.dto.SeccionDTO seccionDTO) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_INSERT, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, seccionDTO.getIdSeccion());
            preparedStatement.setString(2, seccionDTO.getNombre());
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    seccionDTO.setIdSeccion(resultSet.getInt(1));
                }
            } catch (Exception e) {
                throw new Exception("Error al agregar sección: " + e.getMessage());
            }
        }
    }

    @Override
    public void actualizarSeccion(logica.dto.SeccionDTO seccionDTO) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, seccionDTO.getNombre());
            preparedStatement.setInt(2, seccionDTO.getIdSeccion());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw new Exception("Error al actualizar sección: " + e.getMessage());
        }
    }

    @Override
    public SeccionDTO obtenerSeccionPorId(int idSeccion) throws Exception {
        try (PreparedStatement preparedStatement = conexion.prepareStatement(SQL_SELECT_BY_ID)) {
            preparedStatement.setInt(1, idSeccion);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new SeccionDTO(
                            resultSet.getInt("idSeccion"),
                            resultSet.getString("Nombre")
                    );
                } else {
                    throw new Exception("No se encontró la sección con ID: " + idSeccion);
                }
            }
        } catch (Exception e) {
            throw new Exception("Error al obtener sección por ID: " + e.getMessage());
        }
    }

    @Override
    public List<SeccionDTO> obtenerTodasLasSecciones() throws Exception {
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
            throw new Exception("Error al obtener todas las secciones: " + e.getMessage());
        }
    }
}
