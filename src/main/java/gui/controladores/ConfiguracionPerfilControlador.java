

package gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import logica.dao.UsuarioDAO;
import logica.dto.UsuarioDTO;
import logica.utilidades.CifradorContraseña;
import excepciones.DAOExcepcion;

public class ConfiguracionPerfilControlador {
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidoP;
    @FXML private TextField txtApellidoM;
    @FXML private PasswordField txtContraseniaActual;
    @FXML private PasswordField txtContraseniaNueva;

    private UsuarioDTO usuarioSesion;

    public void inicializar(UsuarioDTO usuario) {
        this.usuarioSesion = usuario;
        this.txtNombre.setText(usuario.getNombre());
        this.txtApellidoP.setText(usuario.getApellidoPaterno());
        this.txtApellidoM.setText(usuario.getApellidoMaterno());
    }

    @FXML
    public void manejarGuardarCambios() {
        if (validarCampos()) {
            ejecutarActualizacion();
        }
    }

    private boolean validarCampos() {
        boolean esValido = true;
        String error = "";

        if (txtNombre.getText().isEmpty() || txtContraseniaActual.getText().isEmpty()) {
            error = "Nombre y contraseña actual son obligatorios.";
            esValido = false;
        } else if (!CifradorContraseña.verificarContrasenia(txtContraseniaActual.getText(), usuarioSesion.getContrasenia())) {
            error = "La contraseña actual no coincide.";
            esValido = false;
        }

        if (!esValido) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", error);
        }

        return esValido;
    }

    private void ejecutarActualizacion() {
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            usuarioSesion.setNombre(txtNombre.getText());
            usuarioSesion.setApellidoPaterno(txtApellidoP.getText());
            usuarioSesion.setApellidoMaterno(txtApellidoM.getText());

            if (!txtContraseniaNueva.getText().isEmpty()) {
                usuarioSesion.setContrasenia(CifradorContraseña.cifrarContraseña(txtContraseniaNueva.getText()));
            }

            usuarioDAO.actualizarUsuario(usuarioSesion);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Perfil actualizado correctamente.");
        } catch (DAOExcepcion e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Sistema", "No se pudo conectar con la base de datos.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    public void manejarCancelar() {
        // Lógica para salir
    }
}