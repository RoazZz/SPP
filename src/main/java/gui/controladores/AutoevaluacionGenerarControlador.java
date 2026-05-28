package gui.controladores;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import excepciones.DAOExcepcion;
import javafx.event.ActionEvent;
import logica.interfaces.Regresable;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import logica.dao.AutoevaluacionDAO;
import logica.dto.AutoevaluacionDTO;
import logica.dto.PracticanteDTO;
import logica.utilidades.SesionUsuarioSingleton;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static gui.controladores.NavegacionControlador.regresar;

public class AutoevaluacionGenerarControlador implements Regresable {

    @FXML private ComboBox<Integer> cbAfirmacion1;
    @FXML private ComboBox<Integer> cbAfirmacion2;
    @FXML private ComboBox<Integer> cbAfirmacion3;
    @FXML private ComboBox<Integer> cbAfirmacion4;
    @FXML private ComboBox<Integer> cbAfirmacion5;
    @FXML private ComboBox<Integer> cbAfirmacion6;
    @FXML private ComboBox<Integer> cbAfirmacion7;
    @FXML private ComboBox<Integer> cbAfirmacion8;
    @FXML private TextArea txtComentarios;
    @FXML private Label lblError;

    private Scene escenaAnterior;

    private static final Logger REGISTRADOR = Logger.getLogger(AutoevaluacionGenerarControlador.class.getName());
    private static final List<Integer> PUNTAJES = List.of(1, 2, 3, 4, 5);
    private static final List<String> AFIRMACIONES =
            List.of(
                    "Cumplí con el horario y las actividades asignadas.",
                    "Apliqué los conocimientos adquiridos en la universidad.",
                    "Mostré responsabilidad y compromiso con la organización.",
                    "Me comuniqué de manera efectiva con mi equipo de trabajo.",
                    "Resolví problemas de manera autónoma durante las prácticas.",
                    "Entregué los reportes y documentos en tiempo y forma.",
                    "Mantuve una actitud profesional durante toda la práctica.",
                    "Aprendí nuevas habilidades durante el periodo de prácticas."
            );

    @FXML
    public void initialize() {
        configurarComboBoxes();
    }

    private void configurarComboBoxes() {
        List<ComboBox<Integer>> comboBoxes = obtenerComboBoxes();

        for (ComboBox<Integer> comboBox : comboBoxes) {
            comboBox.setItems(FXCollections.observableArrayList(PUNTAJES));
        }
    }

    private List<ComboBox<Integer>> obtenerComboBoxes() {
        return List.of(
                cbAfirmacion1, cbAfirmacion2, cbAfirmacion3, cbAfirmacion4,
                cbAfirmacion5, cbAfirmacion6, cbAfirmacion7, cbAfirmacion8
        );
    }

    private String validarCampos() {
        List<ComboBox<Integer>> comboBoxes = obtenerComboBoxes();

        for (int indice = 0; indice < comboBoxes.size(); indice++) {
            if (comboBoxes.get(indice).getValue() == null) {
                return "Debes seleccionar un puntaje para la afirmación " + (indice + 1) + ".";
            }
        }

        if (txtComentarios.getText().trim().isEmpty()) {
            return "Los comentarios son obligatorios.";
        }

        return null;
    }

    private BigDecimal calcularCalificacion() {
        List<ComboBox<Integer>> comboBoxes = obtenerComboBoxes();
        BigDecimal suma = BigDecimal.ZERO;

        for (ComboBox<Integer> comboBox : comboBoxes) {
            suma = suma.add(BigDecimal.valueOf(comboBox.getValue()));
        }

        BigDecimal totalPosible = BigDecimal.valueOf((long) comboBoxes.size() * 5);
        return suma.divide(totalPosible, 2, RoundingMode.HALF_UP).multiply(BigDecimal.TEN);
    }

    private void generarDocumentoPDF(String matriculaPracticante, BigDecimal calificacionCalculada, Path rutaArchivoDestino) throws IOException {
        try (
                PdfWriter escritorPdf = new PdfWriter(rutaArchivoDestino.toString());
                PdfDocument documentoPdf = new PdfDocument(escritorPdf);
                Document documentoVisual = new Document(documentoPdf)
        ) {
            documentoVisual.add(new Paragraph("AUTOEVALUACIÓN DE PRÁCTICAS PROFESIONALES").setBold().setFontSize(16));
            documentoVisual.add(new Paragraph("Matrícula: " + matriculaPracticante));
            documentoVisual.add(new Paragraph("Fecha: " + LocalDate.now()));
            documentoVisual.add(new Paragraph(" "));
            documentoVisual.add(new Paragraph("AFIRMACIONES:").setBold());

            List<ComboBox<Integer>> listaDeComboBoxes = obtenerComboBoxes();

            for (int indiceCiclo = 0; indiceCiclo < AFIRMACIONES.size(); indiceCiclo++) {
                documentoVisual.add(new Paragraph(
                        (indiceCiclo + 1) + ". " + AFIRMACIONES.get(indiceCiclo) +
                                " — Puntaje: " + listaDeComboBoxes.get(indiceCiclo).getValue()
                ));
            }

            documentoVisual.add(new Paragraph(" "));
            documentoVisual.add(new Paragraph("Calificación: " + calificacionCalculada.toPlainString()).setBold());
            documentoVisual.add(new Paragraph(" "));
            documentoVisual.add(new Paragraph("Comentarios:").setBold());
            documentoVisual.add(new Paragraph(txtComentarios.getText().trim()));
            documentoVisual.add(new Paragraph(" "));
            documentoVisual.add(new Paragraph("Firma del Practicante: ____________________________"));
        }
    }

    private void guardarAutoevaluacion(String matricula, BigDecimal calificacion) throws DAOExcepcion {
        AutoevaluacionDTO autoevaluacionDTO = new AutoevaluacionDTO(
                0,
                matricula,
                calificacion,
                txtComentarios.getText().trim()
        );
        AutoevaluacionDAO autoevaluacionDAO = new AutoevaluacionDAO();
        autoevaluacionDAO.agregarAutoevaluacion(autoevaluacionDTO);
    }

    private void procesarGeneracion() {
        try {
            String matricula = obtenerMatricula();
            BigDecimal calificacion = calcularCalificacion();

            Path carpetaDestino = Paths.get(
                    System.getProperty("user.dir"), "Autoevaluaciones", matricula
            );

            if (!Files.exists(carpetaDestino)) {
                Files.createDirectories(carpetaDestino);
            }

            String nombreArchivo = "Autoevaluacion_" + matricula + "_" + System.currentTimeMillis() + ".pdf";
            Path rutaArchivo = carpetaDestino.resolve(nombreArchivo);

            generarDocumentoPDF(matricula, calificacion, rutaArchivo);
            guardarAutoevaluacion(matricula, calificacion);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Autoevaluación generada exitosamente.");
            regresar(lblError, escenaAnterior);
        } catch (IOException | DAOExcepcion e) {
            REGISTRADOR.log(Level.SEVERE, "Error al generar autoevaluación", e);
            manejarErrorGeneracion();
        }
    }

    private String obtenerMatricula() {
        return ((PracticanteDTO) SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual()).getMatricula();
    }

    @FXML
    private void manejarGenerar(ActionEvent evento) {
        ocultarError();

        String errorValidacion = validarCampos();
        if (errorValidacion != null) {
            mostrarError(errorValidacion);
            return;
        }

        Optional<ButtonType> respuesta = mostrarConfirmacion("¿Desea generar la autoevaluación?");

        if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
            procesarGeneracion();
        }
    }

    @FXML
    private void manejarCancelar(ActionEvent evento) {
        Optional<ButtonType> respuesta = mostrarConfirmacion("¿Seguro que deseas cancelar?");

        if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
            regresar(lblError, escenaAnterior);
        }
    }

    private void manejarErrorGeneracion() {
        ButtonType intentarDeNuevo = new ButtonType("Intentar de nuevo");

        Alert alerta = new Alert(
                Alert.AlertType.ERROR,
                "Error al generar el archivo PDF de la autoevaluación."
        );
        alerta.getButtonTypes().setAll(intentarDeNuevo, ButtonType.CANCEL);
        alerta.setHeaderText(null);

        Optional<ButtonType> respuesta = alerta.showAndWait();

        if (respuesta.isPresent() && respuesta.get() == intentarDeNuevo) {
            procesarGeneracion();
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

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }
}