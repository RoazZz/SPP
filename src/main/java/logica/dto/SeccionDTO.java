package logica.dto;

public class SeccionDTO {
    private int idSeccion;
    private String nombre;

    public SeccionDTO(int idSeccion, String nombre) {
        this.idSeccion = idSeccion;
        this.nombre = nombre;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
