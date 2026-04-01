package logica.dto;

import logica.enums.TipoEstado;
import logica.enums.TipoTurno;

public class ProfesorDTO {
    private String numeroDePersonal;
    private TipoTurno turno;

    public ProfesorDTO(String numeroDePersonal, TipoTurno turno, TipoEstado estado) {
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
