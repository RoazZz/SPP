package gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import logica.dto.CoordinadorDTO;

public class CamposCoordinadorControlador {

    @FXML
    private TextField txtNumeroPersonal;

    public void cargarDatos(CoordinadorDTO coordinadorDTO) {
        txtNumeroPersonal.setText(coordinadorDTO.getNumeroPersonal());
    }

    public String getNumeroPersonal() {
        return txtNumeroPersonal.getText();
    }
}
