package logica.dao;

import excepciones.DAOExcepcion;
import logica.interfaces.BitacoraSistemaDAOInterfaz;
import accesodatos.ConexionBD;
import logica.dto.BitacoraSistemaDTO;

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

public class BitacoraSistemaSistemaDAO implements BitacoraSistemaDAOInterfaz {
    private final Connection conexion;
    private static final Logger REGISTRADOR = Logger.getLogger(BitacoraSistemaSistemaDAO.class.getName());
    private static final String SQL_INSERT = "INSERT INTO Bitacora (RolUsuario, NombreUsuario, Fecha_Hora, TipoEvento, Descripcion) " + "VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_ALL = "SELECT * FROM Bitacora ORDER BY Fecha_Hora DESC";

    public BitacoraSistemaSistemaDAO() throws DAOExcepcion {
        try {
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioException) {
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de configuración", ioException);
            throw new DAOExcepcion("Error de configuracion", ioException);
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexion SQL en BitacoraDAO", sqlException);
            throw new DAOExcepcion("Error de base de datos", sqlException);
        }
    }

    @Override
    public boolean agregarBitacora(BitacoraSistemaDTO bitacora) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada =
                     conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            sentenciaPreparada.setString(1, bitacora.getRolUsuario());
            sentenciaPreparada.setString(2, bitacora.getNombreUsuario());
            sentenciaPreparada.setTimestamp(3, java.sql.Timestamp.valueOf(bitacora.getFechaHora()));
            sentenciaPreparada.setString(4, bitacora.getTipoEvento());
            sentenciaPreparada.setString(5, bitacora.getDescripcionEvento());
            sentenciaPreparada.executeUpdate();
            try (ResultSet conjuntoResultado = sentenciaPreparada.getGeneratedKeys()) {
                if (conjuntoResultado.next()) {
                    bitacora.setIdRegistro(conjuntoResultado.getInt(1));
                }
            }
            return true;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al agregar bitacora", sqlException);
            throw new DAOExcepcion("Error al agregar la bitacora: ", sqlException);
        }
    }

    @Override
    public List<BitacoraSistemaDTO> listarBitacoras() throws DAOExcepcion {
        List<BitacoraSistemaDTO> listaBitacora = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            while (conjuntoResultado.next()) {
                BitacoraSistemaDTO bitacora = new BitacoraSistemaDTO(
                        conjuntoResultado.getInt("idRegistro"),
                        conjuntoResultado.getString("RolUsuario"),
                        conjuntoResultado.getString("NombreUsuario"),
                        conjuntoResultado.getString("TipoEvento"),
                        conjuntoResultado.getTimestamp("Fecha_Hora").toLocalDateTime(),
                        conjuntoResultado.getString("Descripcion")
                );
                listaBitacora.add(bitacora);
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error al listar bitacora", sqlException);
            throw new DAOExcepcion("Error al listar las bitacoras: ", sqlException);
        }
        return listaBitacora;
    }
}