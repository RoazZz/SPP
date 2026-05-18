package gui.controladores;

import interfaces.Regresable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logica.dto.PracticanteDTO;
import logica.dto.UsuarioDTO;
import logica.utilidades.SesionUsuarioSingleton;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlanDeActividadesControlador implements Initializable, Regresable {

    private static final Logger LOGGER = Logger.getLogger(PlanDeActividadesControlador.class.getName());

    @FXML private Label lblNombreArchivo;
    @FXML private Label lblValidacionArchivo;
    @FXML private HBox hboxValidacionArchivo;
    @FXML private Button btnExaminarArchivos;
    @FXML private Button btnVistaPrevia;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Scene escenaAnterior;
    private File archivoSeleccionado;
    private PracticanteDTO practicanteLogueado;
    private static final String RUTA_BASE_PLANES = "D:/Escritorio/Java/SPP-Project";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UsuarioDTO usuarioBase = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual();

        if (usuarioBase instanceof PracticanteDTO) {
            this.practicanteLogueado = (PracticanteDTO) usuarioBase;
            configurarEventos();
        } else {
            manejarAccesoDenegado("El usuario no es un Practicante.", Level.SEVERE);
        }

        configurarEventos();
    }

    private void manejarAccesoDenegado(String mensaje, Level nivelLog) {
        LOGGER.log(nivelLog, "Acceso denegado en controlador: " + mensaje);
        mostrarAlerta(Alert.AlertType.ERROR, "Error de Permisos", mensaje);
        regresar();
    }

    private void configurarEventos() {
        btnExaminarArchivos.setOnAction(event -> manejarSeleccionArchivo());
        btnGuardar.setOnAction(event -> manejarGuardar());
        btnCancelar.setOnAction(event -> regresar());
        btnVistaPrevia.setOnAction(event -> manejarVistaPrevia());
    }

    private void manejarSeleccionArchivo() {
        FileChooser selectorArchivos = new FileChooser();
        selectorArchivos.setTitle("Seleccionar Plan de Actividades");
        selectorArchivos.getExtensionFilters().add( new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

        Stage escenario = (Stage) btnExaminarArchivos.getScene().getWindow();
        File archivo = selectorArchivos.showOpenDialog(escenario);
        if (archivo != null) {
            archivoSeleccionado = archivo;
            lblNombreArchivo.setText(archivo.getName());
            actualizarEstadoUI(true);
        }
    }

    private void actualizarEstadoUI(boolean archivoCargado) {
        btnGuardar.setDisable(!archivoCargado);
        btnVistaPrevia.setVisible(archivoCargado);
        btnVistaPrevia.setManaged(archivoCargado);
        hboxValidacionArchivo.setVisible(archivoCargado);
        hboxValidacionArchivo.setManaged(archivoCargado);
        if (archivoCargado) {
            lblValidacionArchivo.setText("Plan de Actividades listo para subir");
        }
    }

    @FXML
    private void manejarGuardar() {
        if (archivoSeleccionado == null || practicanteLogueado == null) {
            return;
        }

        File directorioDestino = obtenerDirectorioAlmacenamiento();

        if (!asegurarExistenciaDeDirectorio(directorioDestino)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron crear las carpetas de almacenamiento.");
            return;
        }

        File archivoDestino = new File(directorioDestino, archivoSeleccionado.getName());

        if (archivoDestino.exists() && !confirmarReemplazo(archivoSeleccionado.getName())) {
            return;
        }

        try {
            Files.copy(archivoSeleccionado.toPath(), archivoDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El Plan de Actividades se ha subido correctamente.");
            regresar();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error crítico de E/S al copiar el archivo para la matrícula: " + practicanteLogueado.getMatricula(), ex);
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Escritura", "Ocurrió un problema físico al guardar el archivo.");
        }
    }

    private File obtenerDirectorioAlmacenamiento() {
        String nombreFormateado = practicanteLogueado.getNombre().trim().replace(" ", "_");
        String matricula = practicanteLogueado.getMatricula().trim();
        String carpetaPracticante = nombreFormateado + "_" + matricula;
        return new File(RUTA_BASE_PLANES, carpetaPracticante);
    }

    private boolean asegurarExistenciaDeDirectorio(File directorio) {
        if (!directorio.exists()) {
            return directorio.mkdirs();
        }
        return true;
    }

    private boolean confirmarReemplazo(String nombreArchivo) {
        return mostrarAlertaConfirmacion("Archivo duplicado", "El archivo '" + nombreArchivo + "' ya existe en tu carpeta.\n\n¿Deseas reemplazarlo por el nuevo?");
    }

    private boolean mostrarAlertaConfirmacion(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        Optional<ButtonType> resultado = alerta.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void manejarVistaPrevia() {
        if (archivoSeleccionado == null) return;
        try {
            Desktop.getDesktop().open(archivoSeleccionado);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "No se pudo abrir la vista previa del PDF", e);
            mostrarAlerta(Alert.AlertType.WARNING, "Vista previa no disponible", "No se pudo abrir el visor de PDF predeterminado.");
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
        }
    }

}