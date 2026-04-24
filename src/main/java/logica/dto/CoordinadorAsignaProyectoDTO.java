package logica.dto;

import logica.enums.EstadoAsignacionProyecto;

public class CoordinadorAsignaProyectoDTO {
    private String numeroDePersonal;
    private int idProyecto;
    private EstadoAsignacionProyecto tipoEstado;

    public CoordinadorAsignaProyectoDTO(String numeroDePersonal, int idProyecto, EstadoAsignacionProyecto tipoEstado) {
        this.numeroDePersonal = numeroDePersonal;
        this.idProyecto = idProyecto;
        this.tipoEstado = tipoEstado;
    }

    public String getNumeroDePersonal() {
        return numeroDePersonal;
    }

    public void setNumeroDePersonal(String numeroDePersonal) {
        this.numeroDePersonal = numeroDePersonal;
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }


    public EstadoAsignacionProyecto getTipoEstado() {
        return tipoEstado;
    }

    public void setEstado(EstadoAsignacionProyecto tipoEstado) {
        this.tipoEstado = tipoEstado;
    }
}