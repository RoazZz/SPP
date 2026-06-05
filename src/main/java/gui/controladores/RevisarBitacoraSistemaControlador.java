package gui.controladores;

import excepciones.DAOExcepcion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import logica.dao.BitacoraDAO;
import logica.dto.BitacoraDTO;
import logica.interfaces.Regresable;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RevisarBitacoraSistemaControlador implements Initializable, Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(RevisarBitacoraSistemaControlador.class.getName());

    @FXML private TextField txtBuscarMatricula;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiar;
    @FXML private TableView<BitacoraDTO> tvBitacora;
    @FXML private TableColumn<BitacoraDTO, String> colFechaHora;
    @FXML private TableColumn<BitacoraDTO, String> colMatricula;
    @FXML private TableColumn<BitacoraDTO, String> colTipoEvento;
    @FXML private TableColumn<BitacoraDTO, String> colDescripcion;
    @FXML private Label lblMensaje;

    private Scene escenaAnterior;
    private final ObservableList<BitacoraDTO> registrosCompletos = FXCollections.observableArrayList();
    private FilteredList<BitacoraDTO> registrosFiltrados;

    @Override
    public void initialize(URL url, ResourceBundle recursoRecibido) {
        colFechaHora.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colTipoEvento.setCellValueFactory(new PropertyValueFactory<>("tipoEvento"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionEvento"));

        cargarBitacora();
    }

    private void cargarBitacora() {
        try {
            BitacoraDAO bitacoraDAO = new BitacoraDAO();
            List<BitacoraDTO> registrosRecuperados = bitacoraDAO.listarBitacoras();
            registrosCompletos.setAll(registrosRecuperados);
            registrosFiltrados = new FilteredList<>(registrosCompletos, registroVisible -> true);
            tvBitacora.setItems(registrosFiltrados);

            if (registrosRecuperados.isEmpty()) {
                lblMensaje.setText("No hay registros que mostrar.");
            }
        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al cargar la bitácora del sistema", excepcionCapturada);
            lblMensaje.getStyleClass().add("label-error");
            lblMensaje.setText("No se pudo conectar a la base de datos. Intentar más tarde.");
        }
    }

    @FXML
    private void manejarBuscar(ActionEvent eventoClic) {
        String matriculaBuscada = txtBuscarMatricula.getText().trim().toLowerCase();
        if (registrosFiltrados == null) {
            return;
        }
        if (matriculaBuscada.isEmpty()) {
            registrosFiltrados.setPredicate(registroVisible -> true);
        } else {
            registrosFiltrados.setPredicate(registroVisible ->
                    registroVisible.getMatricula() != null
                            && registroVisible.getMatricula().toLowerCase().contains(matriculaBuscada));
        }
        if (registrosFiltrados.isEmpty()) {
            lblMensaje.setText("No hay registros que mostrar.");
        } else {
            lblMensaje.setText("");
        }
    }

    @FXML
    private void manejarLimpiar(ActionEvent eventoClic) {
        txtBuscarMatricula.clear();
        if (registrosFiltrados != null) {
            registrosFiltrados.setPredicate(registroVisible -> true);
        }
        lblMensaje.setText("");
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void manejarRegresar(ActionEvent eventoClic) {
        if (escenaAnterior != null) {
            Stage escenarioControlado = (Stage) ((Node) eventoClic.getSource()).getScene().getWindow();
            escenarioControlado.setScene(escenaAnterior);
            escenarioControlado.show();
        }
    }
}