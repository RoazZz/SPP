package logica.dto;

import logica.enums.EstadoReporte;
import logica.enums.TipoReporte;

import java.time.LocalDate;

public class ReporteDTO {
    private int idReporte;
    private int idUsuario;
    private TipoReporte tipoReporte;
    private LocalDate fecha;
    private String ruta;
    private EstadoReporte estado;
    private String mes;

    // Se declara como Double y no como double para cuando la
    // calificación del reporte necesite ser null (aún no ha sido calificado).
    private Double calificacion;

    public ReporteDTO(int idReporte, int idUsuario, TipoReporte tipoReporte, LocalDate fecha,
                      String ruta, EstadoReporte estado, String mes, Double calificacion) {
        this.idReporte = idReporte;
        this.idUsuario = idUsuario;
        this.tipoReporte = tipoReporte;
        this.fecha = fecha;
        this.ruta = ruta;
        this.estado = estado;
        this.mes = mes;
        this.calificacion = calificacion;
    }

    public int getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(int idReporte) {
        this.idReporte = idReporte;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public TipoReporte getTipoReporte() {
        return tipoReporte;
    }

    public void setTipoReporte(TipoReporte tipoReporte) {
        this.tipoReporte = tipoReporte;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public EstadoReporte getEstado() {
        return estado;
    }

    public void setEstado(EstadoReporte estado) {
        this.estado = estado;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }
}