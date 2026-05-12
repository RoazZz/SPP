package gui.controladores;

import interfaces.Regresable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logica.dao.ReporteDAO;
import logica.dto.ReporteDTO;
import logica.enums.TipoReporte;
import logica.enums.EstadoReporte;
import excepciones.DAOExcepcion;
import logica.utilidades.CifradorArchivo;
import logica.utilidades.SesionUsuarioSingleton;

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

public class ReporteAnadirControlador implements Initializable, Regresable {
    private static final Logger logger = Logger.getLogger(ReporteAnadirControlador.class.getName());

    @FXML private ComboBox<TipoReporte> cbTipoReporte;

    @FXML private Label lblNombreArchivo;
    @FXML private Button btnVistaPrevia;
    @FXML private HBox hboxValidacionArchivo;

    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    private Scene escenaAnterior;
    @FXML private ComboBox<String> cbMes;

    private File archivoPDF = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbTipoReporte.getItems().setAll(TipoReporte.values());
        cbTipoReporte.valueProperty().addListener((observable, viejoValor, nuevoValor) -> actualizarEstadoBotonGuardar());
        cbMes.getItems().setAll(
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        );

        cbTipoReporte.valueProperty().addListener((observable, viejoValor, nuevoValor) -> {
            boolean esMensual = nuevoValor == TipoReporte.MENSUAL;
            cbMes.setVisible(esMensual);
            cbMes.setManaged(esMensual);
            if (!esMensual) {
                cbMes.setValue(null);
            }
            actualizarEstadoBotonGuardar();
        });

        cbMes.valueProperty().addListener((observable, viejoValor, nuevoValor) -> actualizarEstadoBotonGuardar());
        btnGuardar.setOnAction(e -> guardarReporte());
        btnCancelar.setOnAction(e -> regresar());
        btnVistaPrevia.setOnAction(e -> mostrarVistaPrevia());

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
                if (esDuplicado()) {
                    return;
                }
                String subCarpetaTipo = cbTipoReporte.getValue().name();
                Path carpetaDestino = Paths.get(System.getProperty("user.dir"), "Reportes", subCarpetaTipo, "ANADIDOS");

                if (!Files.exists(carpetaDestino)) {
                    Files.createDirectories(carpetaDestino);
                }

                Path archivoDestino = carpetaDestino.resolve(archivoPDF.getName());
                Files.copy(archivoPDF.toPath(), archivoDestino, StandardCopyOption.REPLACE_EXISTING);
                String hashArchivo = CifradorArchivo.generarHashArchivo(archivoPDF.toPath());
                String hashContenido = CifradorArchivo.generarHashContenido(archivoPDF.toPath());


                ReporteDAO reporteDAO = new ReporteDAO();
                ReporteDTO reporteDTO = new ReporteDTO(
                        0,
                        SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getIdUsuario(),
                        cbTipoReporte.getValue(),
                        LocalDate.now(),
                        archivoDestino.toString(),
                        EstadoReporte.ENTREGADO,
                        cbTipoReporte.getValue() == TipoReporte.MENSUAL ? cbMes.getValue() : null,
                        hashArchivo,
                        hashContenido
                );
                reporteDAO.agregarReporte(reporteDTO);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Archivo firmado cargado y registrado exitosamente.");
                regresar();

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error al copiar el archivo físico", e);
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Archivo", "No se pudo guardar el archivo en el servidor local.");
            } catch (DAOExcepcion e) {
                logger.log(Level.SEVERE, "Error al registrar en la base de datos", e);
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "El archivo se copió, pero no se pudo registrar en la base de datos.");
            }
        }
    }

    private boolean esDuplicado() throws DAOExcepcion, IOException {
        ReporteDAO reporteDAO = new ReporteDAO();
        int idUsuario = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getIdUsuario();
        String mesSeleccionado = cbTipoReporte.getValue() == TipoReporte.MENSUAL ? cbMes.getValue() : null;

        if (reporteDAO.existeDuplicado(idUsuario, cbTipoReporte.getValue(), mesSeleccionado, EstadoReporte.ENTREGADO)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Reporte duplicado",
                    cbTipoReporte.getValue() == TipoReporte.MENSUAL
                            ? "Ya subiste el reporte de " + mesSeleccionado + "."
                            : "Ya subiste tu reporte parcial.");
            return true;
        }

        String tipoEnPdf = CifradorArchivo.extraerTipoReporte(archivoPDF.toPath());
        if (tipoEnPdf != null && !tipoEnPdf.equalsIgnoreCase(cbTipoReporte.getValue().name())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Tipo incorrecto",
                    "El PDF es de tipo " + tipoEnPdf + " pero seleccionaste " + cbTipoReporte.getValue().name() + ".");
            return true;
        }

        if (cbTipoReporte.getValue() == TipoReporte.MENSUAL) {
            String mesEnPdf = CifradorArchivo.extraerMes(archivoPDF.toPath());
            if (mesEnPdf != null && !mesEnPdf.equalsIgnoreCase(cbMes.getValue())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Mes incorrecto",
                        "El PDF corresponde a " + mesEnPdf + " pero seleccionaste " + cbMes.getValue() + ".");
                return true;
            }
        }

        String hashArchivo = CifradorArchivo.generarHashArchivo(archivoPDF.toPath());
        String hashContenido = CifradorArchivo.generarHashContenido(archivoPDF.toPath());

        if (reporteDAO.existeHashDuplicado(hashArchivo, hashContenido)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Archivo duplicado", "Este archivo ya existe en el sistema.");
            return true;
        }

        return false;
    }

    private void actualizarEstadoBotonGuardar() {
        boolean faltaMes = cbTipoReporte.getValue() == TipoReporte.MENSUAL
                && cbMes.getValue() == null;
        btnGuardar.setDisable(archivoPDF == null || cbTipoReporte.getValue() == null || faltaMes);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @Override
    public void setEscenaAnterior(Scene escena) {
        this.escenaAnterior = escena;
    }

    private void regresar() {
        if (escenaAnterior != null) {
            Stage escenario = (Stage) btnCancelar.getScene().getWindow();
            escenario.setScene(escenaAnterior);
            escenario.show();
        }
    }
}