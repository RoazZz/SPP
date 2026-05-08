package gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import logica.utilidades.SesionUsuarioSingleton;

public class PrincipalAdministradorControlador extends PrincipalBaseControlador{

    @FXML private Button btnUsuarios;
    @FXML private Button btnCerrarSesion;


    @Override
    protected void inicializarBotonesEspecificos() {
        btnUsuarios.setOnAction(e -> abrirVentana("/gui/vista/FXMLListaUsuarios.fxml"));
    }

}
