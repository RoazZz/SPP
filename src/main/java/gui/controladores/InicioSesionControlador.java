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
import logica.utilidades.SesionUsuarioSingleton;


import java.util.logging.Level;
import java.util.logging.Logger;

public class InicioSesionControlador {

    private static final Logger logger = Logger.getLogger(InicioSesionControlador.class.getName());

    @FXML private TextField txtUsuario;
    @FXML private PasswordField pswContrasenia;
    @FXML private Label lblMensaje;

    private final AutenticacionUsuario autenticacionServicio = new AutenticacionUsuario();
    private final NavegacionControlador navegacionControlador = new NavegacionControlador();

    @FXML
    public void manejarInicioDeSesion() {
        if (!validarCampos()) {
            return;
        }
        autenticarYNavegar();
    }

    private boolean validarCampos() {
        if (txtUsuario.getText().trim().isEmpty() || pswContrasenia.getText().trim().isEmpty()) {
            lblMensaje.setText("Es obligatorio llenar todos los campos");
        }
        return true;
    }

    private void autenticarYNavegar() {
        try {
            UsuarioDTO usuario = autenticarUsuario();
            navegar(usuario);
        } catch (EntidadNoEncontradaExcepcion e) {
            lblMensaje.setText("Usuario no encontrado");
            logger.log(Level.WARNING, "Usuario no encontrado", e);
        } catch (AutenticacionDeUsuarioExcepcion e) {
            lblMensaje.setText(e.getMessage());
            logger.log(Level.WARNING, "Error de autenticación", e);
        } catch (DAOExcepcion e) {
            lblMensaje.setText("Error al conectar con la base de datos");
            logger.log(Level.SEVERE, "Error de base de datos en login", e);
        }
    }

    private UsuarioDTO autenticarUsuario() throws EntidadNoEncontradaExcepcion, AutenticacionDeUsuarioExcepcion, DAOExcepcion {
        UsuarioDTO usuario = autenticacionServicio.autenticar(
                txtUsuario.getText().trim(),
                pswContrasenia.getText().trim()
        );
        SesionUsuarioSingleton.obtenerInstancia().iniciarSesion(usuario);
        return usuario;
    }

    private void navegar(UsuarioDTO usuario) throws AutenticacionDeUsuarioExcepcion {
        Stage stage = (Stage) txtUsuario.getScene().getWindow();
        navegacionControlador.navegarSegunRol(usuario.getTipoDeUsuario(), stage);
    }
}


