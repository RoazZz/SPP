

package gui.controladores;

import excepciones.ReglaDeNegocioExcepcion;
import interfaces.Regresable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import logica.dao.UsuarioDAO;
import logica.dto.ProfesorDTO;
import logica.dto.UsuarioDTO;
import logica.enums.TipoTurno;
import logica.utilidades.CifradorContraseña;
import excepciones.DAOExcepcion;
import logica.utilidades.SesionUsuarioSingleton;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfiguracionPerfilControlador implements Initializable, Regresable {
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidoP;
    @FXML private TextField txtApellidoM;
    @FXML private PasswordField txtContraseniaActual;
    @FXML private PasswordField txtContraseniaNueva;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    @FXML private TextField txtNumeroPersonal;
    @FXML private ComboBox<TipoTurno> cbTipoTurno;

    private Scene  escenaAnterior;
    private UsuarioDTO usuarioSesion = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarDatosUsuario();
        btnGuardar.setOnAction(e -> manejarGuardarCambios());
        btnCancelar.setOnAction(e -> regresar());

    }

    @FXML
    private void cargarDatosUsuario() {
        if (usuarioSesion != null) {
            txtNombre.setText(usuarioSesion.getNombre());
            txtApellidoP.setText(usuarioSesion.getApellidoPaterno());
            txtApellidoM.setText(usuarioSesion.getApellidoMaterno());
        }
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().trim().isEmpty() || txtContraseniaActual.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Faltantes",
                    "El nombre y la contraseña actual son obligatorios para confirmar cambios.");
            return false;
        }

        if (!CifradorContraseña.verificarContrasenia(txtContraseniaActual.getText(), usuarioSesion.getContrasenia())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Seguridad", "La contraseña actual es incorrecta.");
            return false;
        }

        return validarSegunEspecialidad();
    }

    private boolean validarSegunEspecialidad() {
        String tipo = usuarioSesion.getTipoDeUsuario().toString();

        try{
            switch (tipo) {
                case "PRACTICANTE":
                    // Llamada al método de validacion - Jared
                    return true;
                case "PROFESOR":
                    ProfesorControlador.validarCamposProfesor(
                            txtNumeroPersonal.getText(),
                            cbTipoTurno.getValue()
                    );
                    return true;
                case "COORDINADOR":
                    // Llamada al método de validacion - Jared
                default:
                    return true;
            }
        }catch (ReglaDeNegocioExcepcion e){
            mostrarAlerta(Alert.AlertType.WARNING, "Dato Inválido", e.getMessage());
            return false;
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un error al validar los datos.");
            return false;
        }
    }

    private void ejecutarActualizacion() {
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            usuarioSesion.setNombre(txtNombre.getText());
            usuarioSesion.setApellidoPaterno(txtApellidoP.getText());
            usuarioSesion.setApellidoMaterno(txtApellidoM.getText());

            if (usuarioSesion instanceof ProfesorDTO) {
                ProfesorDTO profe = (ProfesorDTO) usuarioSesion;
                profe.setNumeroDePersonal(txtNumeroPersonal.getText().trim());
                profe.setTurno(cbTipoTurno.getValue());
            }

            if (!txtContraseniaNueva.getText().trim().isEmpty()) {
                String nuevoHash = CifradorContraseña.cifrarContraseña(txtContraseniaNueva.getText());
                usuarioSesion.setContrasenia(nuevoHash);
            }

            usuarioDAO.actualizarUsuario(usuarioSesion);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Perfil actualizado correctamente.");
            txtContraseniaActual.clear();
            txtContraseniaNueva.clear();
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
    public void manejarGuardarCambios() {
        if (validarFormulario()) {
            ejecutarActualizacion();
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
            escenario.show();
        }
    }
}