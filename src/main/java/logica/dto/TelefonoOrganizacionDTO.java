package logica.dto;

import java.util.ArrayList;
import java.util.List;

public class TelefonoOrganizacionDTO {
    private String idOrganizacion;
    private List<String> telefono = new ArrayList<>();

    public TelefonoOrganizacionDTO() {
    }

    public TelefonoOrganizacionDTO(String idOrganizacion, List<String> telefono){
     this.idOrganizacion = idOrganizacion;
     this.telefono = telefono;
    }

    public String getIdOrganizacion() {
        return idOrganizacion;
    }

    public void setIdOrganizacion(String idOrganizacion) {
        this.idOrganizacion = idOrganizacion;
    }

    public List<String> getTelefono() {
        return telefono;
    }

    public void setTelefono(List<String> telefono) {
        this.telefono = telefono;
    }
}
