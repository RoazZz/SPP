package gui.controladores;

import excepciones.AutenticacionDeUsuarioExcepcion;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logica.dto.UsuarioDTO;
import logica.utilidades.AutenticacionUsuario;
import logica.utilidades.RegistradorBitacora;
import logica.utilidades.SesionUsuarioSingleton;

import javafx.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InicioSesionControlador {

    private static final Logger REGISTRADOR = Logger.getLogger(InicioSesionControlador.class.getName());

    @FXML private TextField txtUsuario;
    @FXML private PasswordField pswContrasenia;
    @FXML private Label lblMensaje;

    private final AutenticacionUsuario autenticacionServicio = new AutenticacionUsuario();
    private final NavegacionControlador navegacionControlador = new NavegacionControlador();

    @FXML
    private void manejarInicioDeSesion(ActionEvent evento) {
        if (!validarCampos()) {
            return;
        }
        autenticarYNavegar();
    }

    private boolean validarCampos() {
        if (txtUsuario.getText().trim().isEmpty() || pswContrasenia.getText().trim().isEmpty()) {
            lblMensaje.setText("Es obligatorio llenar todos los campos");
            return false;
        }
        return true;
    }


    private void autenticarYNavegar() {
        try {
            UsuarioDTO usuario = autenticarUsuario();
            navegar(usuario);
        } catch (EntidadNoEncontradaExcepcion entidadNoEncontradaExcepcion) {
            lblMensaje.setText("Usuario no encontrado");
            REGISTRADOR.log(Level.WARNING, "Usuario no encontrado", entidadNoEncontradaExcepcion);
        } catch (AutenticacionDeUsuarioExcepcion autenticacionExcepcion) {
            lblMensaje.setText(autenticacionExcepcion.getMessage());
            REGISTRADOR.log(Level.WARNING, "Error de autenticación", autenticacionExcepcion);
        } catch (DAOExcepcion daoExcepcion) {
            lblMensaje.setText("Error al conectar con la base de datos");
            REGISTRADOR.log(Level.SEVERE, "Error de base de datos en login", daoExcepcion);
        }
    }

    private UsuarioDTO autenticarUsuario() throws EntidadNoEncontradaExcepcion, AutenticacionDeUsuarioExcepcion, DAOExcepcion {
        UsuarioDTO usuario = autenticacionServicio.autenticar(txtUsuario.getText(), pswContrasenia.getText());
        SesionUsuarioSingleton.obtenerInstancia().iniciarSesion(usuario);
        RegistradorBitacora.registrar("INICIO_SESION", "El usuario inicio sesion en el sistema");
        return usuario;
    }

    private void navegar(UsuarioDTO usuario) throws AutenticacionDeUsuarioExcepcion {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        navegacionControlador.navegarSegunRol(usuario.getTipoDeUsuario(), stage);
    }
}


