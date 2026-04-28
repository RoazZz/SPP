package gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PrincipalPracticanteControlador extends PrincipalBaseControlador{
    @FXML private Button btnProyectos;
    @FXML private Button btnHorario;
    @FXML private Button btnReportes;
    @FXML private Button btnAutoevaluacion;
    @FXML private Button btnActividades;
    @FXML private Button btnBitacoraPSP;
    @FXML private Button btnPlanDeActividades;


    @Override
    protected void inicializarBotonesEspecificos() {
        btnProyectos.setOnAction(e -> abrirVentana("/gui/vista/FXMLProyectos.fxml"));
        btnHorario.setOnAction(e -> abrirVentana("/gui/vista/FXMLHorario.fxml"));
        btnReportes.setOnAction(e -> abrirVentana("/gui/vista/FXMLReporteSeleccion.fxml"));
        btnAutoevaluacion.setOnAction(e -> abrirVentana("/gui/vista/FXMLAutoevaluacion.fxml"));
        btnActividades.setOnAction(e -> abrirVentana("/gui/vista/FXMLActividades.fxml"));
        btnBitacoraPSP.setOnAction(e -> abrirVentana("/gui/vista/FXMLBitacoraPSP.fxml"));
        btnPlanDeActividades.setOnAction(e -> abrirVentana("/gui/vista/FXMLPlanDeActividades.fxml"));
    }
}
