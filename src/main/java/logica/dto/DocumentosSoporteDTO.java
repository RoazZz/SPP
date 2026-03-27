package logica.dto;

public class DocumentosSoporteDTO {
    private int idDocumento;
    private String matricula;
    private String tipoDocumento;
    private String estado;

    public DocumentosSoporteDTO(int idDocumento, String matricula, String tipoDocumento, String estado) {
        this.idDocumento = idDocumento;
        this.matricula = matricula;
        this.tipoDocumento = tipoDocumento;
        this.estado = estado;
    }

    public int getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
