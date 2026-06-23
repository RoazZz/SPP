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
import javafx.stage.FileChooser.ExtensionFilter;
import logica.dao.AutoevaluacionDAO;
import logica.dto.AutoevaluacionDTO;
import logica.utilidades.RegistradorBitacora;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvaluarAutoevaluacionControlador {
    private static final Logger REGISTRADOR = Logger.getLogger(EvaluarAutoevaluacionControlador.class.getName());
    private static final double CALIFICACION_MINIMA = 0.0;
    private static final double CALIFICACION_MAXIMA = 10.0;
    private static final String DESCRIPCION_PDF = "Archivos PDF";
    private static final String FILTRO_PDF_PATRON = "*.pdf";
    @FXML private Label lblIdAutoevaluacion;
    @FXML private Label lblMatricula;
    @FXML private TextField txtCalificacion;
    @FXML private TextArea txtComentarios;
    @FXML private Label lblError;
    @FXML private Button btnDescargar;
    @FXML private Button btnCancelar;
    @FXML private Button btnCalificar;
    private AutoevaluacionDTO autoevaluacionActual;
    @FXML
    public void initialize() {
        ocultarError();
    }
    public void cargarAutoevaluacion(AutoevaluacionDTO autoevaluacion) {
        this.autoevaluacionActual = autoevaluacion;
        lblIdAutoevaluacion.setText(String.valueOf(autoevaluacion.getIdAutoevaluacion()));
        lblMatricula.setText(autoevaluacion.getMatricula());
        if (autoevaluacion.getComentarios() != null) {
            txtComentarios.setText(autoevaluacion.getComentarios());
        }
    }
    @FXML
    private void manejarCalificar(ActionEvent eventoClic) {
        ocultarError();
        String errorValidacion = validarCampos();
        if (errorValidacion != null) {
            mostrarError(errorValidacion);
        } else {
            Optional<ButtonType> respuesta = mostrarConfirmacion("¿Estas seguro de que deseas asignar esta calificacion?");
            if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
                calificarAutoevaluacion(Double.parseDouble(txtCalificacion.getText().trim()));
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
        if (autoevaluacionActual == null || autoevaluacionActual.getRutaArchivo() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No hay documento para descargar.");
            return;
        }
        File archivoOrigen = new File(autoevaluacionActual.getRutaArchivo());
        if (!archivoOrigen.exists()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se encontro el archivo de la autoevaluacion.");
            return;
        }
        FileChooser selectorArchivo = new FileChooser();
        selectorArchivo.setTitle("Guardar autoevaluacion");
        selectorArchivo.setInitialFileName(archivoOrigen.getName());
        selectorArchivo.getExtensionFilters().add(new ExtensionFilter(DESCRIPCION_PDF, FILTRO_PDF_PATRON));
        File archivoDestino = selectorArchivo.showSaveDialog(btnDescargar.getScene().getWindow());
        if (archivoDestino == null) {
            return;
        }
        try {
            Files.copy(archivoOrigen.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Descarga", "La autoevaluacion se guardo correctamente.");
        } catch (IOException ioExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al descargar la autoevaluacion", ioExcepcion);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No fue posible guardar el archivo.");
        }
    }
    private String validarCampos() {
        String textoCalificacion = txtCalificacion.getText().trim();
        if (textoCalificacion.isEmpty()) {
            return "La calificacion es obligatoria.";
        }
        try {
            double valor = Double.parseDouble(textoCalificacion);
            if (valor < CALIFICACION_MINIMA || valor > CALIFICACION_MAXIMA) {
                return "Rango invalido (" + CALIFICACION_MINIMA + " - " + CALIFICACION_MAXIMA + ").";
            }
        } catch (NumberFormatException numeroInvalidoExcepcion) {
            return "Formato numerico invalido.";
        }
        return null;
    }
    private void calificarAutoevaluacion(double calificacion) {
        try {
            new AutoevaluacionDAO().calificarAutoevaluacion(autoevaluacionActual.getMatricula(), calificacion);
            RegistradorBitacora.registrar("CALIFICAR_AUTOEVALUACION", "Calificó la autoevaluación de la matrícula: " + autoevaluacionActual.getMatricula());            mostrarAlerta(Alert.AlertType.INFORMATION, "Exito", "Autoevaluacion evaluada correctamente.");
            btnCalificar.getScene().getWindow().hide();
        } catch (DAOExcepcion daoExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al calificar la autoevaluacion", daoExcepcion);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No fue posible guardar la calificacion.");
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
