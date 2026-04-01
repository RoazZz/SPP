package interfaces;

import logica.dto.OrganizacionVinculadaDTO;
import java.util.List;

public interface OrganizacionVinculadaDAOInterfaz {
    public void agregarOrganizacionVinculada(OrganizacionVinculadaDTO organizacionVinculada) throws Exception;
    public void actualizarOrganizacionVinculada (OrganizacionVinculadaDTO organizacionVinculada) throws Exception;
    public OrganizacionVinculadaDTO buscarOrganizacionVinculadaPorIdProyecto(String idOrganizacion) throws Exception;
    public List<OrganizacionVinculadaDTO> listarOrganizacionesVinculadas() throws Exception;
}
