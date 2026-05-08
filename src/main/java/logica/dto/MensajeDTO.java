package logica.dto;

import java.time.LocalDateTime;

public class MensajeDTO {
    private int idMensaje;
    private int idBuzonOrigen;
    private int idBuzonDestino;
    private String asunto;
    private String contenido;
    private LocalDateTime fecha;
    private boolean leido;
    private LocalDateTime fechaLectura;

    public MensajeDTO(int idMensaje, int idBuzonOrigen, int idBuzonDestino,
                      String asunto, String contenido, LocalDateTime fecha,
                      boolean leido, LocalDateTime fechaLectura) {
        this.idMensaje = idMensaje;
        this.idBuzonOrigen = idBuzonOrigen;
        this.idBuzonDestino = idBuzonDestino;
        this.asunto = asunto;
        this.contenido = contenido;
        this.fecha = fecha;
        this.leido = leido;
        this.fechaLectura = fechaLectura;
    }

    public MensajeDTO(int idBuzonOrigen, int idBuzonDestino,
                      String asunto, String contenido) {
        this.idBuzonOrigen = idBuzonOrigen;
        this.idBuzonDestino = idBuzonDestino;
        this.asunto = asunto;
        this.contenido = contenido;
    }

    public int getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(int idMensaje) {
        this.idMensaje = idMensaje;
    }

    public int getIdBuzonOrigen() {
        return idBuzonOrigen;
    }

    public void setIdBuzonOrigen(int idBuzonOrigen) {
        this.idBuzonOrigen = idBuzonOrigen;
    }

    public int getIdBuzonDestino() {
        return idBuzonDestino;
    }

    public void setIdBuzonDestino(int idBuzonDestino) {
        this.idBuzonDestino = idBuzonDestino;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }

    public LocalDateTime getFechaLectura() {
        return fechaLectura;
    }

    public void setFechaLectura(LocalDateTime fechaLectura) {
        this.fechaLectura = fechaLectura;
    }

}