package logica.dao;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.BitacoraDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.BitacoraDTO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BitacoraDAO implements BitacoraDAOInterfaz {
    private final Connection conexion;
    private static final Logger REGISTRADOR = Logger.getLogger(BitacoraDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO Bitacora (Matricula, Fecha_Hora, TipoEvento, Descripcion) " +
            "VALUES ( ?, ?, ?, ?)";
    private static final String SQL_BUSCAR_POR_MATRICULA = "SELECT * FROM Bitacora WHERE Matricula = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Bitacora";

    public BitacoraDAO() throws DAOExcepcion {
        try{
        this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioException){
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de configuración", ioException);
            throw new DAOExcepcion("Error de configuracion", ioException);
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexion SQL en BitacoraDAO", sqlException);
            throw new DAOExcepcion("Error de base de datos", sqlException);
        }
    }

    @Override
    public boolean agregarBitacora(BitacoraDTO bitacora) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            sentenciaPreparada.setString(1, bitacora.getMatricula());
            sentenciaPreparada.setTimestamp(2, java.sql.Timestamp.valueOf(bitacora.getFechaHora()));
            sentenciaPreparada.setString(3, bitacora.getTipoEvento());
            sentenciaPreparada.setString(4, bitacora.getDescripcionEvento());
            sentenciaPreparada.executeUpdate();
            try (ResultSet conjuntoResultado = sentenciaPreparada.getGeneratedKeys()) {
                if (conjuntoResultado.next()) {
                    bitacora.setIdRegistro(conjuntoResultado.getInt(1));
                }
            }
            REGISTRADOR.log(Level.INFO, "Bitacora creada exitosamente: " + bitacora.getIdRegistro());
            return true;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al agregar bitacora", sqlException);
            throw new DAOExcepcion ("Error al agregar la bitacora: ", sqlException);
        }
    }

    @Override
    public BitacoraDTO buscarBitacoraPorMatricula(String matricula) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_BUSCAR_POR_MATRICULA)) {
            sentenciaPreparada.setString(1, matricula);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return new BitacoraDTO(
                            conjuntoResultado.getInt("idRegistro"),
                            conjuntoResultado.getString("Matricula"),
                            conjuntoResultado.getString("TipoEvento"),
                            conjuntoResultado.getTimestamp("Fecha_Hora").toLocalDateTime(),
                            conjuntoResultado.getString("Descripcion")
                    );
                } else{
                    REGISTRADOR.log(Level.WARNING, "No se encontro alguna bitacora con Matricula  " + matricula);
                    throw new EntidadNoEncontradaExcepcion("No existe bitacora con Matricula  " + matricula);
                }
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al buscar bitacora", sqlException);
            throw new DAOExcepcion("Error al buscar Bitacora por Matricula: ", sqlException);
        }
    }

    @Override
    public List<BitacoraDTO> listarBitacoras() throws DAOExcepcion {
        List<BitacoraDTO> listaBitacora = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            while (conjuntoResultado.next()) {
                BitacoraDTO bitacora = new BitacoraDTO(
                        conjuntoResultado.getInt("idRegistro"),
                        conjuntoResultado.getString("Matricula"),
                        conjuntoResultado.getString("TipoEvento"),
                        conjuntoResultado.getTimestamp("Fecha_Hora").toLocalDateTime(),
                        conjuntoResultado.getString("Descripcion")
                );
                listaBitacora.add(bitacora);
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al buscar bitacora", sqlException);
            throw new DAOExcepcion("Error al listar las bitacoras: ", sqlException);
        }
        return listaBitacora;
    }
}
