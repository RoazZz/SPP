package gui.controladores;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import logica.dao.ReporteDAO;
import logica.dto.ReporteDTO;
import logica.enums.TipoReporte;
import logica.enums.EstadoReporte;
import excepciones.DAOExcepcion;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReporteAnadirControlador implements Initializable {
    private static final Logger logger = Logger.getLogger(ReporteAnadirControlador.class.getName());

    @FXML private ComboBox<TipoReporte> cbTipoReporte;

    @FXML private Label lblNombreArchivo;
    @FXML private Button btnVistaPrevia;
    @FXML private HBox hboxValidacionArchivo;

    @FXML private Button btnGuardar;

    private File archivoPDF = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbTipoReporte.getItems().setAll(TipoReporte.values());
        cbTipoReporte.valueProperty().addListener((obs, oldVal, newVal) -> actualizarEstadoBotonGuardar());
    }

    @FXML
    private void seleccionarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Reporte PDF Firmado");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documentos PDF", "*.pdf"));

        File archivo = fileChooser.showOpenDialog(lblNombreArchivo.getScene().getWindow());
        if (archivo == null) {
            return;
        }

        archivoPDF = archivo;
        lblNombreArchivo.setText(archivo.getName());

        hboxValidacionArchivo.setVisible(true);
        hboxValidacionArchivo.setManaged(true);
        btnVistaPrevia.setVisible(true);
        btnVistaPrevia.setManaged(true);

        actualizarEstadoBotonGuardar();
    }

    @FXML
    private void mostrarVistaPrevia() {
        if (archivoPDF == null) return;
        try {
            Desktop.getDesktop().open(archivoPDF);
        } catch (IOException e) {
            logger.log(Level.WARNING, "No se pudo abrir la vista previa del PDF", e);
            mostrarAlerta(Alert.AlertType.WARNING, "Vista previa no disponible", "No se pudo abrir el visor de PDF predeterminado.");
        }
    }

    @FXML
    private void guardarReporte() {
        if (cbTipoReporte.getValue() == null || archivoPDF == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Faltan datos", "Por favor selecciona el tipo de reporte y el archivo PDF.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Está seguro de subir el documento firmado?", ButtonType.YES, ButtonType.NO);
        confirmacion.setHeaderText(null);
        confirmacion.setTitle("Confirmar subida");

        if (confirmacion.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            try {
                String subCarpetaTipo = cbTipoReporte.getValue().name();
                Path carpetaDestino = Paths.get(System.getProperty("user.dir"), "Reportes", subCarpetaTipo, "ANADIDOS");

                if (!Files.exists(carpetaDestino)) {
                    Files.createDirectories(carpetaDestino);
                }

                Path archivoDestino = carpetaDestino.resolve(archivoPDF.getName());
                Files.copy(archivoPDF.toPath(), archivoDestino, StandardCopyOption.REPLACE_EXISTING);

                ReporteDAO reporteDAO = new ReporteDAO();
                ReporteDTO reporteDTO = new ReporteDTO(
                        0,
                        cbTipoReporte.getValue(),
                        LocalDate.now(),
                        archivoDestino.toString(),
                        EstadoReporte.ENTREGADO
                );
                reporteDAO.agregarReporte(reporteDTO);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Archivo firmado cargado y registrado exitosamente.");
                cerrarVentana();

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error al copiar el archivo físico", e);
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Archivo", "No se pudo guardar el archivo en el servidor local.");
            } catch (DAOExcepcion e) {
                logger.log(Level.SEVERE, "Error al registrar en la base de datos", e);
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "El archivo se copió, pero no se pudo registrar en la base de datos.");
            }
        }
    }

    @FXML
    private void cancelar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que deseas cancelar? Se perderá la selección del archivo.", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText(null);
        if (alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            cerrarVentana();
        }
    }

    private void actualizarEstadoBotonGuardar() {
        btnGuardar.setDisable(archivoPDF == null || cbTipoReporte.getValue() == null);
    }

    private void cerrarVentana() {
        lblNombreArchivo.getScene().getWindow().hide();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}