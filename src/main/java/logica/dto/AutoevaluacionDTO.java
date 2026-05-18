package logica.dto;

import java.math.BigDecimal;

public class AutoevaluacionDTO {
    private int idAutoevaluacion;
    private String matricula;
    private BigDecimal calificacion;
    private String comentarios;

    public AutoevaluacionDTO(int idAutoevaluacion, String matricula, BigDecimal calificacion, String comentarios) {
        this.idAutoevaluacion = idAutoevaluacion;
        this.matricula = matricula;
        this.calificacion = calificacion;
        this.comentarios = comentarios;
    }

    public int getIdAutoevaluacion() {
        return idAutoevaluacion;
    }

    public void setIdAutoevaluacion(int idAutoevaluacion) {
        this.idAutoevaluacion = idAutoevaluacion;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public BigDecimal getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(BigDecimal calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }
}
