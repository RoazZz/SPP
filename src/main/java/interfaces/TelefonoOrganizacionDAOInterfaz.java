package interfaces;


import logica.dto.TelefonoOrganizacionDTO;

import java.util.List;

public interface TelefonoOrganizacionDAOInterfaz {
    public void agregarTelefonoOrganizacion(TelefonoOrganizacionDTO telefonoOrganizacion) throws Exception;
    public void actualizarTelefonoOrganizacionVinculada(TelefonoOrganizacionDTO telefonoOrganizaciono) throws Exception;
    public TelefonoOrganizacionDTO buscarProyectoPorIdOrganizacion(String idOrganizacion) throws Exception;
    public List<TelefonoOrganizacionDTO> listarTelefonosPorOrganizacion() throws Exception;
}
