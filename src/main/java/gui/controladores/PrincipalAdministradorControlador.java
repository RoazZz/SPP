package gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PrincipalAdministradorControlador extends PrincipalBaseControlador{

    @FXML private Button btnUsuarios;


    @Override
    protected void inicializarBotonesEspecificos() {
        btnUsuarios.setOnAction(e -> abrirVentana("/gui/vista/FXMLGestionUsuarios.fxml"));
    }
}
