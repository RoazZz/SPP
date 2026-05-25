package logica.dto;

import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstadoUsuario;
import logica.enums.TipoTurno;

public class ProfesorDTO extends UsuarioDTO {
    private String numeroDePersonal;
    private TipoTurno turno;
    private int idSeccion;

    public ProfesorDTO(int idUsuario, String nombre, String apellidoPaterno, String apellidoMaterno,
                       String contrasenia, TipoEstadoUsuario estado, TipoDeUsuario tipoDeUsuario,
                       String numeroDePersonal, TipoTurno turno, int idSeccion) {
        super(idUsuario, nombre, apellidoPaterno, apellidoMaterno, contrasenia, estado, tipoDeUsuario);
        this.numeroDePersonal = numeroDePersonal;
        this.turno = turno;
        this.idSeccion = idSeccion;
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

    public int getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }
}