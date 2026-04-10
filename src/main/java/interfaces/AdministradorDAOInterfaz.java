package interfaces;
import excepciones.DAOExcepcion;
import logica.dto.AdministradorDTO;

public interface AdministradorDAOInterfaz {
        public void agregarAdministrador(AdministradorDTO admin) throws DAOExcepcion;
}
