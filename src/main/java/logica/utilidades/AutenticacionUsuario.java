package logica.utilidades;

import excepciones.AutenticacionDeUsuarioExcepcion;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.AdministradorDAO;
import logica.dao.CoordinadorDAO;
import logica.dao.PracticanteDAO;
import logica.dao.ProfesorDAO;
import logica.dto.AdministradorDTO;
import logica.dto.UsuarioDTO;
import java.util.logging.Level;
import java.util.logging.Logger;
import static logica.utilidades.CifradorContraseña.verificarContrasenia;

public class AutenticacionUsuario {
    private static final Logger logger = Logger.getLogger(AutenticacionUsuario.class.getName());

    public UsuarioDTO autenticar(String Usuario, String contrasenia) throws DAOExcepcion, EntidadNoEncontradaExcepcion, AutenticacionDeUsuarioExcepcion {
        UsuarioDTO usuario = buscarUsuario(Usuario);
        if (!verificarContrasenia(contrasenia, usuario.getContrasenia())) {
            logger.log(Level.WARNING, "Intento de inicio de sesión fallido para el usuario: ", Usuario);
            throw new AutenticacionDeUsuarioExcepcion("La contraseña ingresada es incorrecta.");
        }

        logger.log(Level.INFO, "Usuario autenticado exitosamente: {0}", Usuario);
        return usuario;
    }

    private UsuarioDTO buscarUsuario(String usuario) throws DAOExcepcion, EntidadNoEncontradaExcepcion {

        try {
            return new PracticanteDAO().buscarPracticantePorMatricula(usuario);
        } catch (EntidadNoEncontradaExcepcion e) {
            logger.log(Level.INFO, "El usuario no es un practicante");
        } catch (DAOExcepcion e) {
            logger.log(Level.SEVERE, "Error al buscar practicante", e);
            throw new EntidadNoEncontradaExcepcion("Error al buscar el practicante." + e);
        }

        try {
            return new ProfesorDAO().buscarProfesorPorNumPersonal(usuario);
        } catch (EntidadNoEncontradaExcepcion e) {
            logger.log(Level.INFO, "El usuario no es un profesor");
        } catch (DAOExcepcion e) {
            logger.log(Level.SEVERE, "Error al buscar profesor", e);
            throw new EntidadNoEncontradaExcepcion("Error al buscar el profesor." + e);
        }

        try {
            return new CoordinadorDAO().buscarCoordinadorPorNumeroDePersonal(usuario);
        } catch (EntidadNoEncontradaExcepcion e) {
            logger.log(Level.INFO, "No es coordinador");
        } catch (DAOExcepcion e) {
            logger.log(Level.SEVERE, "Error al buscar coordinador", e);
            throw new EntidadNoEncontradaExcepcion("Error al buscar el coordinador." + e);
        }
        try{
            return new AdministradorDAO().buscarAdministradorPorNombre(usuario);
        }catch (EntidadNoEncontradaExcepcion e) {
            logger.log(Level.INFO, "No es coordinador");
        } catch (DAOExcepcion e) {
            logger.log(Level.SEVERE, "Error al buscar coordinador", e);
            throw new EntidadNoEncontradaExcepcion("Error al buscar el coordinador." + e);
        }

        logger.log(Level.INFO, "No se encontro ningún usuario");
        throw new EntidadNoEncontradaExcepcion("No existe usuario con el identificador: " + usuario);
    }
}