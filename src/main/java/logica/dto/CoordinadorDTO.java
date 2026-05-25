package logica.dto;

import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstadoUsuario;

public class CoordinadorDTO extends UsuarioDTO {
    private String numeroPersonal;

    public CoordinadorDTO(int idUsuario, String nombre, String apellidoPaterno, String apellidoMaterno, String contrasenia, TipoEstadoUsuario estado, TipoDeUsuario tipoDeUsuario, String numeroPersonal) {
        super(idUsuario, nombre, apellidoPaterno, apellidoMaterno, contrasenia, estado, tipoDeUsuario);
        this.numeroPersonal = numeroPersonal;
    }

    public String getNumeroPersonal() {
        return numeroPersonal;
    }

    public void setNumeroPersonal(String numeroPersonal) {
        this.numeroPersonal = numeroPersonal;
    }
}
