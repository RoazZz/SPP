package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.ValidacionExcepcion;

public class UsuarioControlador {

    private static final int LONGITUD_MINIMA_CONTRASENIA = 8;

    public static void validarCamposComunes(String nombre, String apellidoP, String apellidoM, String contrasenia) throws ValidacionExcepcion {
        StringBuilder errores = new StringBuilder();

        if (nombre.trim().isEmpty()) {
            errores.append("El campo nombre no puede estar vacío.\n");
        }
        if (apellidoP.trim().isEmpty()) {
            errores.append("El campo apellido paterno no puede estar vacío.\n");
        }
        if (apellidoM.trim().isEmpty()) {
            errores.append("El campo apellido materno no puede estar vacío.\n");
        }
        if (contrasenia.trim().isEmpty()) {
            errores.append("El campo contraseña no puede estar vacío.\n");
        } else if (contrasenia.length() < LONGITUD_MINIMA_CONTRASENIA) {
            errores.append("La contraseña debe tener al menos "
                    + LONGITUD_MINIMA_CONTRASENIA + " caracteres.\n");
        }

        if (errores.length() > 0) {
            throw new ValidacionExcepcion(errores.toString());
        }
    }
}