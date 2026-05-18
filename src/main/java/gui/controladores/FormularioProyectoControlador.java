package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.ReglaDeNegocioExcepcion;
import interfaces.Regresable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logica.dto.ProyectoDTO;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormularioProyectoControlador implements Initializable, Regresable {

    private static final Logger logger = Logger.getLogger(FormularioProyectoControlador.class.getName());

    @FXML private Label lblTitulo;
    @FXML private TextField txtIdOrganizacionVinculada;
    @FXML private TextField txtNumeroDePersonal;
    @FXML private TextField txtNombre;
    @FXML private TextArea  txtDescripcion;
    @FXML private Label     lblMensaje;
    @FXML private Button    btnSalir;
    @FXML private Button    btnGuardar;

    private Scene escenaAnterior;
    private ProyectoControlador proyectoControlador;
    private ProyectoDTO proyectoEnEdicion = null;
    private ProyectosControlador controladorPadre;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            proyectoControlador = new ProyectoControlador();
        } catch (DAOExcepcion e) {
            logger.log(Level.SEVERE, "Error al inicializar ProyectoControlador", e);
            lblMensaje.setText("Error al conectar con la base de datos.");
        }
    }

    public void cargarProyecto(ProyectoDTO proyecto) {
        this.proyectoEnEdicion = proyecto;
        txtIdOrganizacionVinculada.setText(proyecto.getIdOrganizacion());
        txtIdOrganizacionVinculada.setEditable(false);
        txtNumeroDePersonal.setText(proyecto.getNumeroDePersonal());
        txtNumeroDePersonal.setEditable(false);
        txtNombre.setText(proyecto.getNombre());
        txtDescripcion.setText(proyecto.getDescripcion());
    }

    @FXML
    private void manejarGuardarProyecto() {
        if (!validarCamposVacios()) {
            return;
        }
        try {
            boolean modoEdicion = proyectoEnEdicion != null;
            int idProyecto = modoEdicion ? proyectoEnEdicion.getIdProyecto() : 0;

            ProyectoDTO proyectoDTO = new ProyectoDTO(
                    idProyecto,
                    txtIdOrganizacionVinculada.getText().trim(),
                    txtNumeroDePersonal.getText().trim(),
                    txtNombre.getText().trim(),
                    txtDescripcion.getText().trim()
            );

            proyectoControlador.procesarGuardadoProyecto(proyectoDTO, modoEdicion);
            lblMensaje.setStyle("-fx-text-fill: green;");
            lblMensaje.setText(modoEdicion
                    ? "Proyecto actualizado con éxito."
                    : "Proyecto guardado con éxito.");
            if (!modoEdicion) {
                limpiarCampos();
            }
            logger.log(Level.INFO, "Proyecto guardado correctamente");
            if (controladorPadre != null) {
                controladorPadre.cargarProyectos();
            }
        } catch (ReglaDeNegocioExcepcion e) {
            logger.log(Level.WARNING, "Regla de negocio violada al guardar proyecto", e);
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText(e.getMessage());
        } catch (DAOExcepcion e) {
            logger.log(Level.SEVERE, "Error de base de datos al guardar proyecto", e);
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText("Error al guardar el proyecto. Intente de nuevo.");
        }
    }

    private boolean validarCamposVacios() {
        StringBuilder camposVacios = new StringBuilder();
        if (txtIdOrganizacionVinculada.getText().trim().isEmpty()) {
            camposVacios.append("Id Organización Vinculada es obligatorio.\n");
        }
        if (txtNumeroDePersonal.getText().trim().isEmpty()) {
            camposVacios.append("Número de Personal es obligatorio.\n");
        }
        if (txtNombre.getText().trim().isEmpty()) {
            camposVacios.append("Nombre es obligatorio.\n");
        }
        if (txtDescripcion.getText().trim().isEmpty()) {
            camposVacios.append("Descripción es obligatoria.\n");
        }
        if (!camposVacios.isEmpty()) {
            lblMensaje.setStyle("-fx-text-fill: red;");
            lblMensaje.setText(camposVacios.toString());
            return false;
        }
        lblMensaje.setText("");
        return true;
    }

    private void limpiarCampos() {
        txtIdOrganizacionVinculada.clear();
        txtNumeroDePersonal.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        proyectoEnEdicion = null;
    }

    public void setControladorPadre(ProyectosControlador controlador) {
        this.controladorPadre = controlador;
    }

    public void configurarTitulo(String titulo) {
        lblTitulo.setText(titulo);
    }

    @Override
    public void setEscenaAnterior(Scene escena) {
        this.escenaAnterior = escena;
    }

    @FXML
    private void salir() {
        if (escenaAnterior != null) {
            if (controladorPadre != null) {
                controladorPadre.cargarProyectos();
            }
            Stage escenario = (Stage) txtNombre.getScene().getWindow();
            escenario.setScene(escenaAnterior);
            escenario.show();
        }
    }
}