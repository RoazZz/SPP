package logica.dao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.SeccionDAOInterfaz;
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
    private static final Logger REGISTRADOR = Logger.getLogger(SeccionDAO.class.getName());

    public SeccionDAO() throws DAOExcepcion {
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioException){
            REGISTRADOR.log(Level.SEVERE, "Error de entrada/salida al configurar la conexión", ioException);
            throw new DAOExcepcion("Error al leer la configuración de la base de datos", ioException);
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error de SQL al intentar conectar", sqlException);
            throw new DAOExcepcion("Error de acceso a la base de datos", sqlException);
        }
    }

    @Override
    public SeccionDTO agregarSeccion(logica.dto.SeccionDTO seccionDTO) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT)) {
            sentenciaPreparada.setInt(1, seccionDTO.getIdSeccion());
            sentenciaPreparada.setString(2, seccionDTO.getNombre());
            sentenciaPreparada.executeUpdate();

            REGISTRADOR.log(Level.INFO, "Seccion agregada exitosamente " + seccionDTO.getNombre());
            return seccionDTO;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al agregar seccion", sqlException);
            throw new DAOExcepcion("Error al guardar la sección en la base de datos", sqlException);
        }
    }

    @Override
    public boolean actualizarSeccion(logica.dto.SeccionDTO seccionDTO) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE)) {
            sentenciaPreparada.setString(1, seccionDTO.getNombre());
            sentenciaPreparada.setInt(2, seccionDTO.getIdSeccion());
            int filasAfectadas = sentenciaPreparada.executeUpdate();
            if (filasAfectadas > 0) {
                REGISTRADOR.log(Level.INFO, "Seccion actualizada exitosamente " + seccionDTO.getNombre());
                return true;
            } else{
                REGISTRADOR.log(Level.WARNING, "No se encontró la Sección para actualizar. ID " + seccionDTO.getIdSeccion());
                throw new EntidadNoEncontradaExcepcion("No se encontró la Sección con el ID " + seccionDTO.getIdSeccion());
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al actualizar seccion", sqlException);
            throw new DAOExcepcion("Error al modificar los datos de la sección", sqlException);
        }
    }

    @Override
    public SeccionDTO obtenerSeccionPorId(int idSeccion) throws DAOExcepcion, EntidadNoEncontradaExcepcion{
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_ID)) {
            sentenciaPreparada.setInt(1, idSeccion);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return new SeccionDTO(
                            conjuntoResultado.getInt("idSeccion"),
                            conjuntoResultado.getString("Nombre")
                    );
                } else{
                    REGISTRADOR.log(Level.WARNING, "No se encontró la Sección con ID " + idSeccion);
                    throw new EntidadNoEncontradaExcepcion("No se encontró la Sección con el ID proporcionado");
                }
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al obtener seccion por ID " + idSeccion, sqlException);
            throw new DAOExcepcion("Error al consultar la sección", sqlException);
        }
    }

    @Override
    public List<SeccionDTO> obtenerTodasLasSecciones() throws DAOExcepcion {
        List<SeccionDTO> lista = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            while (conjuntoResultado.next()) {
                SeccionDTO seccion = new SeccionDTO(
                        conjuntoResultado.getInt("idSeccion"),
                        conjuntoResultado.getString("Nombre")
                );
                lista.add(seccion);
            }
            return lista;
        } catch (SQLException sqlExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al listar todas las secciones", sqlExcepcion);
            throw new DAOExcepcion("Error al obtener la lista de secciones", sqlExcepcion);
        }
    }
}
