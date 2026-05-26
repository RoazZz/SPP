package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.AdministradorDTO;

import java.util.List;

public interface AdministradorDAOInterfaz {
         AdministradorDTO agregarAdministrador(AdministradorDTO administrador) throws DAOExcepcion;
         AdministradorDTO buscarAdministradorPorId(int idAdmin) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
         AdministradorDTO buscarAdministradorPorNombre(String nombre) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
        List<AdministradorDTO> listarAdministradores() throws DAOExcepcion;
}
