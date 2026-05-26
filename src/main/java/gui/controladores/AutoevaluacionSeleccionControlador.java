package gui.controladores;

import logica.interfaces.Regresable;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class AutoevaluacionSeleccionControlador implements Regresable {

    @FXML private Button btnGenerar;
    @FXML private Button btnSubir;
    @FXML private Button btnRegresar;

    private Scene escenaAnterior;

    private static final Logger LOGGER = Logger.getLogger(AutoevaluacionSeleccionControlador.class.getName());

    @FXML
    public void initialize() {
        btnGenerar.setOnAction(e -> NavegacionControlador.abrirVentana(
                "/gui/vista/FXMLAutoevaluacionGenerar.fxml", btnGenerar)
        );
        btnSubir.setOnAction(e -> NavegacionControlador.abrirVentana(
                "/gui/vista/FXMLAutoevaluacionSubir.fxml", btnSubir)
        );
        btnRegresar.setOnAction(e -> regresar());
    }

    @Override
    public void setEscenaAnterior(Scene escena) {
        this.escenaAnterior = escena;
    }

    private void regresar() {
        if (escenaAnterior != null) {
            Stage escenario = (Stage) btnRegresar.getScene().getWindow();
            escenario.setScene(escenaAnterior);
            escenario.show();
        }
    }
}