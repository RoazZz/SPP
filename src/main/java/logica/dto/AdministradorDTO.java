package logica.dto;

import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;

public class AdministradorDTO extends UsuarioDTO{

    public AdministradorDTO(int idUsuario, String nombre, String apellidoP, String apellidoM, String contrasenia, TipoEstado tipoEstado, TipoDeUsuario tipoDeUsuario) {
        super(idUsuario, nombre, apellidoP, apellidoM, contrasenia, tipoEstado, tipoDeUsuario);
    }

}
