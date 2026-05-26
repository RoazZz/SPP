package gui.controladores;

import excepciones.DAOExcepcion;
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
import javafx.stage.Stage;
import logica.dto.ProyectoDTO;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProyectosControlador implements Initializable, Regresable {

    private static final Logger logger = Logger.getLogger(ProyectosControlador.class.getName());

    @FXML private TableView<ProyectoDTO>           tablaProyectos;
    @FXML private TableColumn<ProyectoDTO, String> colNombre;
    @FXML private TableColumn<ProyectoDTO, String> colDescripcion;
    @FXML private TableColumn<ProyectoDTO, String> colOrganizacion;
    @FXML private Label lblMensaje;

    private Scene escenaAnterior;
    private ProyectoControlador proyectoControlador;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            proyectoControlador = new ProyectoControlador();
        } catch (DAOExcepcion e) {
            logger.log(Level.SEVERE, "Error al inicializar ProyectoControlador", e);
            lblMensaje.setText("Error al conectar con la base de datos.");
            return;
        }
        tablaProyectos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        configurarColumnas();
        cargarProyectos();
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getNombre()));
        colDescripcion.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getDescripcion()));
        colOrganizacion.setCellValueFactory(dato ->
                new SimpleStringProperty(dato.getValue().getIdOrganizacion()));
    }

    void cargarProyectos() {
        try {
            List<ProyectoDTO> proyectos = proyectoControlador.listarProyectos();
            ObservableList<ProyectoDTO> listaProyectos = FXCollections.observableArrayList(proyectos);
            tablaProyectos.setItems(listaProyectos);
            logger.log(Level.INFO, "Proyectos cargados correctamente");
        } catch (DAOExcepcion e) {
            logger.log(Level.SEVERE, "Error al cargar proyectos", e);
            lblMensaje.setText("Error al cargar los proyectos.");
        }
    }

    @FXML
    private void abrirAnadirProyecto() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/gui/vista/FXMLFormularioProyecto.fxml"));
            Parent vista = loader.load();
            Stage escenario = (Stage) tablaProyectos.getScene().getWindow();

            Object controlador = loader.getController();
            if (controlador instanceof Regresable regresable) {
                regresable.setEscenaAnterior(escenario.getScene());
            }

            escenario.setScene(new Scene(vista));
            escenario.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al abrir formulario de proyecto", e);
            lblMensaje.setText("No se pudo abrir el formulario.");
        }
    }

    @FXML
    private void abrirEditarProyecto() {
        ProyectoDTO seleccionado = tablaProyectos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            lblMensaje.setText("Selecciona un proyecto para editar.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/gui/vista/FXMLFormularioProyecto.fxml"));
            Parent vista = loader.load();
            Stage escenario = (Stage) tablaProyectos.getScene().getWindow();

            FormularioProyectoControlador formularioProyectoControlador = loader.getController();
            formularioProyectoControlador.setEscenaAnterior(escenario.getScene());
            formularioProyectoControlador.cargarProyecto(seleccionado);
            formularioProyectoControlador.configurarTitulo("Editar Proyecto");

            escenario.setScene(new Scene(vista));
            escenario.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al abrir formulario de edición", e);
            lblMensaje.setText("No se pudo abrir el formulario de edición.");
        }
    }

    @Override
    public void setEscenaAnterior(Scene escena) {
        this.escenaAnterior = escena;
    }

    @FXML
    private void salir() {
        if (escenaAnterior != null) {
            Stage escenario = (Stage) tablaProyectos.getScene().getWindow();
            escenario.setScene(escenaAnterior);
            escenario.show();
        }
    }
}