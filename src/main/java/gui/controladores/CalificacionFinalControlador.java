package gui.controladores;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import logica.dao.CalificacionFinalDAO;
import logica.dto.CalificacionFinalDTO;
import logica.enums.EstadoCalificacionFinal;
import logica.utilidades.CalculadoraCalificacionFinal;
import java.util.logging.Level;
import java.util.logging.Logger;
public class CalificacionFinalControlador {
    private static final Logger REGISTRADOR = Logger.getLogger(CalificacionFinalControlador.class.getName());
    private static final double CALIFICACION_MINIMA = 0.0;
    private static final double CALIFICACION_MAXIMA = 10.0;
    private static final double DIVISOR_ESCALA = 10.0;
    private static final String TEXTO_SIN_CALIFICACION = "Pendiente";
    @FXML private Label lblMatricula;
    @FXML private TextField txtDocumentosIniciales;
    @FXML private TextField txtReportesMensuales;
    @FXML private TextField txtPrimerInforme210;
    @FXML private TextField txtPrimeraPresentacionColegiado;
    @FXML private TextField txtPrimeraEvaluacionOrganizacion;
    @FXML private TextField txtSegundoInforme420;
    @FXML private TextField txtSegundaPresentacionColegiado;
    @FXML private TextField txtSegundaEvaluacionOrganizacion;
    @FXML private TextField txtAutoevaluacion;
    @FXML private Label lblCalificacionPorcentaje;
    @FXML private Label lblCalificacionFinal;
    @FXML private Label lblError;
    private CalificacionFinalDAO calificacionFinalDAO;
    private String matriculaPracticante;
    @FXML
    private void initialize() {
        ocultarError();
        try {
            calificacionFinalDAO = new CalificacionFinalDAO();
        } catch (DAOExcepcion daoExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al inicializar el acceso a datos", daoExcepcion);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No fue posible conectar con la base de datos.");
        }
    }
    public void cargarCalificacion(String matricula) {
        this.matriculaPracticante = matricula;
        lblMatricula.setText(matricula);
        refrescarVista();
    }
    @FXML
    private void calcularCalificacionFinal() {
        ocultarError();
        String errorValidacion = validarCampos();
        if (errorValidacion != null) {
            mostrarError(errorValidacion);
            return;
        }
        CalificacionFinalDTO calificacion = construirDesdeFormulario();
        double porcentaje = CalculadoraCalificacionFinal.calcularPorcentaje(calificacion);
        double calificacionSobreDiez = porcentaje / DIVISOR_ESCALA;
        calificacion.setCalificacionPorcentaje(porcentaje);
        calificacion.setCalificacionFinal(calificacionSobreDiez);
        calificacion.setEstado(EstadoCalificacionFinal.CALCULADA);
        try {
            calificacionFinalDAO.guardarCalificacionFinal(calificacion);
            lblCalificacionPorcentaje.setText(formatearPorcentaje(porcentaje));
            lblCalificacionFinal.setText(formatearCalificacion(calificacionSobreDiez));
            mostrarAlerta(Alert.AlertType.INFORMATION, "Listo", "Calificacion final calculada y guardada.");
        } catch (DAOExcepcion daoExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al guardar la calificacion final", daoExcepcion);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No fue posible guardar la calificacion final.");
        }
    }
    private CalificacionFinalDTO construirDesdeFormulario() {
        return new CalificacionFinalDTO(
                0,
                matriculaPracticante,
                leerCampo(txtDocumentosIniciales),
                leerCampo(txtReportesMensuales),
                leerCampo(txtPrimerInforme210),
                leerCampo(txtPrimeraPresentacionColegiado),
                leerCampo(txtPrimeraEvaluacionOrganizacion),
                leerCampo(txtSegundoInforme420),
                leerCampo(txtSegundaPresentacionColegiado),
                leerCampo(txtSegundaEvaluacionOrganizacion),
                leerCampo(txtAutoevaluacion),
                null,
                null,
                EstadoCalificacionFinal.PENDIENTE
        );
    }
    private void refrescarVista() {
        try {
            CalificacionFinalDTO calificacion = calificacionFinalDAO.buscarPorMatricula(matriculaPracticante);
            escribirCampo(txtDocumentosIniciales, calificacion.getDocumentosIniciales());
            escribirCampo(txtReportesMensuales, calificacion.getReportesMensuales());
            escribirCampo(txtPrimerInforme210, calificacion.getPrimerInforme210());
            escribirCampo(txtPrimeraPresentacionColegiado, calificacion.getPrimeraPresentacionColegiado());
            escribirCampo(txtPrimeraEvaluacionOrganizacion, calificacion.getPrimeraEvaluacionOrganizacion());
            escribirCampo(txtSegundoInforme420, calificacion.getSegundoInforme420());
            escribirCampo(txtSegundaPresentacionColegiado, calificacion.getSegundaPresentacionColegiado());
            escribirCampo(txtSegundaEvaluacionOrganizacion, calificacion.getSegundaEvaluacionOrganizacion());
            escribirCampo(txtAutoevaluacion, calificacion.getAutoevaluacion());
            lblCalificacionPorcentaje.setText(formatearPorcentaje(calificacion.getCalificacionPorcentaje()));
            lblCalificacionFinal.setText(formatearCalificacion(calificacion.getCalificacionFinal()));
        } catch (EntidadNoEncontradaExcepcion entidadNoEncontradaExcepcion) {
            lblCalificacionPorcentaje.setText(TEXTO_SIN_CALIFICACION);
            lblCalificacionFinal.setText(TEXTO_SIN_CALIFICACION);
        } catch (DAOExcepcion daoExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al refrescar la calificacion final", daoExcepcion);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No fue posible cargar la calificacion.");
        }
    }
    private String validarCampos() {
        TextField[] camposCalificacion = {
                txtDocumentosIniciales, txtReportesMensuales, txtPrimerInforme210,
                txtPrimeraPresentacionColegiado, txtPrimeraEvaluacionOrganizacion, txtSegundoInforme420,
                txtSegundaPresentacionColegiado, txtSegundaEvaluacionOrganizacion, txtAutoevaluacion
        };
        for (TextField campoCalificacion : camposCalificacion) {
            String texto = campoCalificacion.getText().trim();
            if (!texto.isEmpty()) {
                String errorRango = validarRango(texto);
                if (errorRango != null) {
                    return errorRango;
                }
            }
        }
        return null;
    }
    private String validarRango(String texto) {
        try {
            double valor = Double.parseDouble(texto);
            if (valor < CALIFICACION_MINIMA || valor > CALIFICACION_MAXIMA) {
                return "Cada calificacion debe estar entre " + CALIFICACION_MINIMA + " y " + CALIFICACION_MAXIMA + ".";
            }
        } catch (NumberFormatException numeroInvalidoExcepcion) {
            return "Formato numerico invalido en alguna calificacion.";
        }
        return null;
    }
    private Double leerCampo(TextField campoCalificacion) {
        String texto = campoCalificacion.getText().trim();
        if (texto.isEmpty()) {
            return null;
        }
        return Double.parseDouble(texto);
    }
    private void escribirCampo(TextField campoCalificacion, Double valor) {
        if (valor != null) {
            campoCalificacion.setText(valor.toString());
        }
    }
    private String formatearCalificacion(Double calificacion) {
        if (calificacion == null) {
            return TEXTO_SIN_CALIFICACION;
        }
        return calificacion.toString();
    }
    private String formatearPorcentaje(Double porcentaje) {
        if (porcentaje == null) {
            return TEXTO_SIN_CALIFICACION;
        }
        return porcentaje + "%";
    }
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
    private void ocultarError() {
        lblError.setVisible(false);
        lblError.setManaged(false);
    }
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
