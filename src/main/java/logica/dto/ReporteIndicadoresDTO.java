package logica.dto;

public class ReporteIndicadoresDTO {
    private final String categoria;
    private final int total;

    public ReporteIndicadoresDTO(String categoria, int total) {
        this.categoria = categoria;
        this.total = total;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getTotal() {
        return total;
    }
}