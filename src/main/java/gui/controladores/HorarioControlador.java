package gui.controladores;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javafx.event.ActionEvent;
import javafx.scene.Node;
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
import excepciones.DAOExcepcion;
import logica.utilidades.GestorDocumento;

import logica.dto.PracticanteDTO;
import logica.utilidades.RegistradorBitacora;
import logica.utilidades.SesionUsuarioSingleton;

import static gui.controladores.NavegacionControlador.regresar;

public class HorarioControlador implements Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(HorarioControlador.class.getName());
    private static final String EXTENSION_PDF = ".pdf";
    private static final String FILTRO_PDF_DESCRIPCION = "Documentos PDF";
    private static final String FILTRO_PDF_PATRON = "*.pdf";

    @FXML private Button btnSeleccionar;
    @FXML private Button btnVistaPrevia;
    @FXML private Label lblNombreArchivo;
    @FXML private Button btnGuardar;
    @FXML private HBox hboxValidacionArchivo;

    private File archivoPdfSeleccionado;
    private Scene escenaAnterior;

    @FXML
    public void initialize() {
        btnSeleccionar.setOnAction(eventoClic -> seleccionarArchivoPdf());
        btnVistaPrevia.setOnAction(eventoClic -> mostrarVistaPreviaPdf());

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
        selectorArchivo.getExtensionFilters().add(new ExtensionFilter(FILTRO_PDF_DESCRIPCION, FILTRO_PDF_PATRON));

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

    private boolean esArchivoPdfValido(File archivoRecibido) {
        boolean esValido = false;
        if (archivoRecibido.exists() && archivoRecibido.isFile()) {
            if (archivoRecibido.getName().toLowerCase().endsWith(EXTENSION_PDF)) {
                esValido = true;
            }
        }
        return esValido;
    }

    private void mostrarVistaPreviaPdf() {
        if (archivoPdfSeleccionado != null) {
            try {
                Desktop.getDesktop().open(archivoPdfSeleccionado);
            } catch (IOException excepcionCapturada) {
                REGISTRADOR.log(Level.WARNING, "No se pudo abrir la vista previa del PDF", excepcionCapturada);
                mostrarAlerta(AlertType.WARNING,
                        "Vista previa no disponible",
                        "No se pudo abrir el visor de PDF predeterminado.");
            }
        }
    }

    @FXML
    private void guardarHorario(ActionEvent eventoClic) {
        if (archivoPdfSeleccionado == null) {
            return;
        }

        String matriculaUsuario = obtenerMatriculaUsuarioActual();
        if (matriculaUsuario == null || matriculaUsuario.isBlank()) {
            mostrarAlerta(AlertType.ERROR, "Sesión no válida", "No se pudo obtener la matrícula del usuario actual.");
            return;
        }

        try {
            if (!GestorDocumento.practicanteTieneProyectoAceptado(matriculaUsuario)) {
                mostrarAlerta(AlertType.WARNING, "Sin proyecto asignado", "No puedes subir el horario sin tener un proyecto aceptado.");
                return;
            }

            Path carpetaMatricula = GestorDocumento.construirRutaHorario(matriculaUsuario);

            if (GestorDocumento.existeDocumentoEnCarpeta(carpetaMatricula) && !GestorDocumento.confirmarReemplazo()) {
                return;
            }

            if (!GestorDocumento.confirmarSubida()) {
                return;
            }

            GestorDocumento.guardarDocumento(carpetaMatricula, archivoPdfSeleccionado);
            RegistradorBitacora.registrar("REGISTRO_HORARIO", "Registró su horario de la matrícula: " + matriculaUsuario);            mostrarAlerta(AlertType.INFORMATION, "Éxito", "Horario guardado correctamente.");
            regresar(lblNombreArchivo, escenaAnterior);

        } catch (IOException ioExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al guardar el horario en disco", ioExcepcion);
            mostrarAlerta(AlertType.ERROR, "Error al guardar", "No se pudo guardar el horario en el servidor local.");
        } catch (DAOExcepcion daoExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al verificar proyecto del practicante", daoExcepcion);
            mostrarAlerta(AlertType.ERROR, "Error", "No se pudo verificar el estado de tu proyecto.");
        }
    }

    private String obtenerMatriculaUsuarioActual() {
        Object usuarioActual = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual();
        String matriculaEncontrada = null;

        if (usuarioActual instanceof PracticanteDTO practicanteActual) {
            matriculaEncontrada = practicanteActual.getMatricula();
        } else {
            String tipoUsuarioDetectado = "null";
            if (usuarioActual != null) {
                tipoUsuarioDetectado = usuarioActual.getClass().getName();
            }
            REGISTRADOR.log(Level.WARNING, "El usuario en sesión no es un Practicante: " + tipoUsuarioDetectado);
        }

        return matriculaEncontrada;
    }

    private boolean esArchivoPdf(Path rutaRecibida) {
        return Files.isRegularFile(rutaRecibida)
                && rutaRecibida.getFileName().toString().toLowerCase().endsWith(EXTENSION_PDF);
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void manejarSalir(ActionEvent eventoBoton) {
        Node nodoOrigenDeAtras = (Node) eventoBoton.getSource();
        regresar(nodoOrigenDeAtras, this.escenaAnterior);
    }

    private void mostrarAlerta(AlertType tipoAlerta, String tituloAlerta, String mensajeAlerta) {
        Alert ventanaAlerta = new Alert(tipoAlerta);
        ventanaAlerta.setTitle(tituloAlerta);
        ventanaAlerta.setHeaderText(null);
        ventanaAlerta.setContentText(mensajeAlerta);
        ventanaAlerta.showAndWait();
    }

    private void mostrarElemento(Node nodoRecibido) {
        nodoRecibido.setVisible(true);
        nodoRecibido.setManaged(true);
    }

    private void ocultarElemento(Node nodoRecibido) {
        nodoRecibido.setVisible(false);
        nodoRecibido.setManaged(false);
    }
}