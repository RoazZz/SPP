package logica.dto;
import java.time.LocalDateTime;

public class BitacoraSistemaDTO {
    private int idRegistro;
    private String rolUsuario;
    private String nombreUsuario;
    private String tipoEvento;
    private LocalDateTime fechaHora;
    private String descripcionEvento;

    public BitacoraSistemaDTO(int idRegistro, String rolUsuario, String nombreUsuario,
                              String tipoEvento, LocalDateTime fechaHora, String descripcionEvento) {
        this.idRegistro = idRegistro;
        this.rolUsuario = rolUsuario;
        this.nombreUsuario = nombreUsuario;
        this.tipoEvento = tipoEvento;
        this.fechaHora = fechaHora;
        this.descripcionEvento = descripcionEvento;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(String rolUsuario) {
        this.rolUsuario = rolUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
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