package gui.controladores;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import java.net.URL;
import java.util.ResourceBundle;

import static gui.controladores.NavegacionControlador.abrirVentana;

public class BienvenidaControlador implements Initializable {

    @FXML private Button btnComenzar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnComenzar.setOnAction(evento -> abrirVentana("/gui/vista/FXMLRegistroAdministrador.fxml", btnComenzar));
    }
}