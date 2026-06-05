package gui.controladores;

import excepciones.DAOExcepcion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import logica.dao.AdministradorDAO;
import logica.dao.UsuarioDAO;
import logica.dto.AdministradorDTO;
import logica.dto.UsuarioDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstadoUsuario;
import logica.utilidades.CifradorContrasenia;
import java.util.logging.Level;
import java.util.logging.Logger;

import static gui.controladores.NavegacionControlador.abrirVentana;

public class RegistroAdministradorControlador {

    private static final Logger REGISTRADOR = Logger.getLogger(RegistroAdministradorControlador.class.getName());
    private static final int LONGITUD_MINIMA_CONTRASENIA = 8;
    private static final String RUTA_VISTA_INICIO_SESION = "/gui/vista/FXMLInicioSesion.fxml";

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidoPaterno;
    @FXML private TextField txtApellidoMaterno;
    @FXML private PasswordField txtContrasenia;
    @FXML private Button btnGuardar;

    @FXML
    private void manejarGuardar(ActionEvent eventoBoton) {
        String nombre = txtNombre.getText().trim();
        String apellidoPaterno = txtApellidoPaterno.getText().trim();
        String apellidoMaterno = txtApellidoMaterno.getText().trim();
        String contrasenia = txtContrasenia.getText();

        String errorValidacion = validarCampos(nombre, apellidoPaterno, contrasenia);
        if (errorValidacion != null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Atención", errorValidacion);
            return;
        }

        try {
            registrarAdministrador(nombre, apellidoPaterno, apellidoMaterno, contrasenia);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Listo", "Administrador registrado correctamente.");
            Node nodoOrigen = (Node) eventoBoton.getSource();
            abrirInicioSesion(nodoOrigen);

        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al registrar el administrador en el primer arranque.", excepcionCapturada);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No fue posible registrar el administrador.");
        }
    }

    private String validarCampos(String nombre, String apellidoPaterno, String contrasenia) {
        if (nombre.isEmpty()) {
            return "El nombre es obligatorio.";
        }
        if (apellidoPaterno.isEmpty()) {
            return "El apellido paterno es obligatorio.";
        }
        if (contrasenia.length() < LONGITUD_MINIMA_CONTRASENIA) {
            return "La contraseña debe tener al menos " + LONGITUD_MINIMA_CONTRASENIA + " caracteres.";
        }
        return null;
    }

    private void registrarAdministrador(String nombre, String apellidoPaterno, String apellidoMaterno, String contrasenia) throws DAOExcepcion {
        String contraseniaCifrada = CifradorContrasenia.cifrarContraseña(contrasenia);

        AdministradorDTO administrador = new AdministradorDTO(
                0,
                nombre,
                apellidoPaterno,
                apellidoMaterno,
                contraseniaCifrada,
                TipoEstadoUsuario.ACTIVO,
                TipoDeUsuario.ADMIN,
                0
        );

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        UsuarioDTO usuarioCreado = usuarioDAO.agregarUsuario(administrador);

        administrador.setIdUsuario(usuarioCreado.getIdUsuario());

        AdministradorDAO administradorDAO = new AdministradorDAO();
        administradorDAO.agregarAdministrador(administrador);
    }

    private void abrirInicioSesion(Node nodoOrigen) {
        abrirVentana(RUTA_VISTA_INICIO_SESION, nodoOrigen);
    }

    private void mostrarAlerta(Alert.AlertType tipoAlert, String tituloAlert, String mensajeAlert) {
        Alert alerta = new Alert(tipoAlert, mensajeAlert, ButtonType.OK);
        alerta.setTitle(tituloAlert);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}