package gui.controladores;

import excepciones.DAOExcepcion;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import logica.interfaces.Regresable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import logica.dao.ProyectoDAO;
import logica.dao.SolicitaProyectoDAO;
import logica.dto.PracticanteDTO;
import logica.dto.ProyectoDTO;
import logica.dto.SolicitaProyectoDTO;
import logica.enums.TipoEstadoSolicitud;
import logica.utilidades.SesionUsuarioSingleton;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static gui.controladores.NavegacionControlador.regresar;

public class SolicitarProyectoControlador implements Initializable, Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(SolicitarProyectoControlador.class.getName());
    private static final int MAX = 3;

    @FXML private TextField txtBuscar;
    @FXML private TableView<ProyectoDTO> tvProyectos;
    @FXML private TableColumn<ProyectoDTO, String> colNombre;
    @FXML private TableColumn<ProyectoDTO, String> colOrganizacion;
    @FXML private TableColumn<ProyectoDTO, String> colDescripcion;
    @FXML private TableColumn<ProyectoDTO, Void> colSeleccionar;
    @FXML private Label lblPrioridad1;
    @FXML private Label lblPrioridad2;
    @FXML private Label lblPrioridad3;
    @FXML private TextField txtPeriodo;
    @FXML private Label lblError;

    private Scene escenaAnterior;
    private final ObservableList<ProyectoDTO> proyectosDisponibles = FXCollections.observableArrayList();
    private final List<ProyectoDTO> seleccionados = new ArrayList<>();

    @Override
    public void initialize(URL enlace, ResourceBundle resourceBundle) {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colOrganizacion.setCellValueFactory(new PropertyValueFactory<>("idOrganizacion"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colSeleccionar.setCellFactory(parametroColumna -> new CeldaAnadir(this::agregar));
        cargar();
        lblError.setVisible(false);
    }

    private static class CeldaAnadir extends TableCell<ProyectoDTO, Void> {
        private final Button btn;
        private final Consumer<ProyectoDTO> accion;
        public CeldaAnadir(Consumer<ProyectoDTO> accion) {
            this.accion = accion;
            this.btn = new Button("Añadir");
            this.btn.getStyleClass().add("btn-guardar");
            this.btn.setOnAction(e -> {
                ProyectoDTO p = getTableView().getItems().get(getIndex());
                this.accion.accept(p);
            });
        }
        @Override
        protected void updateItem(Void i, boolean v) {
            super.updateItem(i, v);
            setGraphic(v ? null : btn);
        }
    }

    private void cargar() {
        try {
            proyectosDisponibles.setAll(new ProyectoDAO().listarProyectos());
            tvProyectos.setItems(proyectosDisponibles);
        } catch (DAOExcepcion e) {
            REGISTRADOR.log(Level.SEVERE, "Error carga", e);
        }
    }

    @FXML
    private void manejarEnviarSolicitud(ActionEvent eventoClic) {
        String periodo = txtPeriodo.getText().trim();
        if (seleccionados.isEmpty() || periodo.isEmpty()) {
            lblError.setText("Selecciona proyectos y periodo.");
            lblError.setVisible(true);
            return;
        }
        try {
            if (SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual() instanceof PracticanteDTO practicante) {
                SolicitaProyectoDAO solicitaProyectoDAO = new SolicitaProyectoDAO();
                for (int indicePrioridad = 0; indicePrioridad < seleccionados.size(); indicePrioridad++) {
                    ProyectoDTO proyectoSeleccionado = seleccionados.get(indicePrioridad);
                    int prioridad = indicePrioridad + 1;
                    solicitaProyectoDAO.insertarSolicitudProyecto(new SolicitaProyectoDTO(
                            practicante.getMatricula(),
                            proyectoSeleccionado.getIdProyecto(),
                            TipoEstadoSolicitud.PENDIENTE,
                            periodo,
                            prioridad));
                }
                regresar(lblError, escenaAnterior);
            }
        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al registrar la solicitud de proyecto del practicante", excepcionCapturada);
            lblError.setText("No se pudo enviar la solicitud. Intente más tarde.");
            lblError.setVisible(true);
        }
    }

    private void agregar(ProyectoDTO p) {
        boolean existe = seleccionados.stream().anyMatch(sel -> sel.getIdProyecto() == p.getIdProyecto());
        if (!existe && seleccionados.size() < MAX) {
            seleccionados.add(p);
            actualizar();
        }
    }

    private void actualizar() {
        lblPrioridad1.setText(seleccionados.size() > 0 ? seleccionados.get(0).getNombre() : "Sin selección");
        lblPrioridad2.setText(seleccionados.size() > 1 ? seleccionados.get(1).getNombre() : "Sin selección");
        lblPrioridad3.setText(seleccionados.size() > 2 ? seleccionados.get(2).getNombre() : "Sin selección");
    }

    @Override public void setEscenaAnterior(Scene s) { this.escenaAnterior = s; }

    @FXML private void manejarSalir(ActionEvent e) { regresar((Node) e.getSource(), escenaAnterior); }
}