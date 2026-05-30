package logica.dao;
import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.BitacoraPSPDAOInterfaz;
import logica.dto.BitacoraPSPDTO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BitacoraPSPDAO implements BitacoraPSPDAOInterfaz {
    private static final String SQL_INSERT = "INSERT INTO bitacorapsp(Matricula, Fecha) VALUES (?, ?)"; //FALTA RUTA O NOMBRE DEL ARCHIVO
    private static final String SQL_SELECT_BY_IDBITACORA = "SELECT * FROM bitacorapsp WHERE idBitacoraPSP = ?";
    private static final String SQL_UPDATE = "UPDATE bitacorapsp SET Matricula = ?, Fecha = ? WHERE idBitacoraPSP = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM bitacorapsp";

    private Connection conexion;
    private static final Logger REGISTRADOR = Logger.getLogger(BitacoraPSPDAO.class.getName());

    public BitacoraPSPDAO() throws DAOExcepcion {
        try{
            this.conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        } catch (IOException ioException){
            REGISTRADOR.log(Level.SEVERE, "Error al leer archivo de cofniguración", ioException);
            throw new DAOExcepcion("Error de configuracion", ioException);
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error de conexion SQL en BitacoraPSPDAO", sqlException);
            throw new DAOExcepcion("Error de base de datos", sqlException);
        }
    }

    @Override
    public BitacoraPSPDTO agregarBitacoraPSP(BitacoraPSPDTO bitacora) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            sentenciaPreparada.setString(1, bitacora.getMatricula());
            sentenciaPreparada.setDate(2, java.sql.Date.valueOf(bitacora.getFecha()));
            sentenciaPreparada.executeUpdate();

            try (ResultSet conjuntoResultado = sentenciaPreparada.getGeneratedKeys()) {
                if (conjuntoResultado.next()) {
                    bitacora.setIdBBitacora(conjuntoResultado.getInt(1));
                }
            }
            REGISTRADOR.log(Level.INFO, "Bitacora PSP agregada con éxito. ID " + bitacora.getIdBBitacora());
            return bitacora;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al agregar bitacora PSP", sqlException);
            throw new DAOExcepcion("Error al agregar bitácora PSP", sqlException);
        }
    }

    @Override
    public BitacoraPSPDTO buscarBitacoraPSPPorId(int idBitacora) throws DAOExcepcion, EntidadNoEncontradaExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_BY_IDBITACORA)) {
            sentenciaPreparada.setInt(1, idBitacora);
            try (ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
                if (conjuntoResultado.next()) {
                    return new BitacoraPSPDTO(
                            conjuntoResultado.getInt("idBitacoraPSP"),
                            conjuntoResultado.getString("Matricula"),
                            conjuntoResultado.getDate("Fecha").toLocalDate()
                    );
                } else{
                    REGISTRADOR.log(Level.WARNING, "No se encontró bitacoraPSP con ID " + idBitacora);
                    throw new EntidadNoEncontradaExcepcion("BitacoraPSP no encontrado con ID " + idBitacora);
                }
            }
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al buscar bitacora PSP por ID: " + idBitacora, sqlException);
            throw new DAOExcepcion("Error al buscar bitácora PSP por ID", sqlException);
        }
    }

    @Override
    public boolean actualizarBitacoraPSP(BitacoraPSPDTO bitacora) throws DAOExcepcion {
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_UPDATE)) {
                sentenciaPreparada.setString(1, bitacora.getMatricula());
                sentenciaPreparada.setDate(2, java.sql.Date.valueOf(bitacora.getFecha()));
                sentenciaPreparada.setInt(3, bitacora.getIdBBitacora());
                int filasAfectadas = sentenciaPreparada.executeUpdate();
                if (filasAfectadas > 0){
                    REGISTRADOR.log(Level.INFO, "Bitacora PSP actualizada con éxito. ID " + bitacora.getIdBBitacora());
                    return true;
                } else{
                    REGISTRADOR.log(Level.WARNING, "No se encontró BitacoraPSP para actualizar con ID " + bitacora.getIdBBitacora());
                    throw new EntidadNoEncontradaExcepcion("BitacoraPSP no encontrado para actualizar con ID " + bitacora.getIdBBitacora());
                }
            } catch (SQLException sqlException) {
                REGISTRADOR.log(Level.SEVERE, "Error SQL al actualizar bitacora PSP", sqlException);
                throw new DAOExcepcion("Error al actualizar bitácora PSP", sqlException);
        }
    }

    public List<BitacoraPSPDTO> listarBitacorasPSP() throws DAOExcepcion{
        List<BitacoraPSPDTO> listaBitacorasPSP = new ArrayList<>();
        try (PreparedStatement sentenciaPreparada = conexion.prepareStatement(SQL_SELECT_ALL);
             ResultSet conjuntoResultado = sentenciaPreparada.executeQuery()) {
            while (conjuntoResultado.next()) {
                BitacoraPSPDTO bitacora = new BitacoraPSPDTO(
                        conjuntoResultado.getInt("idBitacoraPSP"),
                        conjuntoResultado.getString("Matricula"),
                        conjuntoResultado.getDate("Fecha").toLocalDate()
                );
                listaBitacorasPSP.add(bitacora);
            }
            return listaBitacorasPSP;
        } catch (SQLException sqlException) {
            REGISTRADOR.log(Level.SEVERE, "Error SQL al listar bitacoras PSP", sqlException);
            throw new DAOExcepcion("Error al listar bitácoras PSP", sqlException);
        }
    }
}
