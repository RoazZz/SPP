package interfaces;
import excepciones.DAOExcepcion;
import logica.dto.DocumentosSoporteDTO;

import java.util.List;

public interface DocumentosSoporteDAOInterfaz {
    public void agregarDocumentoSoporte(DocumentosSoporteDTO documento) throws DAOExcepcion;
    public DocumentosSoporteDTO buscarDocumentoSoportePorId(int idDocumento) throws DAOExcepcion;
    public void actualizarDocumentoSoporte(DocumentosSoporteDTO documento) throws DAOExcepcion;
    public List<DocumentosSoporteDTO> obtenerTodosLosDocumentosSoporte() throws DAOExcepcion;
}
