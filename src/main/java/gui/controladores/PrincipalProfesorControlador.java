package gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import static gui.controladores.NavegacionControlador.abrirVentana;


public class PrincipalProfesorControlador extends PrincipalBaseControlador {
    @FXML private Button btnActividades;
    @FXML private Button btnFormatoPresentacion;
    @FXML private Button btnReportes;

    @Override
    protected void inicializarBotonesEspecificos() {
        btnActividades.setOnAction(e -> abrirVentana("/gui/vista/FXMLActividades.fxml", btnActividades));
        btnFormatoPresentacion.setOnAction(e -> abrirVentana("/gui/vista/FXMLFormatoPresentacion.fxml", btnFormatoPresentacion));
        btnReportes.setOnAction(e -> abrirVentana("/gui/vista/FXMLListaReportes.fxml", btnReportes));
    }
}
