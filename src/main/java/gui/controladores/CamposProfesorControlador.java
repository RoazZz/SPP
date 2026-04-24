package gui.controladores;

import interfaces.ControladorEspecialidadInterfaz;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import logica.dto.ProfesorDTO;
import logica.enums.TipoTurno;

public class CamposProfesorControlador implements ControladorEspecialidadInterfaz<ProfesorDTO> {
    @FXML private TextField txtNumeroPersonal;
    @FXML private ComboBox<TipoTurno> cbTurno;

    @FXML public void initialize() {
        cbTurno.getItems().setAll(TipoTurno.values());
    }

    @Override
    public void cargarDatos(ProfesorDTO p) {
        txtNumeroPersonal.setText(p.getNumeroDePersonal());
        txtNumeroPersonal.setEditable(false);
        cbTurno.setValue(p.getTurno());
    }

    public String getNumeroPersonal() {
        return txtNumeroPersonal.getText();
    }
    public TipoTurno getTurno() {
        return cbTurno.getValue();
    }
}