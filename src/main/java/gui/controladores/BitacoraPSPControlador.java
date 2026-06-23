package gui.controladores;

import excepciones.DAOExcepcion;
import logica.interfaces.Regresable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import logica.dao.BitacoraPSPDAO;
import logica.dto.BitacoraPSPDTO;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import logica.dto.PracticanteDTO;
import logica.utilidades.GestorDocumento;
import logica.utilidades.RegistradorBitacora;
import logica.utilidades.SesionUsuarioSingleton;

import static gui.controladores.NavegacionControlador.regresar;

public class BitacoraPSPControlador implements Initializable, Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(BitacoraPSPControlador.class.getName());

    @FXML private DatePicker dpFecha;
    @FXML private TextField txtRuta;
    @FXML private Label lblError;
    @FXML private Button btnGuardar;

    private Scene escenaAnterior;
    private File archivoSeleccionado = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblError.setVisible(false);
    }

    @FXML
    private void seleccionarArchivo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo de Bitácora PSP");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Documentos PDF", "*.pdf")
        );

        File archivo = fileChooser.showOpenDialog(btnGuardar.getScene().getWindow());
        if (archivo != null) {
            archivoSeleccionado = archivo;
            txtRuta.setText(archivo.getName());
        }
    }

    @FXML
    private void manejarGuardar() {
        lblError.setVisible(false);

        String mensajeValidacion = validarCampos();
        if (mensajeValidacion != null) {
            lblError.setText(mensajeValidacion);
            lblError.setVisible(true);
            return;
        }

        String matricula = obtenerMatriculaSesion();
        if (matricula == null) {
            lblError.setText("No se pudo obtener la matrícula de la sesión.");
            lblError.setVisible(true);
            return;
        }

        try {
            if (!GestorDocumento.practicanteTieneProyectoAceptado(matricula)) {
                lblError.setText("No puedes registrar una bitácora sin tener un proyecto aceptado.");
                lblError.setVisible(true);
                return;
            }
        } catch (DAOExcepcion daoExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al verificar proyecto del practicante", daoExcepcion);
            lblError.setText("No se pudo verificar el estado de tu proyecto. Intente más tarde.");
            lblError.setVisible(true);
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar operación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de continuar con la operación?");
        confirmacion.getButtonTypes().setAll(ButtonType.CANCEL, ButtonType.OK);

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }

        try {
            Path carpetaDestino = GestorDocumento.construirRutaBitacora(matricula);

            if (GestorDocumento.existeDocumentoEnCarpeta(carpetaDestino) && !GestorDocumento.confirmarReemplazo()) {
                return;
            }

            GestorDocumento.guardarDocumento(carpetaDestino, archivoSeleccionado);

            BitacoraPSPDTO bitacoraPSPDTO = new BitacoraPSPDTO(0, matricula, dpFecha.getValue());
            BitacoraPSPDAO bitacoraPSPDAO = new BitacoraPSPDAO();
            bitacoraPSPDAO.agregarBitacoraPSP(bitacoraPSPDTO);
            RegistradorBitacora.registrar("REGISTRO_BITACORA_PSP", "Registró su bitácora de actividades de la matrícula: " + matricula);
            Alert exito = new Alert(Alert.AlertType.INFORMATION);
            exito.setTitle("Operación exitosa");
            exito.setHeaderText(null);
            exito.setContentText("Archivo cargado exitosamente.");
            exito.showAndWait();

            regresar(lblError, escenaAnterior);

        } catch (IOException ioExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al copiar el archivo de bitácora PSP", ioExcepcion);
            lblError.setText("No se pudo guardar el archivo. Intente más tarde.");
            lblError.setVisible(true);
        } catch (DAOExcepcion daoExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al registrar la bitácora PSP en la base de datos", daoExcepcion);
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error del sistema");
            alerta.setHeaderText(null);
            alerta.setContentText("No se pudo registrar la bitácora. Intente más tarde.");
            alerta.getButtonTypes().setAll(ButtonType.OK);
            alerta.showAndWait();
        }
    }

    private String obtenerMatriculaSesion() {
        Object usuarioActual = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual();
        if (usuarioActual instanceof PracticanteDTO practicante) {
            return practicante.getMatricula();
        }
        return null;
    }

    private String validarCampos() {
        StringBuilder errores = new StringBuilder();
        if (dpFecha.getValue() == null) {
            errores.append("Debe seleccionar una fecha.\n");
        }
        if (archivoSeleccionado == null) {
            errores.append("Debe seleccionar un archivo.\n");
        }

        boolean hayErrores = errores.length() > 0;
        return hayErrores ? errores.toString() : null;
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void manejarClicCancelar(ActionEvent eventoBoton) {
        Node nodoOrigenDeAtras = (Node) eventoBoton.getSource();
        regresar(nodoOrigenDeAtras, this.escenaAnterior);
    }
}