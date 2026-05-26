package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.Regresable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import logica.dao.BuzonDAO;
import logica.dao.MensajeDAO;
import logica.dto.BuzonDTO;
import logica.dto.MensajeDTO;
import logica.utilidades.SesionUsuarioSingleton;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BuzonControlador implements Initializable, Regresable {

    private static final Logger logger = Logger.getLogger(BuzonControlador.class.getName());

    @FXML private TableView<MensajeDTO>           tablaMensajes;
    @FXML private TableColumn<MensajeDTO, String> colRemitente;
    @FXML private TableColumn<MensajeDTO, String> colAsunto;
    @FXML private TableColumn<MensajeDTO, String> colFecha;
    @FXML private Label    lblRemitente;
    @FXML private Label    lblAsunto;
    @FXML private Label    lblFecha;
    @FXML private TextArea txtContenido;
    @FXML private Label    lblError;

    private Scene escenaAnterior;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tablaMensajes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configurarColumnas();
        configurarSeleccion();
        cargarMensajes();
        limpiarVistaPrevia();
    }

    private void configurarColumnas() {
        colRemitente.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getNombreRemitente() != null
                        ? dato.getValue().getNombreRemitente()
                        : "Buzón #" + dato.getValue().getIdBuzonOrigen()));
        colAsunto.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getAsunto()));
        colFecha.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getFecha() != null
                        ? dato.getValue().getFecha().toLocalDate().toString()
                        : "Sin fecha"));
    }

    private void configurarSeleccion() {
        tablaMensajes.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, seleccionado) -> {
                    if (seleccionado != null) {
                        mostrarVistaPrevia(seleccionado);
                    } else {
                        limpiarVistaPrevia();
                    }
                }
        );
    }

    private void mostrarVistaPrevia(MensajeDTO mensaje) {
        lblRemitente.setText(mensaje.getNombreRemitente() != null
                ? mensaje.getNombreRemitente()
                : "Buzón #" + mensaje.getIdBuzonOrigen());
        lblAsunto.setText(mensaje.getAsunto());
        lblFecha.setText(mensaje.getFecha() != null
                ? mensaje.getFecha().toLocalDate().toString()
                : "Sin fecha");
        txtContenido.setText(mensaje.getContenido());
    }

    private void limpiarVistaPrevia() {
        lblRemitente.setText("Selecciona un mensaje para ver el detalle");
        lblAsunto.setText("-");
        lblFecha.setText("-");
        txtContenido.setText("");
        txtContenido.setPromptText("El contenido del mensaje aparecerá aquí");
    }

    private void cargarMensajes() {
        try {
            int idUsuario = SesionUsuarioSingleton.obtenerInstancia()
                    .obtenerUsuarioActual().getIdUsuario();

            BuzonDAO buzonDAO = new BuzonDAO();
            BuzonDTO buzon = buzonDAO.obtenerBuzonPorIdUsuario(idUsuario);

            MensajeDAO mensajeDAO = new MensajeDAO();
            List<MensajeDTO> mensajes = mensajeDAO.obtenerMensajesConRemitente(buzon.getIdBuzon());

            ObservableList<MensajeDTO> listaMensajes = FXCollections.observableArrayList(mensajes);
            tablaMensajes.setItems(listaMensajes);

            logger.log(Level.INFO, "Mensajes cargados correctamente para idUsuario: " + idUsuario);
        } catch (DAOExcepcion e) {
            logger.log(Level.SEVERE, "Error al cargar los mensajes del buzón", e);
            lblError.setText("Error al cargar los mensajes.");
        } catch (EntidadNoEncontradaExcepcion e) {
            logger.log(Level.WARNING, "No se encontró buzón para el usuario en sesión", e);
            lblError.setText("No se encontró un buzón asociado a tu cuenta.");
        }
    }

    @FXML
    private void abrirEnviarMensaje() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/gui/vista/FXMLEnviarMensaje.fxml"));
            Parent vista = loader.load();
            Stage escenario = new Stage();
            escenario.setScene(new Scene(vista));
            escenario.setTitle("Enviar Mensaje");
            escenario.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al abrir la ventana de enviar mensaje", e);
            lblError.setText("No se pudo abrir la ventana de enviar mensaje.");
        }
    }

    @Override
    public void setEscenaAnterior(Scene escena) {
        this.escenaAnterior = escena;
    }

    @FXML
    private void salir() {
        if (escenaAnterior != null) {
            Stage escenario = (Stage) tablaMensajes.getScene().getWindow();
            escenario.setScene(escenaAnterior);
        }
    }
}