package gui.controladores;

import excepciones.DAOExcepcion;
import interfaces.Regresable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logica.dao.PlanDeActividadesDAO;
import logica.dto.PlanDeActividadesDTO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlanDeActividadesControlador implements Initializable, Regresable {

    private static final Logger LOGGER = Logger.getLogger(PlanDeActividadesControlador.class.getName());

    @FXML private Label lblNombreArchivo;
    @FXML private Label lblValidacionArchivo;
    @FXML private HBox hboxValidacionArchivo;
    @FXML private Button btnExaminarArchivos;
    @FXML private Button btnVistaPrevia;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Scene escenaAnterior;
    private File archivoSeleccionado;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarEventos();
    }

    private void configurarEventos() {
        btnExaminarArchivos.setOnAction(event -> manejarSeleccionArchivo());
        btnGuardar.setOnAction(event -> manejarGuardar());
        btnCancelar.setOnAction(event -> regresar());
    }

    private void manejarSeleccionArchivo() {
        FileChooser selectorArchivos = new FileChooser();
        selectorArchivos.setTitle("Seleccionar Plan de Actividades");
        selectorArchivos.getExtensionFilters().add( new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf"));

        Stage escenario = (Stage) btnExaminarArchivos.getScene().getWindow();
        File archivo = selectorArchivos.showOpenDialog(escenario);
        if (archivo != null) {
            archivoSeleccionado = archivo;
            lblNombreArchivo.setText(archivo.getName());
            actualizarEstadoUI(true);
        }
    }

    private void actualizarEstadoUI(boolean archivoCargado) {
        //Nota solo para la revision del bnoquejo, jajajaja: Aqui voy a habilitar el boton de guardado
        // el de vista previa y el .setManaged en el boton de vista previa,
    }

    private void manejarGuardar() {
        try {
            // Otra nota para la revision, xd: Aqui pues el metodo de procesar guardado comun donde se convierte a bytes para el DTO y el DAO
            // Se construye el dto, se crea el DAO
            regresar();
        }catch (IOException ex){
            LOGGER.log(Level.SEVERE, "Error al leer el archivo físico: " + archivoSeleccionado.getName(), ex);
        }catch (DAOExcepcion ex){
            LOGGER.log(Level.SEVERE, "Error al guardar el plan de actividades en la base de datos", ex);
        }
    }

    @Override
    public void setEscenaAnterior(Scene escena) {
        this.escenaAnterior = escena;
    }

    private void regresar() {
        if (escenaAnterior != null) {
            Stage escenario = (Stage) btnCancelar.getScene().getWindow();
            escenario.setScene(escenaAnterior);
        }
    }

}