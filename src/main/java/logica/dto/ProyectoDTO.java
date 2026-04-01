package logica.dto;

public class ProyectoDTO {
    private int idProyecto;
    private String idOrganizacion;
    private String numeroDePersonal;
    private String nombre;
    private String descripcion;

    public ProyectoDTO (int idProyecto, String idOrganizacion, String numeroPersonal, String nombre, String descripcion){
        this.idProyecto = idProyecto;
        this.idOrganizacion = idOrganizacion;
        this.numeroDePersonal = numeroPersonal;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getIdOrganizacion() {
        return idOrganizacion;
    }

    public void setIdOrganizacion(String idOrganizacion) {
        this.idOrganizacion = idOrganizacion;
    }

    public String getNumeroDePersonal() {
        return numeroDePersonal;
    }

    public void setNumeroDePersonal(String numeroDePersonal) {
        this.numeroDePersonal = numeroDePersonal;
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

}
