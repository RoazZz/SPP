package interfaces;
import logica.dto.DocumentosSoporteDTO;

public interface DocumentosSoporteDAOInterfaz {
    public void agregar(DocumentosSoporteDTO documento) throws Exception;
    public DocumentosSoporteDTO buscarPorId(int idDocumento) throws Exception;
    public void actualizar(DocumentosSoporteDTO documento) throws Exception;
}
