package gui.controladores;
import excepciones.DAOExcepcion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import logica.dao.AutoevaluacionDAO;
import logica.dto.AutoevaluacionDTO;
import logica.interfaces.Regresable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static gui.controladores.NavegacionControlador.regresar;

public class ListaAutoevaluacionesControlador implements Regresable {
    private static final Logger REGISTRADOR = Logger.getLogger(ListaAutoevaluacionesControlador.class.getName());
    private static final String RUTA_EVALUAR = "/gui/vista/FXMLEvaluarAutoevaluacion.fxml";
    @FXML private TableView<AutoevaluacionDTO> tablaAutoevaluaciones;
    @FXML private TableColumn<AutoevaluacionDTO, Integer> colId;
    @FXML private TableColumn<AutoevaluacionDTO, String> colMatricula;
    @FXML private TableColumn<AutoevaluacionDTO, java.math.BigDecimal> colCalificacion;
    @FXML private TableColumn<AutoevaluacionDTO, Void> colAcciones;
    private Scene escenaAnterior;
    private AutoevaluacionDAO autoevaluacionDAO;
    @FXML
    private void initialize() {
        try {
            autoevaluacionDAO = new AutoevaluacionDAO();
        } catch (DAOExcepcion daoExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al inicializar el acceso a datos", daoExcepcion);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No fue posible conectar con la base de datos.");
            return;
        }
        configurarColumnas();
        cargarAutoevaluaciones();
    }
    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idAutoevaluacion"));
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colCalificacion.setCellValueFactory(new PropertyValueFactory<>("calificacion"));
        colAcciones.setCellFactory(columnaRecibida -> new CeldaEvaluarAutoevaluacionControlador(this::abrirEvaluacion));
    }
    private void cargarAutoevaluaciones() {
        try {
            List<AutoevaluacionDTO> autoevaluaciones = autoevaluacionDAO.obtenerTodasLasAutoevaluaciones();
            ObservableList<AutoevaluacionDTO> listaObservable = FXCollections.observableArrayList(autoevaluaciones);
            tablaAutoevaluaciones.setItems(listaObservable);
        } catch (DAOExcepcion daoExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al cargar las autoevaluaciones", daoExcepcion);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No fue posible cargar las autoevaluaciones.");
        }
    }
    private void abrirEvaluacion(AutoevaluacionDTO autoevaluacionObjetivo) {
        try {
            FXMLLoader cargadorVista = new FXMLLoader(getClass().getResource(RUTA_EVALUAR));
            Parent vistaCargada = cargadorVista.load();
            EvaluarAutoevaluacionControlador controladorEvaluacion = cargadorVista.getController();
            controladorEvaluacion.cargarAutoevaluacion(autoevaluacionObjetivo);
            Stage escenarioEmergente = new Stage();
            escenarioEmergente.setScene(new Scene(vistaCargada));
            escenarioEmergente.showAndWait();
            cargarAutoevaluaciones();
        } catch (IOException ioExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al abrir la evaluacion de autoevaluacion", ioExcepcion);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No fue posible abrir la evaluacion.");
        }
    }
    @FXML
    private void manejarSalir(ActionEvent eventoBoton) {
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
