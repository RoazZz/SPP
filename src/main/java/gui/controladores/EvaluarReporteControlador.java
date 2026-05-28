package gui.controladores;

import excepciones.DAOExcepcion;
import javafx.event.ActionEvent;
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

    private static final Logger REGISTRADOR = Logger.getLogger(EvaluarReporteControlador.class.getName());

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

    @FXML
    public void initialize() {
        ocultarError();
    }

    public void cargarReporte(ReporteDTO reporte) {
        this.reporteActual = reporte;
        lblIdReporte.setText(String.valueOf(reporte.getIdReporte()));
        lblIdUsuario.setText(String.valueOf(reporte.getIdUsuario()));
        lblFecha.setText(reporte.getFecha().toString());
        lblEstado.setText(reporte.getEstado().name());
    }

    @FXML
    private void manejarCalificar(ActionEvent eventoClic) {
        ocultarError();
        String errorValidacion = validarCampos();
        if (errorValidacion != null) {
            mostrarError(errorValidacion);
        } else {
            Optional<ButtonType> respuesta = mostrarConfirmacion("¿Estás seguro de que deseas asignar esta calificación?");
            if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
                calificarReporte(Double.parseDouble(txtCalificacion.getText().trim()));
            }
        }
    }

    @FXML
    private void manejarCancelar(ActionEvent eventoClic) {
        Optional<ButtonType> respuesta = mostrarConfirmacion("¿Seguro desea cancelar?");
        if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
            btnCancelar.getScene().getWindow().hide();
        }
    }

    @FXML
    private void manejarDescargar(ActionEvent eventoClic) {
        if (reporteActual != null) {
            try {
                File archivo = new File(reporteActual.getRuta());
                if (!archivo.exists()) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se encontró el archivo.");
                } else {
                    Desktop.getDesktop().open(archivo);
                }
            } catch (IOException excepcionCapturada) {
                REGISTRADOR.log(Level.SEVERE, "Error al abrir reporte", excepcionCapturada);
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir el archivo.");
            }
        }
    }

    private String validarCampos() {
        String textoCalificacion = txtCalificacion.getText().trim();
        if (textoCalificacion.isEmpty()) {
            return "La calificación es obligatoria.";
        }
        try {
            double valor = Double.parseDouble(textoCalificacion);
            if (valor < 0.0 || valor > 10.0) {
                return "Rango inválido (0-10).";
            }
        } catch (NumberFormatException e) {
            return "Formato numérico inválido.";
        }
        return null;
    }

    private void calificarReporte(double calificacion) {
        try {
            new ReporteDAO().calificarReporte(reporteActual.getIdReporte(), calificacion);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Evaluado correctamente.");
            btnCalificar.getScene().getWindow().hide();
        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error calificar", excepcionCapturada);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar.");
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