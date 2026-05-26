package gui.controladores;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import logica.interfaces.Regresable;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import logica.dto.PracticanteDTO;
import logica.utilidades.SesionUsuarioSingleton;

public class HorarioControlador implements Regresable {

    private static final Logger LOGGER = Logger.getLogger(HorarioControlador.class.getName());

    private static final String NOMBRE_CARPETA_RAIZ = "Horarios";
    private static final String EXTENSION_PDF = ".pdf";
    private static final String FILTRO_PDF_DESCRIPCION = "Documentos PDF";
    private static final String FILTRO_PDF_PATRON = "*.pdf";

    @FXML private Button btnSeleccionar;
    @FXML private Button btnVistaPrevia;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    @FXML private Label lblNombreArchivo;
    @FXML private Label lblValidacionArchivo;

    @FXML private HBox hboxValidacionArchivo;

    private File archivoPdfSeleccionado;
    private Scene escenaAnterior;

    @FXML
    public void initialize() {
        btnSeleccionar.setOnAction(evento -> seleccionarArchivoPdf());
        btnVistaPrevia.setOnAction(evento -> mostrarVistaPreviaPdf());
        btnGuardar.setOnAction(evento -> guardarHorario());
        btnCancelar.setOnAction(evento -> regresar());

        restablecerEstadoInicial();
    }

    private void restablecerEstadoInicial() {
        archivoPdfSeleccionado = null;
        lblNombreArchivo.setText("Ningún archivo seleccionado");
        ocultarElemento(hboxValidacionArchivo);
        ocultarElemento(btnVistaPrevia);
        btnGuardar.setDisable(true);
    }

    private void seleccionarArchivoPdf() {
        FileChooser selectorArchivo = new FileChooser();
        selectorArchivo.setTitle("Seleccionar Horario");
        selectorArchivo.getExtensionFilters().add(
                new ExtensionFilter(FILTRO_PDF_DESCRIPCION, FILTRO_PDF_PATRON));

        File archivoElegido = selectorArchivo.showOpenDialog(
                lblNombreArchivo.getScene().getWindow());

        if (archivoElegido == null) {
            return;
        }

        if (!esArchivoPdfValido(archivoElegido)) {
            mostrarAlerta(AlertType.WARNING,
                    "Archivo no válido",
                    "El archivo seleccionado no es un PDF válido.");
            return;
        }

        archivoPdfSeleccionado = archivoElegido;
        lblNombreArchivo.setText(archivoElegido.getName());

        mostrarElemento(hboxValidacionArchivo);
        mostrarElemento(btnVistaPrevia);
        btnGuardar.setDisable(false);
    }

    private boolean esArchivoPdfValido(File archivo) {
        return archivo.exists()
                && archivo.isFile()
                && archivo.getName().toLowerCase().endsWith(EXTENSION_PDF);
    }

    private void mostrarVistaPreviaPdf() {
        if (archivoPdfSeleccionado == null) {
            return;
        }
        try {
            Desktop.getDesktop().open(archivoPdfSeleccionado);
        } catch (IOException excepcion) {
            LOGGER.log(Level.WARNING, "No se pudo abrir la vista previa del PDF", excepcion);
            mostrarAlerta(AlertType.WARNING,
                    "Vista previa no disponible",
                    "No se pudo abrir el visor de PDF predeterminado.");
        }
    }

    private void guardarHorario() {
        if (archivoPdfSeleccionado == null) {
            return;
        }

        String matriculaUsuario = obtenerMatriculaUsuarioActual();
        if (matriculaUsuario == null || matriculaUsuario.isBlank()) {
            mostrarAlerta(AlertType.ERROR,
                    "Sesión no válida",
                    "No se pudo obtener la matrícula del usuario actual.");
            return;
        }

        Path carpetaMatricula = construirRutaCarpetaMatricula(matriculaUsuario);

        try {
            if (existeHorarioPrevio(carpetaMatricula) && !confirmarReemplazoHorarioExistente()) {
                return;
            }

            if (!confirmarSubidaHorario()) {
                return;
            }

            Path rutaArchivoGuardado = copiarHorarioACarpetaDestino(carpetaMatricula);

            mostrarAlerta(AlertType.INFORMATION,
                    "Éxito",
                    "Horario guardado correctamente en:\n" + rutaArchivoGuardado);
            regresar();

        } catch (IOException excepcion) {
            LOGGER.log(Level.SEVERE, "Error al guardar el horario en disco", excepcion);
            mostrarAlerta(AlertType.ERROR,
                    "Error al guardar",
                    "No se pudo guardar el horario en el servidor local.");
        }
    }

    private String obtenerMatriculaUsuarioActual() {
        Object usuarioActual = SesionUsuarioSingleton
                .obtenerInstancia()
                .obtenerUsuarioActual();

        if (!(usuarioActual instanceof PracticanteDTO practicanteActual)) {
            LOGGER.log(Level.WARNING,
                    "El usuario en sesión no es un Practicante: {0}",
                    usuarioActual != null ? usuarioActual.getClass().getName() : "null");
            return null;
        }

        return practicanteActual.getMatricula();
    }

    private Path construirRutaCarpetaMatricula(String matricula) {
        return Paths.get(System.getProperty("user.dir"), NOMBRE_CARPETA_RAIZ, matricula);
    }

    private boolean existeHorarioPrevio(Path carpetaMatricula) throws IOException {
        if (!Files.exists(carpetaMatricula)) {
            return false;
        }
        try (Stream<Path> archivos = Files.list(carpetaMatricula)) {
            return archivos.anyMatch(this::esArchivoPdf);
        }
    }

    private boolean esArchivoPdf(Path ruta) {
        return Files.isRegularFile(ruta)
                && ruta.getFileName().toString().toLowerCase().endsWith(EXTENSION_PDF);
    }

    private boolean confirmarReemplazoHorarioExistente() {
        Alert alertaReemplazo = new Alert(AlertType.CONFIRMATION,
                "Ya existe un horario registrado para tu matrícula.\n"
                        + "Solo puedes tener un horario subido a la vez.\n\n"
                        + "¿Deseas reemplazarlo por el nuevo archivo?",
                ButtonType.YES, ButtonType.NO);
        alertaReemplazo.setHeaderText("Horario existente");
        alertaReemplazo.setTitle("Horario ya registrado");

        Optional<ButtonType> respuesta = alertaReemplazo.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.YES;
    }

    private boolean confirmarSubidaHorario() {
        Alert alertaConfirmacion = new Alert(AlertType.CONFIRMATION,
                "¿Está seguro de subir este horario?",
                ButtonType.YES, ButtonType.NO);
        alertaConfirmacion.setHeaderText(null);
        alertaConfirmacion.setTitle("Confirmar subida");

        Optional<ButtonType> respuesta = alertaConfirmacion.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.YES;
    }

    private Path copiarHorarioACarpetaDestino(Path carpetaMatricula) throws IOException {
        if (!Files.exists(carpetaMatricula)) {
            Files.createDirectories(carpetaMatricula);
        } else {
            eliminarHorariosPreviosEnCarpeta(carpetaMatricula);
        }

        Path archivoDestino = carpetaMatricula.resolve(archivoPdfSeleccionado.getName());
        Files.copy(archivoPdfSeleccionado.toPath(), archivoDestino,
                StandardCopyOption.REPLACE_EXISTING);
        return archivoDestino;
    }

    private void eliminarHorariosPreviosEnCarpeta(Path carpetaMatricula) throws IOException {
        try (Stream<Path> archivos = Files.list(carpetaMatricula)) {
            for (Path archivo : archivos.filter(this::esArchivoPdf).toList()) {
                Files.deleteIfExists(archivo);
            }
        }
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
    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarElemento(javafx.scene.Node nodo) {
        nodo.setVisible(true);
        nodo.setManaged(true);
    }

    private void ocultarElemento(javafx.scene.Node nodo) {
        nodo.setVisible(false);
        nodo.setManaged(false);
    }
}
