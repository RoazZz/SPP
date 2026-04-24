package interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.OrganizacionVinculadaDTO;
import java.util.List;

public interface OrganizacionVinculadaDAOInterfaz {
    public boolean agregarOrganizacionVinculada(OrganizacionVinculadaDTO organizacionVinculada) throws DAOExcepcion;
    public boolean actualizarOrganizacionVinculada (OrganizacionVinculadaDTO organizacionVinculada) throws DAOExcepcion;
    public OrganizacionVinculadaDTO buscarOrganizacionVinculadaPorIdProyecto(String idOrganizacion) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<OrganizacionVinculadaDTO> listarOrganizacionesVinculadas() throws DAOExcepcion;
}
