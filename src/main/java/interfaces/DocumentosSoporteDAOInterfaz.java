package interfaces;
import logica.dto.DocumentosSoporteDTO;

public interface DocumentosSoporteDAOInterfaz {
    public void agregarDocumentoSoporte(DocumentosSoporteDTO documento) throws Exception;
    public DocumentosSoporteDTO buscarDocumentoSoportePorId(int idDocumento) throws Exception;
    public void actualizarDocumentoSoporte(DocumentosSoporteDTO documento) throws Exception;
}
