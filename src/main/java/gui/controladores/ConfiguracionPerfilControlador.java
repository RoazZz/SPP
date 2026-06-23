package gui.controladores;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import logica.dto.UsuarioDTO;
import logica.utilidades.RegistradorBitacora;
import logica.utilidades.SesionUsuarioSingleton;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ConfiguracionPerfilControlador implements Initializable {

    private static final Logger REGISTRADOR = Logger.getLogger(ConfiguracionPerfilControlador.class.getName());

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidoP;
    @FXML private TextField txtApellidoM;
    @FXML private Button btnCancelar;

    private UsuarioDTO usuarioSesion;

    @Override
    public void initialize(URL urlRecibida, ResourceBundle recursoRecibido) {
        this.usuarioSesion = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual();
        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        if (usuarioSesion != null) {
            txtNombre.setText(usuarioSesion.getNombre());
            txtApellidoP.setText(usuarioSesion.getApellidoPaterno());
            txtApellidoM.setText(usuarioSesion.getApellidoMaterno());
        }
    }

    @FXML
    private void manejarGuardarCambios(ActionEvent eventoClic) {
        if (validarCampos()) {
            actualizarInformacion();
        }
    }

    @FXML
    private void manejarCancelar(ActionEvent eventoClic) {
        btnCancelar.getScene().getWindow().hide();
    }

    private boolean validarCampos() {
        String nombre = txtNombre.getText().trim();
        String paterno = txtApellidoP.getText().trim();

        if (nombre.isEmpty() || paterno.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "El nombre y apellido paterno son obligatorios.");
            return false;
        }

        return true;
    }

    private void actualizarInformacion() {
        usuarioSesion.setNombre(txtNombre.getText().trim());
        usuarioSesion.setApellidoPaterno(txtApellidoP.getText().trim());
        usuarioSesion.setApellidoMaterno(txtApellidoM.getText().trim());

        RegistradorBitacora.registrar("ACTUALIZAR_PERFIL", "Actualizó su perfil");
        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Perfil actualizado.");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}