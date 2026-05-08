package logica.dto;

public class BuzonDTO {
    private int idBuzon;
    private int idUsuario;

    public BuzonDTO(int idBuzon, int idUsuario) {
        this.idBuzon = idBuzon;
        this.idUsuario = idUsuario;
    }

    public BuzonDTO(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdBuzon() {
        return idBuzon;
    }

    public void setIdBuzon(int idBuzon) {
        this.idBuzon = idBuzon;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
}