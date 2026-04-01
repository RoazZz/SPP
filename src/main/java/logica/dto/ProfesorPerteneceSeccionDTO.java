package logica.dto;

public class ProfesorPerteneceSeccionDTO {
    private String numeroDePersonal;
    private int idSeccion;

    public ProfesorPerteneceSeccionDTO(String numeroDePersonal, int idSeccion){
        this.numeroDePersonal = numeroDePersonal;
        this.idSeccion = idSeccion;
    }

    public String getNumeroDePersonal() {
        return numeroDePersonal;
    }

    public void setNumeroDePersonal(String numeroDePersonal) {
        this.numeroDePersonal = numeroDePersonal;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

}
