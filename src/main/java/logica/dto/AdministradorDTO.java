package logica.dto;

import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstadoUsuario;

public class AdministradorDTO extends UsuarioDTO {
    private int idAdministrador;

    public AdministradorDTO(int idUsuario, String nombre, String apellidoPaterno, String apellidoMaterno, String contrasenia, TipoEstadoUsuario tipoEstadoUsuario, TipoDeUsuario tipoDeUsuario, int idAdministrador) {
        super(idUsuario, nombre, apellidoPaterno, apellidoMaterno, contrasenia, tipoEstadoUsuario, tipoDeUsuario);
        this.idAdministrador = idAdministrador;
    }

    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

}
