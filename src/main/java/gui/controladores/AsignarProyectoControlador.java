package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.ReglaDeNegocioExcepcion;
import interfaces.Regresable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import logica.dto.CoordinadorDTO;
import logica.dto.SolicitaProyectoDTO;
import logica.dto.UsuarioDTO;
import logica.enums.TipoEstadoSolicitud;
import logica.utilidades.SesionUsuarioSingleton;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsignarProyectoControlador implements Regresable {

    private static final Logger LOGGER = Logger.getLogger(AsignarProyectoControlador.class.getName());

    @FXML private TextField txtPeriodo;
    @FXML private TableView<SolicitaProyectoDTO> tablaSolicitudes;
    @FXML private TableColumn<SolicitaProyectoDTO, String> colMatricula;
    @FXML private TableColumn<SolicitaProyectoDTO, Integer> colIdProyecto;
    @FXML private TableColumn<SolicitaProyectoDTO, String> colPeriodo;
    @FXML private TableColumn<SolicitaProyectoDTO, TipoEstadoSolicitud> colEstado;
    @FXML private TableColumn<SolicitaProyectoDTO, Void> colAsignar;
    @FXML private Label lblMensaje;
    @FXML private Button btnSalir;

    private AsignacionProyectoControlador asignacionProyectoControlador;
    private final ObservableList<SolicitaProyectoDTO> listaSolicitudes = FXCollections.observableArrayList();
    private Scene escenaAnterior;

    @FXML
    public void initialize() {
        try {
            asignacionProyectoControlador = new AsignacionProyectoControlador();
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar AsignacionProyectoControlador", e);
            mostrarMensaje("Error al conectar con la base de datos.", false);
        }
        configurarTabla();
        lblMensaje.setVisible(false);
    }

    private void configurarTabla() {
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colIdProyecto.setCellValueFactory(new PropertyValueFactory<>("idProyecto"));
        colPeriodo.setCellValueFactory(new PropertyValueFactory<>("periodo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoProyecto"));

        colAsignar.setCellFactory(parametro -> new TableCell<>() {
            private final Button btnAsignar = new Button("Asignar");
            {
                btnAsignar.getStyleClass().add("btn-guardar");
                btnAsignar.setOnAction(event -> {
                    SolicitaProyectoDTO solicitud = getTableView().getItems().get(getIndex());
                    manejarAsignar(solicitud);
                });
            }

            @Override
            protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : btnAsignar);
            }
        });

        tablaSolicitudes.setItems(listaSolicitudes);
    }

    @FXML
    private void manejarBuscar() {
        lblMensaje.setVisible(false);
        String periodo = txtPeriodo.getText().trim();
        if (periodo.isEmpty()) {
            mostrarMensaje("Debes ingresar un periodo para buscar.", false);
            return;
        }
        try {
            List<SolicitaProyectoDTO> solicitudes = asignacionProyectoControlador.obtenerSolicitudesPendientes(periodo);
            listaSolicitudes.setAll(solicitudes);
            if (solicitudes.isEmpty()) {
                mostrarMensaje("No hay solicitudes pendientes para el periodo indicado.", false);
            }
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al buscar solicitudes", e);
            mostrarMensaje("Error al buscar solicitudes. Intente de nuevo.", false);
        }
    }

    @FXML
    private void manejarLimpiar() {
        txtPeriodo.clear();
        listaSolicitudes.clear();
        lblMensaje.setVisible(false);
    }

    private void manejarAsignar(SolicitaProyectoDTO solicitud) {
        try {
            UsuarioDTO usuarioActual = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual();
            if (!(usuarioActual instanceof CoordinadorDTO)) {
                mostrarMensaje("Solo un coordinador puede asignar proyectos.", false);
                return;
            }
            String numeroPersonal = ((CoordinadorDTO) usuarioActual).getNumeroPersonal();
            asignacionProyectoControlador.procesarAsignacionProyecto(solicitud, numeroPersonal);
            listaSolicitudes.remove(solicitud);
            mostrarMensaje("Proyecto asignado correctamente.", true);
        } catch (ReglaDeNegocioExcepcion e) {
            LOGGER.log(Level.WARNING, "Regla de negocio violada al asignar proyecto", e);
            mostrarMensaje(e.getMessage(), false);
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error de BD al asignar proyecto", e);
            mostrarMensaje("Error al asignar el proyecto. Intente de nuevo.", false);
        }
    }

    @FXML
    private void manejarSalir() {
        regresar();
    }

    private void mostrarMensaje(String mensaje, boolean exito) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle(exito ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        lblMensaje.setVisible(true);
    }

    @Override
    public void setEscenaAnterior(Scene escena) {
        this.escenaAnterior = escena;
    }

    private void regresar() {
        if (escenaAnterior != null) {
            Stage escenario = (Stage) btnSalir.getScene().getWindow();
            escenario.setScene(escenaAnterior);
        }
    }
}