package logica.utilidades;
import logica.dto.CalificacionFinalDTO;
public class CalculadoraCalificacionFinal {
    private static final double CALIFICACION_MAXIMA = 10.0;
    private static final int PESO_DOCUMENTOS_INICIALES = 5;
    private static final int PESO_REPORTES_MENSUALES = 20;
    private static final int PESO_PRIMER_INFORME_210 = 10;
    private static final int PESO_PRIMERA_PRESENTACION_COLEGIADO = 10;
    private static final int PESO_PRIMERA_EVALUACION_OV = 10;
    private static final int PESO_SEGUNDO_INFORME_420 = 15;
    private static final int PESO_SEGUNDA_PRESENTACION_COLEGIADO = 15;
    private static final int PESO_SEGUNDA_EVALUACION_OV = 10;
    private static final int PESO_AUTOEVALUACION = 5;
    private CalculadoraCalificacionFinal() {
    }
    public static double calcularPorcentaje(CalificacionFinalDTO calificacion) {
        double porcentaje = 0.0;
        porcentaje += ponderar(calificacion.getDocumentosIniciales(), PESO_DOCUMENTOS_INICIALES);
        porcentaje += ponderar(calificacion.getReportesMensuales(), PESO_REPORTES_MENSUALES);
        porcentaje += ponderar(calificacion.getPrimerInforme210(), PESO_PRIMER_INFORME_210);
        porcentaje += ponderar(calificacion.getPrimeraPresentacionColegiado(), PESO_PRIMERA_PRESENTACION_COLEGIADO);
        porcentaje += ponderar(calificacion.getPrimeraEvaluacionOrganizacion(), PESO_PRIMERA_EVALUACION_OV);
        porcentaje += ponderar(calificacion.getSegundoInforme420(), PESO_SEGUNDO_INFORME_420);
        porcentaje += ponderar(calificacion.getSegundaPresentacionColegiado(), PESO_SEGUNDA_PRESENTACION_COLEGIADO);
        porcentaje += ponderar(calificacion.getSegundaEvaluacionOrganizacion(), PESO_SEGUNDA_EVALUACION_OV);
        porcentaje += ponderar(calificacion.getAutoevaluacion(), PESO_AUTOEVALUACION);
        return porcentaje;
    }
    private static double ponderar(Double calificacionEvidencia, int peso) {
        double calificacion = 0.0;
        if (calificacionEvidencia != null) {
            calificacion = calificacionEvidencia;
        }
        return (calificacion / CALIFICACION_MAXIMA) * peso;
    }
}