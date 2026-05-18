package gui.controladores;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.List;
import interfaces.Regresable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import logica.dao.ReporteDAO;
import logica.dto.ReporteDTO;
import logica.enums.EstadoReporte;
import logica.enums.TipoReporte;
import excepciones.DAOExcepcion;
import logica.utilidades.SesionUsuarioSingleton;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReporteGenerarControlador implements Initializable, Regresable {
    private static final Logger logger = Logger.getLogger(ReporteGenerarControlador.class.getName());

    @FXML private TextField txtNRC, txtPeriodo, txtNombreActividad, txtTiempoP, txtTiempoR;
    @FXML private TextArea txtDescActividad;
    @FXML private ComboBox<TipoReporte> cbTipoReporte;
    @FXML private ListView<String> lvActividades;
    @FXML private Button btnGenerar;
    @FXML private Button btnCancelar;
    @FXML private ComboBox<String> cbMes;
    private Scene escenaAnterior;

    private ObservableList<String> listaActividades = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbTipoReporte.getItems().setAll(TipoReporte.values());
        lvActividades.setItems(listaActividades);
        btnGenerar.setOnAction(e -> procesarGeneracion());
        btnCancelar.setOnAction(e -> regresar());
        cbMes.getItems().setAll(
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        );

        cbTipoReporte.valueProperty().addListener((observable, anterior, nuevo) -> {
            boolean esMensual = nuevo == TipoReporte.MENSUAL;
            cbMes.setVisible(esMensual);
            cbMes.setManaged(esMensual);
            if (!esMensual) {
                cbMes.setValue(null);
            }
        });
    }

    @FXML
    private void agregarActividad() {
        if (txtNombreActividad.getText().isEmpty() || txtTiempoR.getText().isEmpty()) {
            return;
        }
        listaActividades.add(txtNombreActividad.getText() + " | Horas: " + txtTiempoR.getText() + " | " + txtDescActividad.getText());
        txtNombreActividad.clear();
        txtDescActividad.clear();
        txtTiempoP.clear();
        txtTiempoR.clear();
    }

    @FXML
    private void procesarGeneracion() {
        if (cbTipoReporte.getValue() == null || listaActividades.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Llena los datos y agrega actividades.");
            return;
        }

        if (cbTipoReporte.getValue() == TipoReporte.MENSUAL && cbMes.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Falta el mes", "Selecciona el mes al que corresponde este reporte.");
            return;
        }

        String subCarpetaTipo = cbTipoReporte.getValue().name();
        String nombreArchivo = cbTipoReporte.getValue() == TipoReporte.MENSUAL
                ? "Generado_" + subCarpetaTipo + "_" + cbMes.getValue() + "_" + System.currentTimeMillis() + ".pdf"
                : "Generado_" + subCarpetaTipo + "_" + System.currentTimeMillis() + ".pdf";
        Path carpetaDestino = Paths.get(System.getProperty("user.dir"), "Reportes", subCarpetaTipo, "GENERADOS");

        try {
            if (esDuplicado()) {
                return;
            }

            if (!Files.exists(carpetaDestino)) {
                Files.createDirectories(carpetaDestino);
            }

            Path rutaArchivo = carpetaDestino.resolve(nombreArchivo);

            PdfWriter pdfWriter = new PdfWriter(rutaArchivo.toString());
            PdfDocument pdf = new PdfDocument(pdfWriter);
            Document documento = new Document(pdf);

            documento.add(new Paragraph("REPORTE OFICIAL DE PRÁCTICAS").setBold().setFontSize(16));
            documento.add(new Paragraph("Tipo: " + subCarpetaTipo));
            if (cbTipoReporte.getValue() == TipoReporte.MENSUAL) {
                documento.add(new Paragraph("Mes: " + cbMes.getValue()));
            }
            documento.add(new Paragraph("NRC: " + txtNRC.getText()));
            documento.add(new Paragraph("Periodo: " + txtPeriodo.getText()));
            documento.add(new Paragraph("Fecha: " + LocalDate.now()));
            documento.add(new Paragraph("\nDETALLE DE ACTIVIDADES:"));

            List listaPdf = new List();
            for (String act : listaActividades) {
                listaPdf.add(act);
            }
            documento.add(listaPdf);
            documento.close();

            String mesSeleccionado = cbTipoReporte.getValue() == TipoReporte.MENSUAL
                    ? cbMes.getValue()
                    : null;

            ReporteDAO reporteDAO = new ReporteDAO();
            ReporteDTO reporteDTO = new ReporteDTO(0,
                    SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getIdUsuario(),
                    cbTipoReporte.getValue(),
                    LocalDate.now(),
                    rutaArchivo.toString(),
                    EstadoReporte.GENERADO,
                    mesSeleccionado,
                    null,
                    null);
            reporteDAO.agregarReporte(reporteDTO);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Reporte generado en: " + subCarpetaTipo + "/GENERADOS");
            regresar();

        } catch (IOException | DAOExcepcion e) {
            logger.log(Level.SEVERE, "Error en generación", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo completar la operación.");
        }
    }

    private boolean esDuplicado() throws DAOExcepcion {
        ReporteDAO reporteDAO = new ReporteDAO();
        int idUsuario = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getIdUsuario();
        String mesSeleccionado = cbTipoReporte.getValue() == TipoReporte.MENSUAL ? cbMes.getValue() : null;

        if (reporteDAO.existeDuplicado(idUsuario, cbTipoReporte.getValue(), mesSeleccionado, EstadoReporte.GENERADO)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Reporte duplicado",
                    cbTipoReporte.getValue() == TipoReporte.MENSUAL
                            ? "Ya generaste el reporte de " + mesSeleccionado + "."
                            : "Ya generaste tu reporte parcial.");
            return true;
        }
        return false;
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