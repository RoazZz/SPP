package gui.controladores;

import interfaces.Regresable;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.util.logging.Logger;

public class ReporteSeleccionControlador implements Regresable {
    private Scene escenaAnterior;
    private static final Logger logger = Logger.getLogger(ReporteSeleccionControlador.class.getName());

    @FXML private Button btnRegresar;
    @FXML private Button btnGenerar;
    @FXML private Button btnAñadir;

    public void initialize() {
        btnGenerar.setOnAction(e -> irAGenerar());
        btnAñadir.setOnAction(e -> irAAnadir());
        btnRegresar.setOnAction(e -> regresar());
    }

    @FXML
    private void irAGenerar() {
        NavegacionControlador.abrirVentana("/gui/vista/FXMLReporteGenerar.fxml", btnGenerar);
    }

    @FXML
    private void irAAnadir() {
        NavegacionControlador.abrirVentana("/gui/vista/FXMLReporteAnadir.fxml", btnAñadir);
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