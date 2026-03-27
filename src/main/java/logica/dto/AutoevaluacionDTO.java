package logica.dto;

import java.math.BigDecimal;

public class AutoevaluacionDTO {
    private int idAutoevalaucion;
    private String matricula;
    private BigDecimal calificacion;
    private String comentarios;

    public AutoevaluacionDTO(int idAutoevalaucion, String matricula, BigDecimal calificacion, String comentarios) {
        this.idAutoevalaucion = idAutoevalaucion;
        this.matricula = matricula;
        this.calificacion = calificacion;
        this.comentarios = comentarios;
    }

    public int getIdAutoevalaucion() {
        return idAutoevalaucion;
    }

    public void setIdAutoevalaucion(int idAutoevalaucion) {
        this.idAutoevalaucion = idAutoevalaucion;
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
