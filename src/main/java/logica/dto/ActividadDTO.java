package logica.dto;

import java.time.LocalDate;

public class ActividadDTO {

    private int idActividad;
    private String matricula;
    private String titulo;
    private String descripcion;
    private LocalDate fecha;
    private String rutaDocumento;

    public ActividadDTO() {
    }

    public ActividadDTO(int idActividad, String matricula, String titulo, String descripcion, LocalDate fecha, String rutaDocumento) {
        this.idActividad = idActividad;
        this.matricula = matricula;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.rutaDocumento = rutaDocumento;
    }

    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getRutaDocumento() {
        return rutaDocumento;
    }

    public void setRutaDocumento(String rutaDocumento) {
        this.rutaDocumento = rutaDocumento;
    }
}