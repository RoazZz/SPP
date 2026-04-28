package gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PrincipalProfesorControlador extends PrincipalBaseControlador{
    @FXML private Button btnActividades;
    @FXML private Button btnFormatoPresentacion;
    @FXML private Button btnReportes;

    @Override
    protected void inicializarBotonesEspecificos() {
        btnActividades.setOnAction(e -> abrirVentana("/gui/vista/FXMLActividades.fxml"));
        btnFormatoPresentacion.setOnAction(e -> abrirVentana("/gui/vista/FXMLFormatoPresentacion.fxml"));
        btnReportes.setOnAction(e -> abrirVentana("/gui/vista/FXMLReportes.fxml"));
    }
}
