package gui.controladores;

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
import java.util.logging.Logger;
import java.util.logging.Level;

public class FormularioUsuarioControlador {
    private static final Logger logger = Logger.getLogger(FormularioUsuarioControlador.class.getName());

    @FXML private TextField txtNombre, txtApellidoP, txtApellidoM;
    @FXML private PasswordField txtContrasenia;
    @FXML private ComboBox<TipoDeUsuario> cbTipoUsuario;
    @FXML private VBox contenedorDinamico;

    private Object controladorHijo;
    private ProfesorDTO profesorExistente; // Solo se llena al editar un profesor
    private boolean modoEdicion = false;

    @FXML
    public void initialize() {
        cbTipoUsuario.getItems().setAll(TipoDeUsuario.values());
        cbTipoUsuario.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo != null && !modoEdicion) cambiarFragmento(nuevo);
        });
    }

    public void inicializarEdicion(ProfesorDTO profesor) {
        this.modoEdicion = true;
        this.profesorExistente = profesor;

        txtNombre.setText(profesor.getNombre());
        txtApellidoP.setText(profesor.getApellidoPaterno());
        txtApellidoM.setText(profesor.getApellidoMaterno());
        txtContrasenia.setText(profesor.getContrasenia());
        cbTipoUsuario.setValue(TipoDeUsuario.PROFESOR);
        cbTipoUsuario.setDisable(true);

        cambiarFragmento(TipoDeUsuario.PROFESOR);
        ((CamposProfesorControlador)controladorHijo).cargarDatos(profesor);
    }

    @FXML
    private void manejarGuardar() {
        try {
            if (cbTipoUsuario.getValue() == TipoDeUsuario.PROFESOR) {
                CamposProfesorControlador hijo = (CamposProfesorControlador) controladorHijo;

                ProfesorDTO profe = new ProfesorDTO(
                        modoEdicion ? profesorExistente.getIdUsuario() : 0,
                        txtNombre.getText(),
                        txtApellidoP.getText(),
                        txtApellidoM.getText(),
                        txtContrasenia.getText(),
                        TipoEstado.ACTIVO,
                        TipoDeUsuario.PROFESOR,
                        hijo.getNumeroPersonal(),
                        hijo.getTurno()
                );

                ProfesorDAO dao = new ProfesorDAO();
                if (modoEdicion) {
                    new UsuarioDAO().actualizarUsuario(profe);
                    dao.actualizarProfesor(profe);
                } else {
                    dao.agregarProfesor(profe);
                }
                cerrarVentana();
            }
            // Aquí irían los bloques else if para PRACTICANTE o COORDINADORllamando a sus respectivos constructores llenos
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al procesar el registro", e);
        }
    }

    private void cambiarFragmento(TipoDeUsuario tipo) {
        try {
            String nombreTipo = tipo.name().charAt(0) + tipo.name().substring(1).toLowerCase();
            String ruta = "/gui/vistas/FXMLFragmento" + nombreTipo + ".fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Node nodo = loader.load();
            contenedorDinamico.getChildren().setAll(nodo);
            controladorHijo = loader.getController();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en carga dinámica", e);
        }
    }

    @FXML private void manejarCancelar() { cerrarVentana(); }
    private void cerrarVentana() { ((Stage)txtNombre.getScene().getWindow()).close(); }
}