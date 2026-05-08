package gui.controladores;

import interfaces.Regresable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import logica.dto.UsuarioDTO;
import logica.utilidades.SesionUsuarioSingleton;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public abstract class PrincipalBaseControlador implements Initializable {
    private static final Logger logger = Logger.getLogger(PrincipalBaseControlador.class.getName());

    @FXML private Label lblNombre;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnBuzon;
    @FXML private Button btnCerrarSesion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UsuarioDTO usuario = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual();
        if (usuario != null) {
            lblNombre.setText(usuario.getNombre());
        }
        btnConfiguracion.setOnAction(e -> abrirVentana("/gui/vista/FXMLConfiguracionPerfil.fxml"));
        btnBuzon.setOnAction(e -> abrirVentana("/gui/vista/FXMLBuzon.fxml"));
        btnCerrarSesion.setOnAction(e -> cerrarSesion());
        inicializarBotonesEspecificos();
    }

    protected abstract void inicializarBotonesEspecificos();

    protected void abrirVentana(String rutaFXML) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent vista = cargador.load();
            Stage escenario = (Stage) btnCerrarSesion.getScene().getWindow();

            Object controlador = cargador.getController();
            if(controlador instanceof Regresable regresable){
                regresable.setEscenaAnterior(escenario.getScene());
            }

            escenario.setScene(new Scene(vista));
            escenario.show();
        } catch (IOException e) {
            logger.severe("Error al abrir la ventana: " + e.getMessage());
        }
    }

    private void cerrarSesion() {
        SesionUsuarioSingleton.obtenerInstancia().cerrarSesion();
        abrirVentana("/gui/vista/FXMLInicioSesion.fxml");
    }
}
