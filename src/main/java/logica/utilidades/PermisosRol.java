package logica.utilidades;

import logica.enums.TipoDeUsuario;

import java.util.List;

public class PermisosRol {
    private final TipoDeUsuario rol;

    public PermisosRol(TipoDeUsuario rol) {
        this.rol = rol;
    }

    public boolean puedeAgregarCoordinador(){
        return rol == TipoDeUsuario.ADMIN;
    }

    public boolean puedeAgregarProfesor(){
        return rol == TipoDeUsuario.ADMIN;
    }

    public boolean puedeAgregarPracticante(){
        return rol == TipoDeUsuario.COORDINADOR;
    }

    public boolean puedeAgregarUsuario(){
        return rol == TipoDeUsuario.COORDINADOR || rol == TipoDeUsuario.ADMIN;
    }

    public List<TipoDeUsuario> tiposVisibles() {
        return switch (rol) {
            case ADMIN -> List.of(TipoDeUsuario.PROFESOR, TipoDeUsuario.COORDINADOR);
            case COORDINADOR -> List.of(TipoDeUsuario.PROFESOR);
            default -> List.of();
        };
    }

}
