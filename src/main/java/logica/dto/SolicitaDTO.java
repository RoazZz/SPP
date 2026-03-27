package logica.dto;

import logica.enums.TipoEstadoProyecto;

public class SolicitaDTO {
    private String matricula;
    private int idProyecto;
    private TipoEstadoProyecto tipoEstadoProyecto;
    private String periodo;

    public SolicitaDTO(String matricula, int idProyecto, TipoEstadoProyecto tipoEstadoProyecto, String periodo) {
        this.matricula = matricula;
        this.idProyecto = idProyecto;
        this.tipoEstadoProyecto = tipoEstadoProyecto;
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

    public TipoEstadoProyecto getEstadoProyecto() {
        return tipoEstadoProyecto;
    }

    public void setEstadoProyecto(TipoEstadoProyecto tipoEstadoProyecto) {
        this.tipoEstadoProyecto = tipoEstadoProyecto;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }
}
