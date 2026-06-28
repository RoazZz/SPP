package gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import static gui.controladores.NavegacionControlador.abrirVentana;

public class PrincipalCoordinadorControlador extends PrincipalBaseControlador {
    @FXML private Button btnProyectos;
    @FXML private Button btnReportes;
    @FXML private Button btnPracticantes;
    @FXML private Button btnReporteIndicadores;
    @FXML private Button btnBitacora;


    @Override
    protected void inicializarBotonesEspecificos() {
        btnProyectos.setOnAction(evento -> abrirVentana("/gui/vista/FXMLProyectos.fxml", btnProyectos));
        btnReportes.setOnAction(evento -> abrirVentana("/gui/vista/FXMLReporte.fxml", btnReportes));
        btnPracticantes.setOnAction(evento -> abrirVentana("/gui/vista/FXMLListaUsuarios.fxml", btnPracticantes));
        btnReporteIndicadores.setOnAction(evento -> abrirVentana("/gui/vista/FXMLReporteIndicadores.fxml", btnReporteIndicadores));
        btnBitacora.setOnAction(evento -> NavegacionControlador.abrirVentana("/gui/vista/FXMLBitacoraSistema.fxml", btnBitacora));

    }
}
