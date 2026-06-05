package gui.controladores;

import excepciones.DAOExcepcion;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import logica.dao.AdministradorDAO;

import java.util.logging.Level;
import java.util.logging.Logger;

import static gui.controladores.NavegacionControlador.abrirVentana;

public class ErrorConexionControlador {

    private static final Logger REGISTRADOR = Logger.getLogger(ErrorConexionControlador.class.getName());
    private static final String RUTA_VISTA_INICIO_SESION = "/gui/vista/FXMLInicioSesion.fxml";
    private static final String RUTA_VISTA_BIENVENIDA = "/gui/vista/FXMLBienvenida.fxml";

    @FXML private Label lblMensajeEstado;
    @FXML private Button btnSalir;
    @FXML private Button btnReintentar;

    @FXML
    private void manejarSalir(ActionEvent eventoBoton) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void manejarReintentarConexion(ActionEvent eventoBoton) {
        try {
            AdministradorDAO administradorDAO = new AdministradorDAO();
            String rutaSiguienteVista;

            if (administradorDAO.existeAlgunAdministrador()) {
                rutaSiguienteVista = RUTA_VISTA_INICIO_SESION;
            } else {
                rutaSiguienteVista = RUTA_VISTA_BIENVENIDA;
            }

            Node nodoOrigen = (Node) eventoBoton.getSource();
            abrirVentana(rutaSiguienteVista, nodoOrigen);

        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.WARNING, "Intento de reconexión fallido.", excepcionCapturada);
            lblMensajeEstado.setText("El servidor sigue sin responder. Intente de nuevo.");
        }
    }
}