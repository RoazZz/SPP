package gui.controladores;

import excepciones.DAOExcepcion;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logica.dao.ReporteDAO;
import logica.dto.ReporteDTO;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvaluarReporteControlador {

    @FXML private Label lblIdReporte;
    @FXML private Label lblIdUsuario;
    @FXML private Label lblFecha;
    @FXML private Label lblEstado;
    @FXML private TextField txtCalificacion;
    @FXML private TextArea txtObservaciones;
    @FXML private Label lblError;
    @FXML private Button btnDescargar;
    @FXML private Button btnCancelar;
    @FXML private Button btnCalificar;

    private ReporteDTO reporteActual;

    private static final Logger LOGGER = Logger.getLogger(EvaluarReporteControlador.class.getName());

    @FXML
    public void initialize() {
        btnDescargar.setOnAction(evento -> manejarDescargar());
        btnCancelar.setOnAction(evento -> manejarCancelar());
        btnCalificar.setOnAction(evento -> manejarCalificar());
    }

    public void cargarReporte(ReporteDTO reporte) {
        this.reporteActual = reporte;
        lblIdReporte.setText(String.valueOf(reporte.getIdReporte()));
        lblIdUsuario.setText(String.valueOf(reporte.getIdUsuario()));
        lblFecha.setText(reporte.getFecha().toString());
        lblEstado.setText(reporte.getEstado().name());
    }

    private String validarCampos() {
        String textoCalificacion = txtCalificacion.getText().trim();

        if (textoCalificacion.isEmpty()) {
            return "La calificación es obligatoria.";
        }

        double calificacion;
        try {
            calificacion = Double.parseDouble(textoCalificacion);
        } catch (NumberFormatException e) {
            return "La calificación debe ser un número válido (Ej. 8.50).";
        }

        if (calificacion < 0.0 || calificacion > 10.0) {
            return "La calificación debe estar entre 0.00 y 10.00.";
        }

        return null;
    }

    private void calificarReporte(double calificacion) {
        try {
            ReporteDAO reporteDAO = new ReporteDAO();
            reporteDAO.calificarReporte(reporteActual.getIdReporte(), calificacion);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Reporte Parcial evaluado exitosamente.");
            btnCalificar.getScene().getWindow().hide();
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al calificar reporte", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo calificar el Reporte Parcial. Intente de nuevo.");
        }
    }

    private void manejarCalificar() {
        ocultarError();

        String errorValidacion = validarCampos();
        if (errorValidacion != null) {
            mostrarError(errorValidacion);
            return;
        }

        Optional<ButtonType> respuesta = mostrarConfirmacion("¿Estás seguro de que deseas asignar esta calificación?");

        if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
            calificarReporte(Double.parseDouble(txtCalificacion.getText().trim()));
        }
    }

    private void manejarCancelar() {
        Optional<ButtonType> respuesta = mostrarConfirmacion("¿Seguro desea cancelar?");

        if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
            btnCancelar.getScene().getWindow().hide();
        }
    }

    private void manejarDescargar() {
        try {
            File archivo = new File(reporteActual.getRuta());

            if (!archivo.exists()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se encontró el archivo del reporte.");
                return;
            }

            Desktop.getDesktop().open(archivo);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al abrir archivo del reporte", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir el archivo.");
        }
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

    private Optional<ButtonType> mostrarConfirmacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        alerta.setHeaderText(null);
        return alerta.showAndWait();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}