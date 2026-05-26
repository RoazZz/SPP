package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.OrganizacionVinculadaDTO;
import java.util.List;

public interface OrganizacionVinculadaDAOInterfaz {
    boolean agregarOrganizacionVinculada(OrganizacionVinculadaDTO organizacionVinculada) throws DAOExcepcion;
    boolean actualizarOrganizacionVinculada (OrganizacionVinculadaDTO organizacionVinculada) throws DAOExcepcion;
    OrganizacionVinculadaDTO buscarOrganizacionVinculadaPorIdProyecto(String idOrganizacion) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    List<OrganizacionVinculadaDTO> listarOrganizacionesVinculadas() throws DAOExcepcion;
}
