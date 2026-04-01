package logica.dto;

import java.time.LocalDateTime;

public class BitacoraDTO {
    private int idRegistro;
    private String matricula;
    private String tipoEvento;
    private LocalDateTime fechaHora;
    private String descripcionEvento;

    public BitacoraDTO(int idRegistro, String matricula, String tipoEvento, LocalDateTime fechaHora, String descripcionEvento) {
        this.idRegistro = idRegistro;
        this.matricula = matricula;
        this.tipoEvento = tipoEvento;
        this.fechaHora = LocalDateTime.now();
        this.descripcionEvento = descripcionEvento;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getDescripcionEvento() {
        return descripcionEvento;
    }

    public void setDescripcionEvento(String descripcionEvento) {
        this.descripcionEvento = descripcionEvento;
    }
}



