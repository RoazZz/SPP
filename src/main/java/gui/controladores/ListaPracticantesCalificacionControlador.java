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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import logica.dao.PracticanteDAO;
import logica.dto.PracticanteDTO;
import logica.interfaces.Regresable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static gui.controladores.NavegacionControlador.regresar;
public class ListaPracticantesCalificacionControlador implements Regresable {
    private static final Logger REGISTRADOR = Logger.getLogger(ListaPracticantesCalificacionControlador.class.getName());
    private static final String RUTA_CALIFICACION = "/gui/vista/FXMLCalificacionFinal.fxml";
    @FXML private TableView<PracticanteDTO> tablaPracticantes;
    @FXML private TableColumn<PracticanteDTO, String> colMatricula;
    @FXML private TableColumn<PracticanteDTO, String> colSemestre;
    @FXML private TableColumn<PracticanteDTO, Void> colAcciones;
    private Scene escenaAnterior;
    private PracticanteDAO practicanteDAO;
    @FXML
    private void initialize() {
        try {
            practicanteDAO = new PracticanteDAO();
        } catch (DAOExcepcion daoExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al inicializar el acceso a datos", daoExcepcion);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No fue posible conectar con la base de datos.");
            return;
        }
        configurarColumnas();
        cargarPracticantes();
    }
    private void configurarColumnas() {
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colSemestre.setCellValueFactory(new PropertyValueFactory<>("semestre"));
        colAcciones.setCellFactory(columnaRecibida -> new CeldaCalificarPracticanteControlador(this::abrirCalificacion));
    }
    private void cargarPracticantes() {
        try {
            List<PracticanteDTO> practicantes = practicanteDAO.listarPracticantes();
            ObservableList<PracticanteDTO> listaObservable = FXCollections.observableArrayList(practicantes);
            tablaPracticantes.setItems(listaObservable);
        } catch (DAOExcepcion daoExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al cargar los practicantes", daoExcepcion);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No fue posible cargar los practicantes.");
        }
    }
    private void abrirCalificacion(PracticanteDTO practicanteObjetivo) {
        try {
            FXMLLoader cargadorVista = new FXMLLoader(getClass().getResource(RUTA_CALIFICACION));
            Parent vistaCargada = cargadorVista.load();
            CalificacionFinalControlador controladorCalificacion = cargadorVista.getController();
            controladorCalificacion.cargarCalificacion(practicanteObjetivo.getMatricula());
            Stage escenarioEmergente = new Stage();
            escenarioEmergente.setScene(new Scene(vistaCargada));
            escenarioEmergente.showAndWait();
        } catch (IOException ioExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al abrir la calificacion final", ioExcepcion);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No fue posible abrir la calificacion final.");
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
