package logica.dto;

public class PlanDeActividadesDTO {
    private int idplanActividades;
    private String matricula;
    private int idProyecto;
    private String descripcion;

    public PlanDeActividadesDTO(int idplanActividades, String matricula, int idProyecto, String descripcion) {
        this.idplanActividades = idplanActividades;
        this.matricula = matricula;
        this.idProyecto = idProyecto;
        this.descripcion = descripcion;
    }

    public int getIdplanActividades() {
        return idplanActividades;
    }

    public void setIdplanActividades(int idplanActividades) {
        this.idplanActividades = idplanActividades;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
