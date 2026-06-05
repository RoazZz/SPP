package gui.controladores;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import static gui.controladores.NavegacionControlador.abrirVentana;
public class PrincipalProfesorControlador extends PrincipalBaseControlador {
    @FXML private Button btnActividades;
    @FXML private Button btnFormatoPresentacion;
    @FXML private Button btnReportes;
    @FXML private Button btnCalificacionFinal;
    @Override
    protected void inicializarBotonesEspecificos() {
        btnActividades.setOnAction(evento -> abrirVentana("/gui/vista/FXMLActividades.fxml", btnActividades));
        btnFormatoPresentacion.setOnAction(evento -> abrirVentana("/gui/vista/FXMLFormatoPresentacion.fxml", btnFormatoPresentacion));
        btnReportes.setOnAction(evento -> abrirVentana("/gui/vista/FXMLListaReportes.fxml", btnReportes));
        btnCalificacionFinal.setOnAction(evento -> abrirVentana("/gui/vista/FXMLCalificacionFinal.fxml", btnCalificacionFinal));
    }
}