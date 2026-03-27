package accesodatos.DTO;

public class CoordinadorDTO {
    private String numeroPersonal;

    public CoordinadorDTO (String numeroPersonal){
        this.numeroPersonal = numeroPersonal;
    }

    public String getNumeroPersonal() {
        return numeroPersonal;
    }

    public void setNumeroPersonal(String numeroPersonal) {
        this.numeroPersonal = numeroPersonal;
    }
}
