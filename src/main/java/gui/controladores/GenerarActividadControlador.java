package gui.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logica.dto.ActividadDTO;
import logica.interfaces.Regresable;

import java.util.logging.Level;
import java.util.logging.Logger;

import static gui.controladores.NavegacionControlador.regresar;

public class GenerarActividadControlador implements Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(GenerarActividadControlador.class.getName());

    @FXML private TextField txtNombre;
    @FXML private DatePicker dpFechaInicio;
    @FXML private DatePicker dpFechaCierre;
    @FXML private TextArea txtDescripcion;
    @FXML private Label lblMensajeFormulario;
    @FXML private Label lblMensaje;
    @FXML private TableView<ActividadDTO> tablaActividades;

    private Scene escenaAnterior;

    @FXML
    private void manejarGuardar(ActionEvent eventoClic) {
        REGISTRADOR.log(Level.WARNING, "manejarGuardar invocado pero no implementado");
    }

    @FXML
    private void manejarLimpiar(ActionEvent eventoClic) {
        txtNombre.clear();
        txtDescripcion.clear();
        dpFechaInicio.setValue(null);
        dpFechaCierre.setValue(null);
        lblMensajeFormulario.setText("");
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void manejarSalir(ActionEvent eventoClic) {
        Node nodoOrigen = (Node) eventoClic.getSource();
        regresar(nodoOrigen, this.escenaAnterior);
    }
}