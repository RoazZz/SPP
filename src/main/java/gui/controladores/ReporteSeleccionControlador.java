package gui.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReporteSeleccionControlador {
    private static final Logger logger = Logger.getLogger(ReporteSeleccionControlador.class.getName());

    @FXML private Button btnRegresar;

    @FXML
    private void irAGenerar() {
        cambiarEscena("/gui/vista/FXMLReporteGenerar.fxml", "Generar Nuevo Reporte");
    }

    @FXML
    private void irAAnadir() {
        cambiarEscena("/gui/vista/FXMLReporteAnadir.fxml", "Añadir Reporte Firmado");
    }

    @FXML
    private void regresar() {
        Stage stage = (Stage) btnRegresar.getScene().getWindow();
        stage.close();
    }

    private void cambiarEscena(String rutaFXML, String titulo) {
        try {
            Stage stageActual = (Stage) btnRegresar.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(rutaFXML));
            stageActual.setScene(new Scene(root));
            stageActual.setTitle(titulo);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al cargar la vista: " + rutaFXML, e);
        }
    }
}