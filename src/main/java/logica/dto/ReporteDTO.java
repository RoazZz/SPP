package logica.dto;

import java.time.LocalDate;

public class ReporteDTO {
    private int idReporte;
    private String matricula;
    private String idOrganizacion;
    private String tipoReporte;
    private String observaciones;
    private LocalDate fecha;
    private String estado;

    public ReporteDTO(int idReporte, String matricula, String idOrganizacion, String tipoReporte, String observaciones, LocalDate fecha, String estado) {
        this.idReporte = idReporte;
        this.matricula = matricula;
        this.idOrganizacion = idOrganizacion;
        this.tipoReporte = tipoReporte;
        this.observaciones = observaciones;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getIdOrganizacion() {
        return idOrganizacion;
    }

    public void setIdOrganizacion(String idOrganizacion) {
        this.idOrganizacion = idOrganizacion;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(String tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
