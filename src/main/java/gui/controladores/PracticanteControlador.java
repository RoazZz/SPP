package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.ReglaDeNegocioExcepcion;
import logica.dao.PracticanteDAO;
import logica.dao.UsuarioDAO;
import logica.dto.PracticanteDTO;
import logica.enums.GeneroDelPracticante;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PracticanteControlador {

    private static final int LONGITUD_MINIMA_CONTRASENIA = 8;
    private final PracticanteDAO practicanteDAO;
    private final UsuarioDAO usuarioDAO;

    private static final Logger LOGGER = Logger.getLogger(PracticanteControlador.class.getName());

    public PracticanteControlador() throws DAOExcepcion {
        this.practicanteDAO = new PracticanteDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    public PracticanteDTO construirPracticanteDTO(int id, String nombre, String apellidoP, String apellidoM,
                                                  String contrasenia, String matricula, int idSeccion,
                                                  String semestre, GeneroDelPracticante genero,
                                                  int edad, boolean lenguaIndigena) {
        return new PracticanteDTO(
                id, nombre, apellidoP, apellidoM, contrasenia,
                TipoEstado.ACTIVO, TipoDeUsuario.PRACTICANTE,
                matricula, idSeccion, semestre, genero, edad, lenguaIndigena
        );
    }

    public void procesarGuardadoPracticante(PracticanteDTO practicanteDTO, boolean modoEdicion)
            throws DAOExcepcion, ReglaDeNegocioExcepcion {
        validarCamposPracticante(practicanteDTO.getMatricula(), practicanteDTO.getIdSeccion(),
                practicanteDTO.getSemestre(), practicanteDTO.getGeneroDelPracticante(),
                practicanteDTO.getEdad(), practicanteDTO.getContrasenia());

        if (!modoEdicion) {
            if (practicanteDAO.existePracticanteConMatricula(practicanteDTO.getMatricula())) {
                throw new ReglaDeNegocioExcepcion("Existe un registre de practicante con la matrícula: " + practicanteDTO.getMatricula());
            }
            LOGGER.log(Level.INFO, "Guardando practicante con matricula:", practicanteDTO.getMatricula());
            practicanteDAO.agregarPracticante(practicanteDTO);
        } else {
            LOGGER.log(Level.INFO, "Actualizando practicante con matricula:", practicanteDTO.getMatricula());
            usuarioDAO.actualizarUsuario(practicanteDTO);
            practicanteDAO.actualizarPracticante(practicanteDTO);
        }
    }

    private void validarCamposPracticante(String matricula, int idSeccion, String semestre,
                                          GeneroDelPracticante genero, int edad, String contrasenia)
            throws ReglaDeNegocioExcepcion {
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new ReglaDeNegocioExcepcion("La matrícula no puede estar vacía.");
        }
        if (idSeccion <= 0) {
            throw new ReglaDeNegocioExcepcion("La sección debe ser un número mayor a 0.");
        }
        if (semestre == null || semestre.trim().isEmpty()) {
            throw new ReglaDeNegocioExcepcion("El semestre no puede estar vacío.");
        }
        if (genero == null) {
            throw new ReglaDeNegocioExcepcion("Debe seleccionar un género.");
        }
        if (edad <= 0) {
            throw new ReglaDeNegocioExcepcion("La edad debe ser un número mayor a 0.");
        }
        if (contrasenia == null || contrasenia.length() < LONGITUD_MINIMA_CONTRASENIA) {
            throw new ReglaDeNegocioExcepcion("La contraseña debe tener al menos " + LONGITUD_MINIMA_CONTRASENIA + " caracteres.");
        }
    }
}