package gui.controladores;

import logica.interfaces.ControladorEspecialidadInterfaz;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import logica.dto.ProfesorDTO;
import logica.enums.TipoTurno;

public class CamposProfesorControlador implements ControladorEspecialidadInterfaz<ProfesorDTO> {
    @FXML private TextField txtNumeroPersonal;
    @FXML private ComboBox<TipoTurno> cbTurno;
    @FXML private TextField txtSeccion;

    @FXML public void initialize() {
        cbTurno.getItems().setAll(TipoTurno.values());
    }

    @Override
    public void cargarDatos(ProfesorDTO profesorDTO) {
        txtNumeroPersonal.setText(profesorDTO.getNumeroDePersonal());
        txtNumeroPersonal.setEditable(false);
        txtSeccion.setText(String.valueOf(profesorDTO.getIdSeccion()));
        cbTurno.setValue(profesorDTO.getTurno());
    }

    public String getNumeroPersonal() {
        return txtNumeroPersonal.getText();
    }
    public TipoTurno getTurno() {
        return cbTurno.getValue();
    }

    public int getSeccion() {
        String textoSeccion = txtSeccion.getText().trim();

        if (textoSeccion.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(textoSeccion);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}