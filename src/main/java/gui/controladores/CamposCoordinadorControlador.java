package gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import logica.dto.CoordinadorDTO;

public class CamposCoordinadorControlador {

    @FXML
    private TextField txtNumeroDePersonal;

    public String getNumeroPersonal() {
        return txtNumeroDePersonal.getText();
    }

    public void cargarDatos(CoordinadorDTO coordinadorDTO) {
        txtNumeroDePersonal.setText(coordinadorDTO.getNumeroPersonal());
    }
}
