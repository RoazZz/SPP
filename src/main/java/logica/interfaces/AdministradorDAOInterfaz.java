package logica.interfaces;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dto.AdministradorDTO;

import java.util.List;

public interface AdministradorDAOInterfaz {
    public AdministradorDTO agregarAdministrador(AdministradorDTO administrador) throws DAOExcepcion;
    public AdministradorDTO buscarAdministradorPorId(int idAdmin) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public AdministradorDTO buscarAdministradorPorNombre(String nombre) throws DAOExcepcion, EntidadNoEncontradaExcepcion;
    public List<AdministradorDTO> listarAdministradores() throws DAOExcepcion;
    public boolean existeAlgunAdministrador() throws DAOExcepcion;
}
