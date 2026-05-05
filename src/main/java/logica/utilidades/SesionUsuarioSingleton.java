package logica.utilidades;

import logica.dto.UsuarioDTO;

public class SesionUsuarioSingleton {

    private static SesionUsuarioSingleton sesionActiva;
    private UsuarioDTO usuarioEnSesion;

    public static SesionUsuarioSingleton obtenerInstancia() {
        if (sesionActiva == null) {
            sesionActiva = new SesionUsuarioSingleton();
        }
        return sesionActiva;
    }

    public void iniciarSesion(UsuarioDTO usuario) {
        this.usuarioEnSesion = usuario;
    }

    public UsuarioDTO obtenerUsuarioActual() {
        return usuarioEnSesion;
    }

    public void cerrarSesion() {
        usuarioEnSesion = null;
    }
}