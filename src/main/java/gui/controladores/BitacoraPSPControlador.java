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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static gui.controladores.NavegacionControlador.regresar;

public class BitacoraPSPControlador implements Initializable, Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(BitacoraPSPControlador.class.getName());

    @FXML private TextField txtMatricula;
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
            String carpetaPracticante = txtMatricula.getText().trim();
            Path carpetaDestino = Paths.get(System.getProperty("user.dir"), "BitacorasPSP", carpetaPracticante);
            Files.createDirectories(carpetaDestino);

            Path archivoDestino = carpetaDestino.resolve(archivoSeleccionado.getName());
            if (Files.exists(archivoDestino)) {
                lblError.setText("Ya existe una bitácora con ese nombre en tu carpeta.");
                lblError.setVisible(true);
                return;
            }
            Files.copy(archivoSeleccionado.toPath(), archivoDestino, StandardCopyOption.REPLACE_EXISTING);

            BitacoraPSPDTO bitacoraPSPDTO = new BitacoraPSPDTO(
                    0,
                    txtMatricula.getText().trim(),
                    dpFecha.getValue());

            BitacoraPSPDAO bitacoraPSPDAO = new BitacoraPSPDAO();
            bitacoraPSPDAO.agregarBitacoraPSP(bitacoraPSPDTO);

            Alert exito = new Alert(Alert.AlertType.INFORMATION);
            exito.setTitle("Operación exitosa");
            exito.setHeaderText(null);
            exito.setContentText("Archivo cargado exitosamente.");
            exito.showAndWait();

            regresar(lblError, escenaAnterior);

        } catch (IOException excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al copiar el archivo de bitácora PSP", excepcionCapturada);
            lblError.setText("No se pudo guardar el archivo. Intente más tarde.");
            lblError.setVisible(true);
        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al registrar la bitácora PSP en la base de datos", excepcionCapturada);
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error del sistema");
            alerta.setHeaderText(null);
            alerta.setContentText("No se pudo registrar la bitácora. Intente más tarde.");
            alerta.getButtonTypes().setAll(ButtonType.OK);
            alerta.showAndWait();
        }
    }

    private String validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtMatricula.getText().trim().isEmpty()) {
            errores.append("La matrícula no puede estar vacía.\n");
        }
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