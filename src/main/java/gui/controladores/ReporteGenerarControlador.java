package gui.controladores;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.List;
import logica.interfaces.Regresable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
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

    private static final Logger REGISTRADOR = Logger.getLogger(ReporteGenerarControlador.class.getName());

    @FXML private TextField txtNRC;
    @FXML private TextField txtPeriodo;
    @FXML private TextField txtNombreActividad;
    @FXML private TextField txtTiempoP;
    @FXML private TextField txtTiempoR;
    @FXML private TextArea txtDescActividad;
    @FXML private ComboBox<TipoReporte> cbTipoReporte;
    @FXML private ListView<String> lvActividades;
    @FXML private ComboBox<String> cbMes;

    private Scene escenaAnterior;
    private ObservableList<String> listaActividades = FXCollections.observableArrayList();

    @Override
    public void initialize(URL urlRecibida, ResourceBundle recursoRecibido) {
        cbTipoReporte.getItems().setAll(TipoReporte.values());
        lvActividades.setItems(listaActividades);

        cbMes.getItems().setAll(
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        );

        cbTipoReporte.valueProperty().addListener(new ChangeListener<TipoReporte>() {
            @Override
            public void changed(ObservableValue<? extends TipoReporte> observablePropiedad, TipoReporte valorAnterior, TipoReporte valorNuevo) {
                boolean esMensual = false;
                if (valorNuevo == TipoReporte.MENSUAL) {
                    esMensual = true;
                }
                cbMes.setVisible(esMensual);
                cbMes.setManaged(esMensual);
                if (!esMensual) {
                    cbMes.setValue(null);
                }
            }
        });
    }

    @FXML
    private void agregarActividad(ActionEvent eventoClic) {
        if (txtNombreActividad.getText().isEmpty() || txtTiempoR.getText().isEmpty()) {
            return;
        }
        String actividadFormateada = txtNombreActividad.getText() + " | Horas: " + txtTiempoR.getText() + " | " + txtDescActividad.getText();
        listaActividades.add(actividadFormateada);
        txtNombreActividad.clear();
        txtDescActividad.clear();
        txtTiempoP.clear();
        txtTiempoR.clear();
    }

    @FXML
    private void procesarGeneracion(ActionEvent eventoClic) {
        if (cbTipoReporte.getValue() == null || listaActividades.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Llena los datos y agrega actividades.");
            return;
        }

        if (cbTipoReporte.getValue() == TipoReporte.MENSUAL && cbMes.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Falta el mes", "Selecciona el mes al que corresponde este reporte.");
            return;
        }

        String subCarpetaTipo = cbTipoReporte.getValue().name();
        String nombreArchivoPDF = "Generado_" + subCarpetaTipo + "_" + System.currentTimeMillis() + ".pdf";
        if (cbTipoReporte.getValue() == TipoReporte.MENSUAL) {
            nombreArchivoPDF = "Generado_" + subCarpetaTipo + "_" + cbMes.getValue() + "_" + System.currentTimeMillis() + ".pdf";
        }

        Path carpetaDestinoSistema = Paths.get(System.getProperty("user.dir"), "Reportes", subCarpetaTipo, "GENERADOS");

        try {
            if (esDuplicado()) {
                return;
            }

            if (!Files.exists(carpetaDestinoSistema)) {
                Files.createDirectories(carpetaDestinoSistema);
            }

            Path rutaArchivoFinal = carpetaDestinoSistema.resolve(nombreArchivoPDF);

            try (
                    PdfWriter escritorPDF = new PdfWriter(rutaArchivoFinal.toString());
                    PdfDocument documentoPDF = new PdfDocument(escritorPDF);
                    Document documentoVisual = new Document(documentoPDF)
            ) {
                documentoVisual.add(new Paragraph("REPORTE OFICIAL DE PRÁCTICAS").setBold().setFontSize(16));
                documentoVisual.add(new Paragraph("Tipo: " + subCarpetaTipo));

                if (cbTipoReporte.getValue() == TipoReporte.MENSUAL) {
                    documentoVisual.add(new Paragraph("Mes: " + cbMes.getValue()));
                }

                documentoVisual.add(new Paragraph("NRC: " + txtNRC.getText()));
                documentoVisual.add(new Paragraph("Periodo: " + txtPeriodo.getText()));
                documentoVisual.add(new Paragraph("Fecha: " + LocalDate.now()));
                documentoVisual.add(new Paragraph("\nDETALLE DE ACTIVIDADES:"));

                List listaVisualPDF = new List();
                for (int indiceActividad = 0; indiceActividad < listaActividades.size(); indiceActividad++) {
                    String actividadLista = listaActividades.get(indiceActividad);
                    listaVisualPDF.add(actividadLista);
                }
                documentoVisual.add(listaVisualPDF);
            }

            String mesConfigurado = null;
            if (cbTipoReporte.getValue() == TipoReporte.MENSUAL) {
                mesConfigurado = cbMes.getValue();
            }

            ReporteDAO reporteAccesoBD = new ReporteDAO();
            int idUsuarioBase = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getIdUsuario();
            ReporteDTO reporteRegistrado = new ReporteDTO(
                    0,
                    idUsuarioBase,
                    cbTipoReporte.getValue(),
                    LocalDate.now(),
                    rutaArchivoFinal.toString(),
                    EstadoReporte.GENERADO,
                    mesConfigurado,
                    null,
                    null,
                    null
            );
            reporteAccesoBD.agregarReporte(reporteRegistrado);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Reporte generado en: " + subCarpetaTipo + "/GENERADOS");
            regresar(eventoClic);

        } catch (IOException | DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error en generación", excepcionCapturada);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo completar la operación.");
        }
    }

    private boolean esDuplicado() throws DAOExcepcion {
        ReporteDAO reporteAccesoBD = new ReporteDAO();
        int identificadorUsuario = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getIdUsuario();
        String mesParaVerificar = null;

        if (cbTipoReporte.getValue() == TipoReporte.MENSUAL) {
            mesParaVerificar = cbMes.getValue();
        }

        boolean existeRegistro = reporteAccesoBD.existeDuplicado(identificadorUsuario, cbTipoReporte.getValue(), mesParaVerificar, EstadoReporte.GENERADO);
        if (existeRegistro) {
            String mensajeAdvertencia = "Ya generaste tu reporte parcial.";
            if (cbTipoReporte.getValue() == TipoReporte.MENSUAL) {
                mensajeAdvertencia = "Ya generaste el reporte de " + mesParaVerificar + ".";
            }
            mostrarAlerta(Alert.AlertType.WARNING, "Reporte duplicado", mensajeAdvertencia);
        }

        return existeRegistro;
    }

    private void mostrarAlerta(Alert.AlertType tipoAlerta, String tituloAlerta, String mensajeAlerta) {
        Alert ventanaAlerta = new Alert(tipoAlerta);
        ventanaAlerta.setTitle(tituloAlerta);
        ventanaAlerta.setHeaderText(null);
        ventanaAlerta.setContentText(mensajeAlerta);
        ventanaAlerta.showAndWait();
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void regresar(ActionEvent eventoClic) {
        if (escenaAnterior != null) {
            Stage escenarioActual = (Stage) ((Node) eventoClic.getSource()).getScene().getWindow();
            escenarioActual.setScene(escenaAnterior);
            escenarioActual.show();
        }
    }
}