package interfaces;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.AdministradorDTO;

public interface AdministradorDAOInterfaz {
        public AdministradorDTO agregarAdministrador(AdministradorDTO admin) throws DAOExcepcion;
        public AdministradorDTO buscarAdministradorPorId(int idAdmin) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
}
