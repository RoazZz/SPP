package logica.dto;

import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import logica.enums.TipoTurno;

public class ProfesorDTO extends UsuarioDTO {
    private String numeroDePersonal;
    private TipoTurno turno;

    public ProfesorDTO(int idUsuario, String nombre, String apellidoPaterno, String apellidoMaterno, String contrasenia, TipoEstado estado, TipoDeUsuario tipoDeUsuario, String numeroDePersonal, TipoTurno turno) {
        super(idUsuario, nombre, apellidoPaterno, apellidoMaterno, contrasenia, estado, tipoDeUsuario);
        this.numeroDePersonal = numeroDePersonal;
        this.turno = turno;
    }

    public String getNumeroDePersonal() {
        return numeroDePersonal;
    }

    public void setNumeroDePersonal(String numeroDePersonal) {
        this.numeroDePersonal = numeroDePersonal;
    }

    public TipoTurno getTurno() {
        return turno;
    }

    public void setTurno(TipoTurno turno) {
        this.turno = turno;
    }

}
