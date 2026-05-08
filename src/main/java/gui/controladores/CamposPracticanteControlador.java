package gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import logica.enums.GeneroDelPracticante;

public class CamposPracticanteControlador {
    @FXML private TextField txtMatricula;
    @FXML private TextField txtIdSeccion;
    @FXML private TextField txtSemestre;
    @FXML private ComboBox<GeneroDelPracticante> cbGenero;
    @FXML private TextField txtEdad;
    @FXML private CheckBox chbLenguaIndigena;

    @FXML public void initialize() {
        cbGenero.getItems().setAll(GeneroDelPracticante.values());
    }

    public String getMatricula() {
        return txtMatricula.getText();
    }

    public int getIdSeccion() {
        return Integer.parseInt(txtIdSeccion.getText());
    }

    public String getSemestre() {
        return txtSemestre.getText();
    }

    public GeneroDelPracticante getGenero() {
        return cbGenero.getValue();
    }

    public int getEdad() {
        return Integer.parseInt(txtEdad.getText());
    }

    public boolean isLenguaIndigena() {
        return chbLenguaIndigena.isSelected();
    }

}
