package gui.controladores;

import logica.interfaces.Regresable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logica.dto.PracticanteDTO;
import logica.utilidades.SesionUsuarioSingleton;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlanDeActividadesControlador implements Initializable, Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(PlanDeActividadesControlador.class.getName());
    private static final Path RUTA_BASE_PLANES = Paths.get(System.getProperty("user.dir"), "PlanesDeActividades");

    @FXML private Label lblNombreArchivo;
    @FXML private Label lblValidacionArchivo;
    @FXML private HBox hboxValidacionArchivo;
    @FXML private Button btnVistaPrevia;
    @FXML private Button btnGuardar;

    private Scene escenaAnterior;
    private File archivoSeleccionado;
    private PracticanteDTO practicanteLogueado;

    @Override
    public void initialize(URL urlRecibida, ResourceBundle recursoRecibido) {
        if (SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual() instanceof PracticanteDTO practicanteActivo) {
            this.practicanteLogueado = practicanteActivo;
        } else {
            manejarAccesoDenegado("El usuario no es un Practicante.", Level.SEVERE);
        }
    }

    private void manejarAccesoDenegado(String mensajeError, Level nivelRegistro) {
        REGISTRADOR.log(nivelRegistro, "Acceso denegado en controlador: " + mensajeError);
        mostrarAlerta(Alert.AlertType.ERROR, "Error de Permisos", mensajeError);
    }

    @FXML
    private void manejarSeleccionArchivo(ActionEvent eventoClic) {
        FileChooser selectorArchivos = new FileChooser();
        selectorArchivos.setTitle("Seleccionar Plan de Actividades");
        selectorArchivos.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

        Stage escenarioActual = (Stage) ((Node) eventoClic.getSource()).getScene().getWindow();
        File archivoEscogido = selectorArchivos.showOpenDialog(escenarioActual);

        if (archivoEscogido != null) {
            archivoSeleccionado = archivoEscogido;
            lblNombreArchivo.setText(archivoEscogido.getName());
            actualizarEstadoInterfaz(true);
        }
    }

    private void actualizarEstadoInterfaz(boolean archivoCargado) {
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
    private void manejarGuardar(ActionEvent eventoClic) {
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
            regresar(eventoClic);
        } catch (IOException excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error crítico de E/S al copiar el archivo para la matrícula: " + practicanteLogueado.getMatricula(), excepcionCapturada);
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Escritura", "Ocurrió un problema físico al guardar el archivo.");
        }
    }

    private File obtenerDirectorioAlmacenamiento() {
        String nombreFormateado = practicanteLogueado.getNombre().trim().replace(" ", "_");
        String matriculaFormateada = practicanteLogueado.getMatricula().trim();
        String carpetaPracticante = nombreFormateado + "_" + matriculaFormateada;
        return new File(RUTA_BASE_PLANES.toFile(), carpetaPracticante);
    }

    private boolean asegurarExistenciaDeDirectorio(File directorioObjetivo) {
        boolean existeDirectorio = true;
        if (!directorioObjetivo.exists()) {
            existeDirectorio = directorioObjetivo.mkdirs();
        }
        return existeDirectorio;
    }

    private boolean confirmarReemplazo(String nombreArchivoNuevo) {
        return mostrarAlertaConfirmacion("Archivo duplicado", "El archivo '" + nombreArchivoNuevo + "' ya existe en tu carpeta.\n\n¿Deseas reemplazarlo por el nuevo?");
    }

    private boolean mostrarAlertaConfirmacion(String tituloAlerta, String mensajeAlerta) {
        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle(tituloAlerta);
        alertaConfirmacion.setHeaderText(null);
        alertaConfirmacion.setContentText(mensajeAlerta);

        Optional<ButtonType> resultadoUsuario = alertaConfirmacion.showAndWait();
        boolean fueConfirmado = false;
        if (resultadoUsuario.isPresent() && resultadoUsuario.get() == ButtonType.OK) {
            fueConfirmado = true;
        }
        return fueConfirmado;
    }

    private void mostrarAlerta(Alert.AlertType tipoAlerta, String tituloAlerta, String mensajeAlerta) {
        Alert ventanaAlerta = new Alert(tipoAlerta);
        ventanaAlerta.setTitle(tituloAlerta);
        ventanaAlerta.setHeaderText(null);
        ventanaAlerta.setContentText(mensajeAlerta);
        ventanaAlerta.showAndWait();
    }

    @FXML
    private void manejarVistaPrevia(ActionEvent eventoClic) {
        if (archivoSeleccionado == null) {
            return;
        }
        try {
            Desktop.getDesktop().open(archivoSeleccionado);
        } catch (IOException excepcionCapturada) {
            REGISTRADOR.log(Level.WARNING, "No se pudo abrir la vista previa del PDF", excepcionCapturada);
            mostrarAlerta(Alert.AlertType.WARNING, "Vista previa no disponible", "No se pudo abrir el visor de PDF predeterminado.");
        }
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
        }
    }
}