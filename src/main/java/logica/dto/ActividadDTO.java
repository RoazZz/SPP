package logica.dto;

import java.sql.Date;

public class ActividadDTO {
    private int idActividad;
    private String matricula;
    private String nombre;
    private String descripcion;
    private Date fecha;

    public ActividadDTO(int idActividad, String matricula, String nombre, String descripcion, Date fecha){
        this.idActividad = idActividad;
        this.matricula = matricula;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
