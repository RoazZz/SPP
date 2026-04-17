package logica.dto;

import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;

public class AdministradorDTO extends UsuarioDTO{
    private int idAdministrador;

    public AdministradorDTO(int idUsuario, String nombre, String apellidoP, String apellidoM, String contrasenia, TipoEstado tipoEstado, TipoDeUsuario tipoDeUsuario, int idAdministrador) {
        super(idUsuario, nombre, apellidoP, apellidoM, contrasenia, tipoEstado, tipoDeUsuario);
        this.idAdministrador = idAdministrador;
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

}
