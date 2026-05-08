package gui.controladores;

import excepciones.DAOExcepcion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logica.dao.BitacoraPSPDAO;
import logica.dto.BitacoraPSPDTO;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BitacoraPSPControlador implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(BitacoraPSPControlador.class.getName());

    @FXML private TextField txtMatricula;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtRuta;
    @FXML private Label lblError;
    @FXML private Button btnGuardar;

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

        try {
            Path carpetaDestino = Paths.get(System.getProperty("user.dir"), "BitacorasPSP");
            Files.createDirectories(carpetaDestino);
            Path archivoDestino = carpetaDestino.resolve(archivoSeleccionado.getName());
            Files.copy(archivoSeleccionado.toPath(), archivoDestino, StandardCopyOption.REPLACE_EXISTING);

            String rutaRelativa = Paths.get("BitacorasPSP", archivoSeleccionado.getName()).toString();

            BitacoraPSPDTO bitacoraPSPDTO = new BitacoraPSPDTO(
                    0,
                    txtMatricula.getText().trim(),
                    dpFecha.getValue()
            );

            BitacoraPSPDAO bitacoraPSPDAO = new BitacoraPSPDAO();
            bitacoraPSPDAO.agregarBitacoraPSP(bitacoraPSPDTO);

            cerrarVentana();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al copiar archivo de bitácora", e);
            lblError.setText("No se pudo guardar el archivo. Intente más tarde.");
            lblError.setVisible(true);
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al guardar bitácora PSP en BD", e);
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error del sistema");
            alerta.setHeaderText(null);
            alerta.setContentText("No se pudo registrar la bitácora. Intente más tarde.");
            alerta.getButtonTypes().setAll(ButtonType.OK);
            alerta.showAndWait();
        }
    }

    @FXML
    private void manejarCancelar() {
        cerrarVentana();
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

    private void cerrarVentana() {
        ((Stage) btnGuardar.getScene().getWindow()).close();
    }
}