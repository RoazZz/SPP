package logica.dto;
import logica.enums.EstadoCalificacionFinal;

public class CalificacionFinalDTO {
    private int idCalificacionFinal;
    private String matricula;
    private Double documentosIniciales;
    private Double reportesMensuales;
    private Double primerInforme210;
    private Double primeraPresentacionColegiado;
    private Double primeraEvaluacionOrganizacion;
    private Double segundoInforme420;
    private Double segundaPresentacionColegiado;
    private Double segundaEvaluacionOrganizacion;
    private Double autoevaluacion;
    private Double calificacionPorcentaje;
    private Double calificacionFinal;
    private EstadoCalificacionFinal estado;

    public CalificacionFinalDTO(int idCalificacionFinal, String matricula,
                                Double documentosIniciales, Double reportesMensuales,
                                Double primerInforme210, Double primeraPresentacionColegiado,
                                Double primeraEvaluacionOrganizacion, Double segundoInforme420,
                                Double segundaPresentacionColegiado, Double segundaEvaluacionOrganizacion,
                                Double autoevaluacion, Double calificacionPorcentaje,
                                Double calificacionFinal, EstadoCalificacionFinal estado) {
        this.idCalificacionFinal = idCalificacionFinal;
        this.matricula = matricula;
        this.documentosIniciales = documentosIniciales;
        this.reportesMensuales = reportesMensuales;
        this.primerInforme210 = primerInforme210;
        this.primeraPresentacionColegiado = primeraPresentacionColegiado;
        this.primeraEvaluacionOrganizacion = primeraEvaluacionOrganizacion;
        this.segundoInforme420 = segundoInforme420;
        this.segundaPresentacionColegiado = segundaPresentacionColegiado;
        this.segundaEvaluacionOrganizacion = segundaEvaluacionOrganizacion;
        this.autoevaluacion = autoevaluacion;
        this.calificacionPorcentaje = calificacionPorcentaje;
        this.calificacionFinal = calificacionFinal;
        this.estado = estado;
    }
    public int getIdCalificacionFinal() {
        return idCalificacionFinal;
    }

    public void setIdCalificacionFinal(int idCalificacionFinal) {
        this.idCalificacionFinal = idCalificacionFinal;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Double getDocumentosIniciales() {
        return documentosIniciales;
    }

    public void setDocumentosIniciales(Double documentosIniciales) {
        this.documentosIniciales = documentosIniciales;
    }

    public Double getReportesMensuales() {
        return reportesMensuales;
    }

    public void setReportesMensuales(Double reportesMensuales) {
        this.reportesMensuales = reportesMensuales;
    }

    public Double getPrimerInforme210() {
        return primerInforme210;
    }

    public void setPrimerInforme210(Double primerInforme210) {
        this.primerInforme210 = primerInforme210;
    }

    public Double getPrimeraPresentacionColegiado() {
        return primeraPresentacionColegiado;
    }

    public void setPrimeraPresentacionColegiado(Double primeraPresentacionColegiado) {
        this.primeraPresentacionColegiado = primeraPresentacionColegiado;
    }

    public Double getPrimeraEvaluacionOrganizacion() {
        return primeraEvaluacionOrganizacion;
    }

    public void setPrimeraEvaluacionOrganizacion(Double primeraEvaluacionOrganizacion) {
        this.primeraEvaluacionOrganizacion = primeraEvaluacionOrganizacion;
    }

    public Double getSegundoInforme420() {
        return segundoInforme420;
    }

    public void setSegundoInforme420(Double segundoInforme420) {
        this.segundoInforme420 = segundoInforme420;
    }

    public Double getSegundaPresentacionColegiado() {
        return segundaPresentacionColegiado;
    }

    public void setSegundaPresentacionColegiado(Double segundaPresentacionColegiado) {
        this.segundaPresentacionColegiado = segundaPresentacionColegiado;
    }

    public Double getSegundaEvaluacionOrganizacion() {
        return segundaEvaluacionOrganizacion;
    }

    public void setSegundaEvaluacionOrganizacion(Double segundaEvaluacionOrganizacion) {
        this.segundaEvaluacionOrganizacion = segundaEvaluacionOrganizacion;
    }

    public Double getAutoevaluacion() {
        return autoevaluacion;
    }

    public void setAutoevaluacion(Double autoevaluacion) {
        this.autoevaluacion = autoevaluacion;
    }

    public Double getCalificacionPorcentaje() {
        return calificacionPorcentaje;
    }

    public void setCalificacionPorcentaje(Double calificacionPorcentaje) {
        this.calificacionPorcentaje = calificacionPorcentaje;
    }

    public Double getCalificacionFinal() {
        return calificacionFinal;
    }

    public void setCalificacionFinal(Double calificacionFinal) {
        this.calificacionFinal = calificacionFinal;
    }

    public EstadoCalificacionFinal getEstado() {
        return estado;
    }

    public void setEstado(EstadoCalificacionFinal estado) {
        this.estado = estado;
    }
}
