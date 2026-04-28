/*
 * FormularioUsuarioControlador.java
 * Versión 1.0
 * Fecha: 28/04/26
 * Copyright (c) 2026
 */
package gui.controladores;

import excepciones.DAOExcepcion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logica.dao.ProfesorDAO;
import logica.dao.UsuarioDAO;
import logica.dto.ProfesorDTO;
import logica.enums.*;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FormularioUsuarioControlador {

    private static final Logger LOGGER = Logger.getLogger(FormularioUsuarioControlador.class.getName());
    private static final int LONGITUD_MINIMA_CONTRASENIA = 8;

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidoP;
    @FXML private TextField txtApellidoM;
    @FXML private PasswordField txtContrasenia;
    @FXML private ComboBox<TipoDeUsuario> cbTipoUsuario;
    @FXML private VBox contenedorDinamico;
    @FXML private Label lblError;

    private Object controladorHijo;
    private ProfesorDTO profesorExistente;
    private boolean modoEdicion = false;

    @FXML
    public void initialize() {
        cbTipoUsuario.getItems().setAll(TipoDeUsuario.values());
        cbTipoUsuario.getSelectionModel().selectedItemProperty().addListener(
                (obs, viejo, nuevo) -> {
                    if (nuevo != null && !modoEdicion) {
                        cambiarFragmento(nuevo);
                    }
                }
        );
        lblError.setVisible(false);
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

    @FXML
    private void manejarGuardar() {
        lblError.setVisible(false);

        String mensajeValidacion = validarCamposComunes();
        if (mensajeValidacion != null) {
            mostrarErrorEnLinea(mensajeValidacion);
            return;
        }

        try {
            if (cbTipoUsuario.getValue() == TipoDeUsuario.PROFESOR) {
                guardarProfesor();
            }
            // else if para PRACTICANTE y COORDINADOR aquí
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado al procesar el registro", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error inesperado",
                    "Ocurrió un error inesperado. Intente más tarde.");
        }
    }

    private void guardarProfesor() {
        CamposProfesorControlador hijo = (CamposProfesorControlador) controladorHijo;

        String mensajeEspecifico = validarCamposProfesor(hijo);
        if (mensajeEspecifico != null) {
            mostrarErrorEnLinea(mensajeEspecifico);
            return;
        }

        try {
            int idExcluir;
            if (modoEdicion) {
                idExcluir = profesorExistente.getIdUsuario();
            } else {
                idExcluir = 0;
            }

            boolean existe = new ProfesorDAO().existeProfesorConNumeroPersonal(
                    hijo.getNumeroPersonal(),
                    idExcluir
            );

            if (existe) {
                mostrarErrorEnLinea("Ya existe un profesor con ese número de personal.");
                return;
            }

            ProfesorDTO profesorDTO = new ProfesorDTO(
                    modoEdicion ? profesorExistente.getIdUsuario() : 0,
                    txtNombre.getText().trim(),
                    txtApellidoP.getText().trim(),
                    txtApellidoM.getText().trim(),
                    txtContrasenia.getText(),
                    TipoEstado.ACTIVO,
                    TipoDeUsuario.PROFESOR,
                    hijo.getNumeroPersonal(),
                    hijo.getTurno()
            );

            ProfesorDAO profesorDAO = new ProfesorDAO();
            if (modoEdicion) {
                new UsuarioDAO().actualizarUsuario(profesorDTO);
                profesorDAO.actualizarProfesor(profesorDTO);
            } else {
                profesorDAO.agregarProfesor(profesorDTO);
            }

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    modoEdicion ? "Profesor actualizado correctamente." : "Profesor registrado correctamente.");
            cerrarVentana();

        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al guardar profesor", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error al guardar",
                    "No se pudo guardar el profesor. Intente más tarde.");
        }
    }

    private String validarCamposComunes() {
        StringBuilder errores = new StringBuilder();

        if (txtNombre.getText().trim().isEmpty()) {
            errores.append("El campo nombre no puede estar vacío.\n");
        }
        if (txtApellidoP.getText().trim().isEmpty()) {
            errores.append("El campo apellido paterno no puede estar vacío.\n");
        }
        if (txtApellidoM.getText().trim().isEmpty()) {
            errores.append("El campo apellido materno no puede estar vacío.\n");
        }
        if (txtContrasenia.getText().trim().isEmpty()) {
            errores.append("El campo contraseña no puede estar vacío.\n");
        }
        if (txtContrasenia.getText().length() < LONGITUD_MINIMA_CONTRASENIA) {
            errores.append("La contraseña debe tener al menos " + LONGITUD_MINIMA_CONTRASENIA + " caracteres.\n");
        }
        if (cbTipoUsuario.getValue() == null) {
            errores.append("Debe seleccionar un tipo de usuario.\n");
        }
        if (controladorHijo == null) {
            errores.append("Error al cargar el formulario. Intente seleccionar el tipo de usuario nuevamente.\n");
        }

        boolean hayErrores = errores.length() > 0;
        return hayErrores ? errores.toString() : null;
    }

    private String validarCamposProfesor(CamposProfesorControlador hijo) {
        StringBuilder errores = new StringBuilder();

        if (hijo.getNumeroPersonal() == null || hijo.getNumeroPersonal().trim().isEmpty()) {
            errores.append("El número de personal no puede estar vacío.\n");
        }
        if (hijo.getTurno() == null) {
            errores.append("Debe seleccionar un turno.\n");
        }

        boolean hayErrores = errores.length() > 0;
        return hayErrores ? errores.toString() : null;
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
            LOGGER.log(Level.SEVERE, "Error al cargar fragmento de tipo: " + tipo.name(), e);
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

    @FXML
    private void manejarCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        ((Stage) txtNombre.getScene().getWindow()).close();
    }
}