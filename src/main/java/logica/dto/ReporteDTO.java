package logica.dto;

import logica.enums.TipoReporte;
import logica.enums.EstadoReporte;
import java.time.LocalDate;

public class ReporteDTO {
    private int idReporte;
    private int idUsuario;
    private TipoReporte tipoReporte;
    private LocalDate fecha;
    private String ruta;
    private EstadoReporte estado;
    private String mes;
    private String hashArchivo;
    private String hashContenido;

    public ReporteDTO(int idReporte, int idUsuario, TipoReporte tipoReporte, LocalDate fecha, String ruta, EstadoReporte estado, String mes, String hashArchivo, String hashContenido) {
        this.idReporte = idReporte;
        this.idUsuario = idUsuario;
        this.tipoReporte = tipoReporte;
        this.fecha = fecha;
        this.ruta = ruta;
        this.estado = estado;
        this.mes = mes;
        this.hashArchivo = hashArchivo;
        this.hashContenido = hashContenido;
    }

    public String getHashArchivo() {
        return hashArchivo;
    }
    public void setHashArchivo(String hashArchivo) {
        this.hashArchivo = hashArchivo;
    }
    public String getHashContenido() {
        return hashContenido;
    }
    public void setHashContenido(String hashContenido) {
        this.hashContenido = hashContenido;
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
}