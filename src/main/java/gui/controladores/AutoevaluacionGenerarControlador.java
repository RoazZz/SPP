/*
 * AutoevaluacionGenerarControlador.java
 * Versión 1.0
 * Fecha: 18/05/26
 * Copyright (c) 2026
 */
package gui.controladores;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import excepciones.DAOExcepcion;
import logica.interfaces.Regresable;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
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
    @FXML private Button btnCancelar;
    @FXML private Button btnGenerar;

    private Scene escenaAnterior;

    private static final Logger LOGGER = Logger.getLogger(AutoevaluacionGenerarControlador.class.getName());
    private static final List<Integer> PUNTAJES = List.of(1, 2, 3, 4, 5);
    private static final String[] AFIRMACIONES = {
            "Cumplí con el horario y las actividades asignadas.",
            "Apliqué los conocimientos adquiridos en la universidad.",
            "Mostré responsabilidad y compromiso con la organización.",
            "Me comuniqué de manera efectiva con mi equipo de trabajo.",
            "Resolví problemas de manera autónoma durante las prácticas.",
            "Entregué los reportes y documentos en tiempo y forma.",
            "Mantuve una actitud profesional durante toda la práctica.",
            "Aprendí nuevas habilidades durante el periodo de prácticas."
    };

    @FXML
    public void initialize() {
        configurarComboBoxes();
        btnGenerar.setOnAction(e -> manejarGenerar());
        btnCancelar.setOnAction(e -> manejarCancelar());
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
        return suma.divide(totalPosible, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.TEN);
    }

    private void generarPDF(String matricula, BigDecimal calificacion, Path rutaArchivo) throws IOException {
        PdfWriter pdfWriter = new PdfWriter(rutaArchivo.toString());
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document documento = new Document(pdfDocument);

        documento.add(new Paragraph("AUTOEVALUACIÓN DE PRÁCTICAS PROFESIONALES")
                .setBold().setFontSize(16));
        documento.add(new Paragraph("Matrícula: " + matricula));
        documento.add(new Paragraph("Fecha: " + LocalDate.now()));
        documento.add(new Paragraph(" "));
        documento.add(new Paragraph("AFIRMACIONES:").setBold());

        List<ComboBox<Integer>> comboBoxes = obtenerComboBoxes();
        for (int indice = 0; indice < AFIRMACIONES.length; indice++) {
            documento.add(new Paragraph(
                    (indice + 1) + ". " + AFIRMACIONES[indice] +
                            " — Puntaje: " + comboBoxes.get(indice).getValue()
            ));
        }

        documento.add(new Paragraph(" "));
        documento.add(new Paragraph("Calificación: " + calificacion.toPlainString()).setBold());
        documento.add(new Paragraph(" "));
        documento.add(new Paragraph("Comentarios:").setBold());
        documento.add(new Paragraph(txtComentarios.getText().trim()));
        documento.add(new Paragraph(" "));
        documento.add(new Paragraph("Firma del Practicante: ____________________________"));

        documento.close();
    }

    private void guardarAutoevaluacion(String matricula, BigDecimal calificacion) throws DAOExcepcion {
        AutoevaluacionDTO autoevaluacionDTO = new AutoevaluacionDTO(
                0,
                matricula,
                calificacion,
                txtComentarios.getText().trim()
        );
        AutoevaluacionDAO autoevaluacionDAO = new AutoevaluacionDAO();
        autoevaluacionDAO.agregarautoevaluacion(autoevaluacionDTO);
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

            generarPDF(matricula, calificacion, rutaArchivo);
            guardarAutoevaluacion(matricula, calificacion);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Autoevaluación generada exitosamente.");
            regresar();
        } catch (IOException | DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al generar autoevaluación", e);
            manejarErrorGeneracion();
        }
    }

    private String obtenerMatricula() {
        return ((PracticanteDTO) SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual()).getMatricula();
    }

    private void manejarGenerar() {
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

    private void manejarCancelar() {
        Optional<ButtonType> respuesta = mostrarConfirmacion("¿Seguro que deseas cancelar?");

        if (respuesta.isPresent() && respuesta.get() == ButtonType.YES) {
            regresar();
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