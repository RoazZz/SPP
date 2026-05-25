package logica.dto;

import java.time.LocalDate;

public class ActividadDTO {
    private int idActividad;
    private String matricula;
    private String nombre;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaCierre;

    public ActividadDTO(int idActividad, String matricula, String nombre, String descripcion, LocalDate fechaInicio, LocalDate fechaCierre) {
        this.idActividad = idActividad;
        this.matricula = matricula;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaCierre = fechaCierre;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDate fechaCierre) {
        this.fechaCierre = fechaCierre;
    }
}
