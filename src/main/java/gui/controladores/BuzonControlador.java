package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.Regresable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
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

import static gui.controladores.NavegacionControlador.regresar;

public class BuzonControlador implements Initializable, Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(BuzonControlador.class.getName());

    @FXML private TableView<MensajeDTO> tablaMensajes;
    @FXML private TableColumn<MensajeDTO, String> colRemitente;
    @FXML private TableColumn<MensajeDTO, String> colAsunto;
    @FXML private TableColumn<MensajeDTO, String> colFecha;
    @FXML private Label lblRemitente;
    @FXML private Label lblAsunto;
    @FXML private Label lblFecha;
    @FXML private TextArea txtContenido;
    @FXML private Label lblError;

    private Scene escenaAnterior;

    @Override
    public void initialize(URL urlRecibida, ResourceBundle recursoRecibido) {
        tablaMensajes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colRemitente.setCellValueFactory(new PropertyValueFactory<>("nombreRemitente"));
        colAsunto.setCellValueFactory(new PropertyValueFactory<>("asunto"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        configurarSeleccion();
        cargarMensajes();
        limpiarVistaPrevia();
    }

    private void configurarSeleccion() {
        tablaMensajes.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MensajeDTO>() {
            @Override
            public void changed(ObservableValue<? extends MensajeDTO> observable, MensajeDTO anterior, MensajeDTO seleccionado) {
                if (seleccionado != null) {
                    mostrarVistaPrevia(seleccionado);
                } else {
                    limpiarVistaPrevia();
                }
            }
        });
    }

    private void mostrarVistaPrevia(MensajeDTO mensaje) {
        String remitente = "Buzón #" + mensaje.getIdBuzonOrigen();
        if (mensaje.getNombreRemitente() != null) {
            remitente = mensaje.getNombreRemitente();
        }
        lblRemitente.setText(remitente);
        lblAsunto.setText(mensaje.getAsunto());

        String fechaTexto = "Sin fecha";
        if (mensaje.getFecha() != null) {
            fechaTexto = mensaje.getFecha().toLocalDate().toString();
        }
        lblFecha.setText(fechaTexto);
        txtContenido.setText(mensaje.getContenido());
    }

    private void limpiarVistaPrevia() {
        lblRemitente.setText("Selecciona un mensaje");
        lblAsunto.setText("-");
        lblFecha.setText("-");
        txtContenido.setText("");
    }

    private void cargarMensajes() {
        try {
            int idUsuario = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getIdUsuario();
            BuzonDTO buzon = new BuzonDAO().obtenerBuzonPorIdUsuario(idUsuario);
            List<MensajeDTO> mensajes = new MensajeDAO().obtenerMensajesConRemitente(buzon.getIdBuzon());
            tablaMensajes.setItems(FXCollections.observableArrayList(mensajes));
        } catch (DAOExcepcion | EntidadNoEncontradaExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error carga buzón", excepcionCapturada);
            lblError.setText("Error al cargar mensajes.");
        }
    }

    @FXML
    private void abrirEnviarMensaje(ActionEvent eventoClic) {
        try {
            FXMLLoader cargador = new FXMLLoader(getClass().getResource("/gui/vista/FXMLEnviarMensaje.fxml"));
            Parent vista = cargador.load();
            Stage escenario = new Stage();
            escenario.setScene(new Scene(vista));
            escenario.setTitle("Enviar Mensaje");
            escenario.show();
        } catch (IOException excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error ventana", excepcionCapturada);
        }
    }

    @FXML
    private void salir(ActionEvent eventoClic) {
        Node nodo = (Node) eventoClic.getSource();
        nodo.getScene().getWindow().hide();
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void manejarClicCancelar(ActionEvent eventoBoton) {
        regresar((Node) eventoBoton.getSource(), this.escenaAnterior);
    }
}