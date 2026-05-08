package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.ReglaDeNegocioExcepcion;
import interfaces.Regresable;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logica.dto.ProyectoDTO;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FormularioProyectoControlador implements Regresable{

    private static final Logger LOGGER = Logger.getLogger(FormularioProyectoControlador.class.getName());

    @FXML private TextField txtIdOrganizacionVinculada;
    @FXML private TextField txtNumeroDePersonal;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private Label lblMensaje;
    @FXML private Button btnSalir;
    @FXML private Button btnGuardar;

    private Scene escenaAnterior;
    private ProyectoControlador proyectoControlador;

    @FXML
    public void initialize() {
        try {
            proyectoControlador = new ProyectoControlador();
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar Proyecto Controlador", e);
            lblMensaje.setText("Error al conectar con la base de datos.");
        }

        btnSalir.setOnAction(event -> regresar());
        btnGuardar.setOnAction(event -> manejarGuardarProyecto());
    }

    @FXML
    public void manejarGuardarProyecto() {
        if (!validarCamposVacios()) {
            return;
        }
        try {
            ProyectoDTO proyectoDTO = new ProyectoDTO(
                    0,
                    txtIdOrganizacionVinculada.getText(),
                    txtNumeroDePersonal.getText(),
                    txtNombre.getText(),
                    txtDescripcion.getText()
            );
            proyectoControlador.procesarGuardadoProyecto(proyectoDTO, false);
            lblMensaje.setText("Proyecto guardado con éxito.");
            limpiarCampos();
        } catch (ReglaDeNegocioExcepcion e) {
            LOGGER.log(Level.WARNING, "Regla de negocio violada al guardar proyecto", e);
            lblMensaje.setText(e.getMessage());
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error de base de datos al guardar proyecto", e);
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
        if (camposVacios.length() > 0) {
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
    }

    @Override
    public void setEscenaAnterior(Scene escena) {
        this.escenaAnterior = escena;
    }

    private void regresar() {
        if (escenaAnterior != null) {
            Stage escenario = (Stage) txtNombre.getScene().getWindow();
            escenario.setScene(escenaAnterior);
            escenario.show();
        }
    }
}