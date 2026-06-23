package gui.controladores;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import logica.dto.UsuarioDTO;
import logica.utilidades.RegistradorBitacora;
import logica.utilidades.SesionUsuarioSingleton;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

import static gui.controladores.NavegacionControlador.abrirVentana;

public abstract class PrincipalBaseControlador implements Initializable {

    @FXML private Label lblNombre;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnBuzon;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UsuarioDTO usuario = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual();
        if (usuario != null) {
            lblNombre.setText(usuario.getNombre());
        }
        btnConfiguracion.setOnAction(evento -> abrirVentana("/gui/vista/FXMLConfiguracionPerfil.fxml", btnConfiguracion));
        btnBuzon.setOnAction(evento -> abrirVentana("/gui/vista/FXMLBuzon.fxml", btnBuzon));
        inicializarBotonesEspecificos();
    }

    protected abstract void inicializarBotonesEspecificos();

    @FXML
    private void manejarCerrarSesion(ActionEvent evento) {
        RegistradorBitacora.registrar("CIERRE_SESION", "El usuario cerro sesion");
        SesionUsuarioSingleton.obtenerInstancia().cerrarSesion();
        NavegacionControlador.abrirVentana("/gui/vista/FXMLInicioSesion.fxml", (Node) evento.getSource());
    }

}
