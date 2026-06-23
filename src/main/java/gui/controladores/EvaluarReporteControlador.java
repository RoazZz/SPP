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
import javafx.stage.FileChooser;
import logica.dao.ReporteDAO;
import logica.dto.ReporteDTO;
import logica.utilidades.RegistradorBitacora;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvaluarReporteControlador {

    private static final Logger REGISTRADOR = Logger.getLogger(EvaluarReporteControlador.class.getName());
    private static final double CALIFICACION_MINIMA = 0.0;
    private static final double CALIFICACION_MAXIMA = 10.0;

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
            File archivoOrigen = new File(reporteActual.getRuta());

            if (!archivoOrigen.exists()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se encontró el archivo origen del reporte.");
                return;
            }

            FileChooser selectorArchivos = new FileChooser();
            selectorArchivos.setTitle("Guardar Reporte");
            selectorArchivos.setInitialFileName(archivoOrigen.getName());

            File archivoDestino = selectorArchivos.showSaveDialog(btnDescargar.getScene().getWindow());

            if (archivoDestino != null) {
                try {
                    Files.copy(archivoOrigen.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    RegistradorBitacora.registrar("DESCARGAR_REPORTE", "Descargó el reporte con id: " + reporteActual.getIdReporte());
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El reporte se ha descargado correctamente.");
                } catch (IOException excepcionCapturada) {
                    REGISTRADOR.log(Level.SEVERE, "Error al copiar archivo con FileChooser", excepcionCapturada);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el archivo en la ruta seleccionada.");
                }
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
            if (valor < CALIFICACION_MINIMA || valor > CALIFICACION_MAXIMA) {
                return "Rango no válido para calificación.";
            }
        } catch (NumberFormatException excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Formato numérico inválido", excepcionCapturada);
            return "Formato numérico inválido.";
        }

        return null;
    }

    private void calificarReporte(double calificacion) {
        try {
            new ReporteDAO().calificarReporte(reporteActual.getIdReporte(), calificacion);
            RegistradorBitacora.registrar("CALIFICAR_REPORTE", "Calificó el reporte con id: " + reporteActual.getIdReporte());            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Evaluado correctamente.");
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