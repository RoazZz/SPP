package interfaces;

import excepciones.DAOExcepcion;
import logica.dto.OrganizacionVinculadaDTO;
import java.util.List;

public interface OrganizacionVinculadaDAOInterfaz {
    public void agregarOrganizacionVinculada(OrganizacionVinculadaDTO organizacionVinculada) throws DAOExcepcion;
    public void actualizarOrganizacionVinculada (OrganizacionVinculadaDTO organizacionVinculada) throws DAOExcepcion;
    public OrganizacionVinculadaDTO buscarOrganizacionVinculadaPorIdProyecto(String idOrganizacion) throws DAOExcepcion;
    public List<OrganizacionVinculadaDTO> listarOrganizacionesVinculadas() throws DAOExcepcion;
}
