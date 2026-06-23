package gui.controladores;

import excepciones.DAOExcepcion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import logica.dao.BitacoraSistemaSistemaDAO;
import logica.dto.BitacoraSistemaDTO;
import logica.interfaces.Regresable;
import logica.utilidades.RegistradorBitacora;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static gui.controladores.NavegacionControlador.regresar;

public class BitacoraSistemaControlador implements Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(BitacoraSistemaControlador.class.getName());
    private static final String TEXTO_VER_ARCHIVO = "VER ARCHIVO .TXT";
    private static final String TEXTO_OCULTAR_ARCHIVO = "OCULTAR ARCHIVO .TXT";
    private static final String MENSAJE_SIN_REGISTROS = "No hay registros que mostrar.";

    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnLimpiar;
    @FXML private Button btnVerArchivo;
    @FXML private Button btnRegresar;
    @FXML private TextArea txtAreaArchivo;
    @FXML private TableView<BitacoraSistemaDTO> tvBitacora;
    @FXML private TableColumn<BitacoraSistemaDTO, String> colFechaHora;
    @FXML private TableColumn<BitacoraSistemaDTO, String> colRolUsuario;
    @FXML private TableColumn<BitacoraSistemaDTO, String> colNombreUsuario;
    @FXML private TableColumn<BitacoraSistemaDTO, String> colTipoEvento;
    @FXML private TableColumn<BitacoraSistemaDTO, String> colDescripcion;
    @FXML private Label lblMensaje;

    private Scene escenaAnterior;
    private BitacoraSistemaSistemaDAO bitacoraSistemaDAO;
    private final ObservableList<BitacoraSistemaDTO> registrosCompletos = FXCollections.observableArrayList();
    private FilteredList<BitacoraSistemaDTO> registrosFiltrados;

    @FXML
    private void initialize() {
        try {
            bitacoraSistemaDAO = new BitacoraSistemaSistemaDAO();
        } catch (DAOExcepcion daoExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al inicializar el acceso a datos", daoExcepcion);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No fue posible conectar con la base de datos.");
            return;
        }

        configurarColumnas();
        cargarBitacora();
    }

    private void configurarColumnas() {
        colFechaHora.setCellValueFactory(new PropertyValueFactory<>("fechaHora"));
        colRolUsuario.setCellValueFactory(new PropertyValueFactory<>("rolUsuario"));
        colNombreUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        colTipoEvento.setCellValueFactory(new PropertyValueFactory<>("tipoEvento"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcionEvento"));
    }

    private void cargarBitacora() {
        try {
            List<BitacoraSistemaDTO> registrosRecuperados = bitacoraSistemaDAO.listarBitacoras();
            registrosCompletos.setAll(registrosRecuperados);
            registrosFiltrados = new FilteredList<>(registrosCompletos, registroVisible -> true);
            tvBitacora.setItems(registrosFiltrados);

            if (registrosRecuperados.isEmpty()) {
                lblMensaje.setText(MENSAJE_SIN_REGISTROS);
            }
        } catch (DAOExcepcion daoExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al cargar la bitacora del sistema", daoExcepcion);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No fue posible cargar la bitacora del sistema.");
        }
    }

    @FXML
    private void manejarBuscar(ActionEvent eventoBoton) {
        if (registrosFiltrados == null) {
            return;
        }

        String textoBuscado = txtBuscar.getText().trim().toLowerCase();
        if (textoBuscado.isEmpty()) {
            registrosFiltrados.setPredicate(registroVisible -> true);
        } else {
            registrosFiltrados.setPredicate(registroVisible -> coincideConBusqueda(registroVisible, textoBuscado));
        }

        if (registrosFiltrados.isEmpty()) {
            lblMensaje.setText(MENSAJE_SIN_REGISTROS);
        } else {
            lblMensaje.setText("");
        }
    }

    private boolean coincideConBusqueda(BitacoraSistemaDTO registro, String textoBuscado) {
        boolean coincideNombre = registro.getNombreUsuario() != null
                && registro.getNombreUsuario().toLowerCase().contains(textoBuscado);
        boolean coincideRol = registro.getRolUsuario() != null
                && registro.getRolUsuario().toLowerCase().contains(textoBuscado);
        return coincideNombre || coincideRol;
    }

    @FXML
    private void manejarLimpiar(ActionEvent eventoBoton) {
        txtBuscar.clear();
        if (registrosFiltrados != null) {
            registrosFiltrados.setPredicate(registroVisible -> true);
        }
        lblMensaje.setText("");
    }

    @FXML
    private void manejarVerArchivo(ActionEvent eventoBoton) {
        if (txtAreaArchivo.isVisible()) {
            txtAreaArchivo.setVisible(false);
            txtAreaArchivo.setManaged(false);
            btnVerArchivo.setText(TEXTO_VER_ARCHIVO);
            return;
        }

        txtAreaArchivo.setText(RegistradorBitacora.leerArchivoBitacora());
        txtAreaArchivo.setVisible(true);
        txtAreaArchivo.setManaged(true);
        btnVerArchivo.setText(TEXTO_OCULTAR_ARCHIVO);
    }

    @FXML
    private void manejarRegresar(ActionEvent eventoBoton) {
        Node nodoOrigen = (Node) eventoBoton.getSource();
        regresar(nodoOrigen, this.escenaAnterior);
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}