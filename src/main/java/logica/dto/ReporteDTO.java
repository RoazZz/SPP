package logica.dto;

import logica.enums.TipoReporte;
import logica.enums.EstadoReporte;
import java.time.LocalDate;

public class ReporteDTO {
    private int idReporte;
    private TipoReporte tipoReporte;
    private LocalDate fecha;
    private String ruta;
    private EstadoReporte estado;

    public ReporteDTO(int idReporte, TipoReporte tipoReporte, LocalDate fecha, String ruta, EstadoReporte estado) {
        this.idReporte = idReporte;
        this.tipoReporte = tipoReporte;
        this.fecha = fecha;
        this.ruta = ruta;
        this.estado = estado;
    }

    public int getIdReporte(){
        return idReporte;
    }
    public void setIdReporte(int idReporte){
        this.idReporte = idReporte;
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
}