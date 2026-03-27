package logica.dto;

import java.time.LocalDate;

public class BitacoraPSPDTO {
    private int idBBitacora;
    private String matricula;
    private LocalDate fecha;

    public BitacoraPSPDTO(int idBBitacora, String matricula, LocalDate fecha) {
        this.idBBitacora = idBBitacora;
        this.matricula = matricula;
        this.fecha = fecha;
    }

    public int getIdBBitacora() {
        return idBBitacora;
    }

    public void setIdBBitacora(int idBBitacora) {
        this.idBBitacora = idBBitacora;
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
