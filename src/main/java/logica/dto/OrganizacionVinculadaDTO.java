package logica.dto;

public class OrganizacionVinculadaDTO {
    private String idOrganizacion;
    private String nombre;
    private String direccion;

    public OrganizacionVinculadaDTO(String idOrganizacion, String nombre, String direccion){
        this.idOrganizacion = idOrganizacion;
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public String getidOrganizacion (){
        return idOrganizacion;
    }

    public void setidOrganizacion(String idOrganizacion){
        this.idOrganizacion = idOrganizacion;

    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre (String nombre){
        this.nombre = nombre;

    }

    public String getDireccion(){
        return direccion;
    }

    public void setDireccion (String direccion){
        this.direccion = direccion;

    }
}
