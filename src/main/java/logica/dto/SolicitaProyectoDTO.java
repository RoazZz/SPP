package logica.dto;

import logica.enums.TipoEstadoSolicitud;

public class SolicitaProyectoDTO {
    private String matricula;
    private int idProyecto;
    private TipoEstadoSolicitud tipoEstadoSolicitud;
    private String periodo;

    public SolicitaProyectoDTO(String matricula, int idProyecto, TipoEstadoSolicitud tipoEstadoSolicitud, String periodo) {
        this.matricula = matricula;
        this.idProyecto = idProyecto;
        this.tipoEstadoSolicitud = tipoEstadoSolicitud;
        this.periodo = periodo;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public TipoEstadoSolicitud getEstadoProyecto() {
        return tipoEstadoSolicitud;
    }

    public void setEstadoProyecto(TipoEstadoSolicitud tipoEstadoSolicitud) {
        this.tipoEstadoSolicitud = tipoEstadoSolicitud;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }
}
