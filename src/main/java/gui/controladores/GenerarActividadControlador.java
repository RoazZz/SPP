package gui.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class GenerarActividadControlador {

    @FXML private TextField  txtNombre;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaCierre;
    @FXML private TextArea   txtDescripcion;
    @FXML private Label      lblMensajeFormulario;
    @FXML private Label      lblMensaje;
    @FXML private TableView  tablaActividades;

    @FXML
    private void manejarGuardar(ActionEvent event) {
    }

    @FXML
    private void manejarLimpiar(ActionEvent event) {
        txtNombre.clear();
        txtDescripcion.clear();
        dpFechaInicio.setValue(null);
        dpFechaCierre.setValue(null);
        lblMensajeFormulario.setText("");
    }

    @FXML
    private void manejarSalir(ActionEvent event) {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }
}