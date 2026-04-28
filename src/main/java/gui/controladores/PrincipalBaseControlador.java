package gui.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public abstract class PrincipalBaseControlador implements Initializable {
    private static final Logger logger = Logger.getLogger(PrincipalBaseControlador.class.getName());
   /* @FXML private Label lblNombre;
    @FXML private Button btnProyectos;
    @FXML private Button btnReportes;
    @FXML private Button btnFormatoPresentacion;;
    @FXML private Button btnUsuarios;
    @FXML private Button btnHorario;
    @FXML private Button btnActividades;
    @FXML private Button btnAutoevaluacion;
    @FXML private Button btnPlanDeActividades;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnBuzon;
    @FXML private Button btnCerrarSesion;
    @FXML private Button btnBitacoraPSP;*/

    @FXML private Label lblNombre;
    @FXML private Button btnConfiguracion;
    @FXML private Button btnBuzon;
    @FXML private Button btnCerrarSesion;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       // SesionUsuario.getInstance().setLblNombre(lblNombre); //Modifica esta parte Jared si es q cambiaste el nombre del singleton
        btnConfiguracion.setOnAction(e -> abrirVentana("/gui/vista/FXMLConfiguracion.fxml"));
        btnBuzon.setOnAction(e -> abrirVentana("/gui/vista/FXMLBuzon.fxml"));
        btnCerrarSesion.setOnAction(e -> cerrarSesion());
        inicializarBotonesEspecificos();
    }

    protected abstract void inicializarBotonesEspecificos();

    protected void abrirVentana(String rutaFXML){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent vista = loader.load();
            Stage escenario = (Stage) btnCerrarSesion.getScene().getWindow();
            escenario.setScene(new Scene(vista));
            escenario.show();
        }catch(IOException e){
            logger.severe("Error al abrir la ventana: " + e.getMessage());
        }
    }

    private void cerrarSesion() {
        //SesionUsuario.getInstancia().cerrar(); //Igual aqui, jajaja
        abrirVentana("/vistas/Login.fxml");
    }


}
