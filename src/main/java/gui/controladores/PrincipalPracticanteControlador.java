package gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import static gui.controladores.NavegacionControlador.abrirVentana;

public class PrincipalPracticanteControlador extends PrincipalBaseControlador {
    @FXML private Button btnProyectos;
    @FXML private Button btnHorario;
    @FXML private Button btnReportes;
    @FXML private Button btnAutoevaluacion;
    @FXML private Button btnActividades;
    @FXML private Button btnBitacoraPSP;
    @FXML private Button btnPlanDeActividades;
    @FXML private Button btnCerrarSesion;


    @Override
    protected void inicializarBotonesEspecificos() {
        btnProyectos.setOnAction(evento -> abrirVentana("/gui/vista/FXMLSolicitarProyecto.fxml", btnProyectos));
        btnHorario.setOnAction(evento -> abrirVentana("/gui/vista/FXMLHorario.fxml", btnHorario));
        btnReportes.setOnAction(evento -> abrirVentana("/gui/vista/FXMLReporteSeleccion.fxml", btnReportes));
        btnAutoevaluacion.setOnAction(evento -> abrirVentana("/gui/vista/FXMLAutoevaluacionSeleccion.fxml", btnAutoevaluacion));
        btnActividades.setOnAction(evento -> abrirVentana("/gui/vista/FXMLActividades.fxml", btnActividades));
        btnBitacoraPSP.setOnAction(evento -> abrirVentana("/gui/vista/FXMLBitacoraPSP.fxml", btnBitacoraPSP));
        btnPlanDeActividades.setOnAction(evento -> abrirVentana("/gui/vista/FXMLPlanDeActividades.fxml", btnPlanDeActividades));
    }
}
