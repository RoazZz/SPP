package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.ReglaDeNegocioExcepcion;
import interfaces.Regresable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logica.dto.CoordinadorDTO;
import logica.dto.PracticanteDTO;
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
    private PracticanteControlador practicanteControlador;
    private CoordinadorControlador coordinadorControlador;
    private Object controladorHijo;
    private ProfesorDTO profesorExistente;
    private boolean modoEdicion = false;
    private Scene escenaAnterior;

    @FXML
    public void initialize() {
        try {
            profesorControlador = new ProfesorControlador();
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar Profesor Controlador", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo inicializar el formulario de profesor. Intente más tarde.");
        }

        try {
            practicanteControlador = new PracticanteControlador();
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar Practicante Controlador", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo inicializar el formulario de practicante. Intente más tarde.");
        }

        try {
            coordinadorControlador = new CoordinadorControlador();
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar al Coordinador controlador", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo inicializar el formulario de coordinador. Intente más tarde.");
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

        TipoDeUsuario tipo = cbTipoUsuario.getValue();
        if (tipo == TipoDeUsuario.PROFESOR) {
            guardarProfesor();
        } else if (tipo == TipoDeUsuario.PRACTICANTE) {
            guardarPracticante();
        } else if (tipo == TipoDeUsuario.COORDINADOR) {
            guardarCoordinador();
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
        } catch (ReglaDeNegocioExcepcion e) {
            LOGGER.log(Level.WARNING, "Validacion fallida en campos comunes", e);
            mostrarErrorEnLinea(e.getMessage());
            return false;
        }
        return true;
    }

    private void guardarProfesor() {
        CamposProfesorControlador hijo = (CamposProfesorControlador) controladorHijo;
        try {
            ProfesorDTO ProfesorDTO = profesorControlador.construirProfesorDTO(
                    modoEdicion ? profesorExistente.getIdUsuario() : 0,
                    txtNombre.getText().trim(),
                    txtApellidoP.getText().trim(),
                    txtApellidoM.getText().trim(),
                    txtContrasenia.getText(),
                    hijo.getNumeroPersonal(),
                    hijo.getTurno()
            );
            profesorControlador.procesarGuardadoProfesor(ProfesorDTO, modoEdicion);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    modoEdicion ? "Profesor actualizado correctamente." : "Profesor registrado correctamente.");
            cerrarVentana();
        } catch (ReglaDeNegocioExcepcion e) {
            LOGGER.log(Level.WARNING, "Validacion fallida al guardar profesor", e);
            mostrarErrorEnLinea(e.getMessage());
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error de BD al guardar profesor", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar",
                    "No se pudo guardar el profesor. Intente más tarde.");
        }
    }

    private void guardarPracticante() {
        CamposPracticanteControlador hijo = (CamposPracticanteControlador) controladorHijo;
        try {
            PracticanteDTO PracticanteDTO = practicanteControlador.construirPracticanteDTO(
                    0,
                    txtNombre.getText().trim(),
                    txtApellidoP.getText().trim(),
                    txtApellidoM.getText().trim(),
                    txtContrasenia.getText(),
                    hijo.getMatricula(),
                    hijo.getIdSeccion(),
                    hijo.getSemestre(),
                    hijo.getGenero(),
                    hijo.getEdad(),
                    hijo.isLenguaIndigena()
            );
            practicanteControlador.procesarGuardadoPracticante(PracticanteDTO, false);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Practicante registrado correctamente.");
            cerrarVentana();
        } catch (ReglaDeNegocioExcepcion e) {
            LOGGER.log(Level.WARNING, "Validacion fallida al guardar practicante", e);
            mostrarErrorEnLinea(e.getMessage());
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error de BD al guardar practicante", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar",
                    "No se pudo guardar el practicante. Intente más tarde.");
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Error de formato en campos numéricos del practicante", e);
            mostrarErrorEnLinea("La sección y la edad deben ser números válidos.");
        }
    }

    private void guardarCoordinador() {
        CamposCoordinadorControlador hijo = (CamposCoordinadorControlador) controladorHijo;
        try {
            CoordinadorDTO coordinadorDTO = coordinadorControlador.construirCoordinadorDTO(
                    0,
                    txtNombre.getText().trim(),
                    txtApellidoP.getText().trim(),
                    txtApellidoM.getText().trim(),
                    txtContrasenia.getText(),
                    hijo.getNumeroPersonal()
            );
            coordinadorControlador.procesarGuardadoCoordinador(coordinadorDTO, false);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Coordinador registrado correctamente.");
            cerrarVentana();
        } catch (ReglaDeNegocioExcepcion e) {
            LOGGER.log(Level.WARNING, "Validacion fallida al guardar coordinador", e);
            mostrarErrorEnLinea(e.getMessage());
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error de BD al guardar coordinador", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar",
                    "No se pudo guardar el coordinador. Intente más tarde.");
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