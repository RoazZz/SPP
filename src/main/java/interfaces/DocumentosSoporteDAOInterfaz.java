package interfaces;
import logica.dto.DocumentosSoporteDTO;

public interface DocumentosSoporteDAOInterfaz {
    void agregar(DocumentosSoporteDTO documento) throws Exception;
    DocumentosSoporteDTO buscarPorId(int idDocumento) throws Exception;
    void actualizar(DocumentosSoporteDTO documento) throws Exception;
}
