package gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PrincipalCoordinadorControlador extends PrincipalBaseControlador{
    @FXML Button btnProyectos;
    @FXML Button btnReportes;
    @FXML Button btnPracticantes;


    @Override
    protected void inicializarBotonesEspecificos() {
        btnProyectos.setOnAction(e -> abrirVentana("/gui/vista/FXMLProyectos.fxml"));
        btnReportes.setOnAction(e -> abrirVentana("/gui/vista/FXMLReportes.fxml"));
        btnPracticantes.setOnAction(e -> abrirVentana("/gui/vista/FXMLListaUsuarios.fxml"));
    }
}
