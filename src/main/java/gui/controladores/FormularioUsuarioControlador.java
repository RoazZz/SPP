package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.ValidacionExcepcion;
import interfaces.Regresable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import gui.controladores.ProfesorControlador;
import gui.controladores.UsuarioControlador;
import logica.dto.ProfesorDTO;
import logica.enums.TipoDeUsuario;
import logica.utilidades.PermisosRol;
import logica.utilidades.SesionUsuarioSingleton;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FormularioUsuarioControlador implements Regresable {

    private static final Logger LOGGER = Logger.getLogger(FormularioUsuarioControlador.class.getName());

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidoP;
    @FXML private TextField txtApellidoM;
    @FXML private PasswordField txtContrasenia;
    @FXML private ComboBox<TipoDeUsuario> cbTipoUsuario;
    @FXML private VBox contenedorDinamico;
    @FXML private Label lblError;
    @FXML private Button btnGuardar;
    @FXML private Button btnSalir;

    private ProfesorControlador profesorControlador;
    private Object controladorHijo;
    private ProfesorDTO profesorExistente;
    private boolean modoEdicion = false;
    private Scene escenaAnterior;

    @FXML
    public void initialize() {
        try {
            profesorControlador = new ProfesorControlador();
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar ProfesorControlador", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo inicializar el formulario. Intente más tarde.");
        }

        cargarTiposPermitidos();

        cbTipoUsuario.getSelectionModel().selectedItemProperty().addListener(
                (obs, viejo, nuevo) -> {
                    if (nuevo != null && !modoEdicion) {
                        cambiarFragmento(nuevo);
                    }
                }
        );

        lblError.setVisible(false);
        btnGuardar.setOnAction(e -> manejarGuardar());
        btnSalir.setOnAction(e -> regresar());
    }

    public void inicializarEdicion(ProfesorDTO profesorDTO) {
        this.modoEdicion = true;
        this.profesorExistente = profesorDTO;

        txtNombre.setText(profesorDTO.getNombre());
        txtApellidoP.setText(profesorDTO.getApellidoPaterno());
        txtApellidoM.setText(profesorDTO.getApellidoMaterno());
        txtContrasenia.setText(profesorDTO.getContrasenia());
        cbTipoUsuario.setValue(TipoDeUsuario.PROFESOR);
        cbTipoUsuario.setDisable(true);

        cambiarFragmento(TipoDeUsuario.PROFESOR);
        ((CamposProfesorControlador) controladorHijo).cargarDatos(profesorDTO);
    }

    private void cargarTiposPermitidos() {
        TipoDeUsuario rol = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getTipoDeUsuario();
        PermisosRol permisos = new PermisosRol(rol);

        if (permisos.puedeAgregarCoordinador()) {
            cbTipoUsuario.getItems().add(TipoDeUsuario.COORDINADOR);
        }
        if (permisos.puedeAgregarProfesor()) {
            cbTipoUsuario.getItems().add(TipoDeUsuario.PROFESOR);
        }
        if (permisos.puedeAgregarPracticante()) {
            cbTipoUsuario.getItems().add(TipoDeUsuario.PRACTICANTE);
        }
    }

    private void manejarGuardar() {
        lblError.setVisible(false);

        if (!validarCamposFormulario()) {
            return;
        }

        if (cbTipoUsuario.getValue() == TipoDeUsuario.PROFESOR) {
            guardarProfesor();
        }
    }

    private boolean validarCamposFormulario() {
        if (cbTipoUsuario.getValue() == null) {
            mostrarErrorEnLinea("Debe seleccionar un tipo de usuario.");
            return false;
        }
        if (controladorHijo == null) {
            mostrarErrorEnLinea("Error al cargar el formulario. Intente seleccionar el tipo de usuario nuevamente.");
            return false;
        }

        try {
            UsuarioControlador.validarCamposComunes(
                    txtNombre.getText(),
                    txtApellidoP.getText(),
                    txtApellidoM.getText(),
                    txtContrasenia.getText()
            );
        } catch (ValidacionExcepcion e) {
            LOGGER.log(Level.WARNING, "Validacion fallida en campos comunes", e);
            mostrarErrorEnLinea(e.getMessage());
            return false;
        }

        return true;
    }

    private void guardarProfesor() {
        CamposProfesorControlador hijo = (CamposProfesorControlador) controladorHijo;

        try {
            ProfesorDTO dto = profesorControlador.construirProfesorDTO(
                    modoEdicion ? profesorExistente.getIdUsuario() : 0,
                    txtNombre.getText().trim(),
                    txtApellidoP.getText().trim(),
                    txtApellidoM.getText().trim(),
                    txtContrasenia.getText(),
                    hijo.getNumeroPersonal(),
                    hijo.getTurno()
            );

            profesorControlador.procesarGuardadoProfesor(dto, modoEdicion);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    modoEdicion ? "Profesor actualizado correctamente." : "Profesor registrado correctamente.");
            cerrarVentana();

        } catch (ValidacionExcepcion e) {
            LOGGER.log(Level.WARNING, "Validacion fallida al guardar profesor", e);
            mostrarErrorEnLinea(e.getMessage());
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error de BD al guardar profesor", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar",
                    "No se pudo guardar el profesor. Intente más tarde.");
        }
    }

    private void cambiarFragmento(TipoDeUsuario tipo) {
        try {
            String nombreTipo = tipo.name().charAt(0) + tipo.name().substring(1).toLowerCase();
            String ruta = "/gui/vista/FXMLFragmento" + nombreTipo + ".fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Node nodo = loader.load();
            contenedorDinamico.getChildren().setAll(nodo);
            controladorHijo = loader.getController();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar fragmento para tipo: " + tipo.name(), e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error de carga",
                    "No se pudo cargar el formulario para el tipo de usuario seleccionado.");
        }
    }

    private void mostrarErrorEnLinea(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void cerrarVentana() {
        ((Stage) txtNombre.getScene().getWindow()).close();
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