package logica.interfaces;
import excepciones.DAOExcepcion;
import logica.dto.DocumentosSoporteDTO;

import java.util.List;

public interface DocumentosSoporteDAOInterfaz {
    DocumentosSoporteDTO agregarDocumentoSoporte(DocumentosSoporteDTO documento) throws DAOExcepcion;
    DocumentosSoporteDTO buscarDocumentoSoportePorId(int idDocumento) throws DAOExcepcion;
    boolean actualizarDocumentoSoporte(DocumentosSoporteDTO documento) throws DAOExcepcion;
    List<DocumentosSoporteDTO> obtenerTodosLosDocumentosSoporte() throws DAOExcepcion;
}
