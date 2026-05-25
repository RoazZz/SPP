package logica.dto;

import java.time.LocalDate;

public class BitacoraPSPDTO {
    private int idBitacora;
    private String matricula;
    private LocalDate fecha;

    public BitacoraPSPDTO(int idBitacora, String matricula, LocalDate fecha) {
        this.idBitacora = idBitacora;
        this.matricula = matricula;
        this.fecha = fecha;
    }

    public int getIdBBitacora() {
        return idBitacora;
    }

    public void setIdBBitacora(int idBBitacora) {
        this.idBitacora = idBBitacora;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
