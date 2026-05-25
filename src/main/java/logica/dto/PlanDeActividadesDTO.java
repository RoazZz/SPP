package logica.dto;

public class PlanDeActividadesDTO {
    private int idPlanActividades;
    private String matricula;
    private int idProyecto;
    private String descripcion;

    public PlanDeActividadesDTO(int idPlanActividades, String matricula, int idProyecto, String descripcion) {
        this.idPlanActividades = idPlanActividades;
        this.matricula = matricula;
        this.idProyecto = idProyecto;
        this.descripcion = descripcion;
    }

    public int getIdPlanActividades() {
        return idPlanActividades;
    }

    public void setIdPlanActividades(int idPlanActividades) {
        this.idPlanActividades = idPlanActividades;
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
