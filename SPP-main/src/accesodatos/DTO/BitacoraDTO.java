package accesodatos.DTO;

import java.time.LocalDateTime;

public class BitacoraDTO {
    private int idRegistro;
    private String matricula;
    private LocalDateTime fechaHora;

    public BitacoraDTO (int idRegistro, String matricula, LocalDateTime fechaHora){
        this.idRegistro = idRegistro;
        this.matricula = matricula;
        this.fechaHora = LocalDateTime.now();
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

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}

