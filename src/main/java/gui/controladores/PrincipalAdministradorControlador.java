package gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import static gui.controladores.NavegacionControlador.abrirVentana;

public class PrincipalAdministradorControlador extends PrincipalBaseControlador{

    @FXML private Button btnUsuarios;


    @Override
    protected void inicializarBotonesEspecificos() {
        btnUsuarios.setOnAction(evento -> abrirVentana("/gui/vista/FXMLListaUsuarios.fxml", btnUsuarios));
    }

}
