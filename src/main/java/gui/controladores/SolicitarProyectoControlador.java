package gui.controladores;

import excepciones.DAOExcepcion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import logica.dao.ProyectoDAO;
import logica.dao.SolicitaProyectoDAO;
import logica.dto.ProyectoDTO;
import logica.dto.SolicitaProyectoDTO;
import logica.enums.TipoEstadoSolicitud;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SolicitarProyectoControlador implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(SolicitarProyectoControlador.class.getName());
    private static final int MAXIMO_SELECCIONES = 3;

    @FXML private TextField txtBuscar;
    @FXML private TableView<ProyectoDTO> tablaProyectos;
    @FXML private TableColumn<ProyectoDTO, String> colNombre;
    @FXML private TableColumn<ProyectoDTO, String> colOrganizacion;
    @FXML private TableColumn<ProyectoDTO, String> colDescripcion;
    @FXML private TableColumn<ProyectoDTO, Void> colSeleccionar;
    @FXML private Label lblPrioridad1;
    @FXML private Label lblPrioridad2;
    @FXML private Label lblPrioridad3;
    @FXML private TextField txtPeriodo;
    @FXML private Label lblError;
    @FXML private Button btnEnviar;

    private final ObservableList<ProyectoDTO> todosLosProyectos = FXCollections.observableArrayList();
    private final List<ProyectoDTO> seleccionados = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblError.setVisible(false);
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colOrganizacion.setCellValueFactory(new PropertyValueFactory<>("idOrganizacion"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        cargarProyectos();
    }

    private void cargarProyectos() {
        try {
            ProyectoDAO proyectoDAO = new ProyectoDAO();
            List<ProyectoDTO> proyectos = proyectoDAO.listarProyectos();
            todosLosProyectos.setAll(proyectos);
            tablaProyectos.setItems(todosLosProyectos);
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al cargar proyectos", e);
            lblError.setText("No se pudieron cargar los proyectos disponibles.");
            lblError.setVisible(true);
        }
    }

    @FXML
    private void manejarBuscar() {
        String termino = txtBuscar.getText().trim().toLowerCase();
        if (termino.isEmpty()) {
            tablaProyectos.setItems(todosLosProyectos);
            return;
        }
        ObservableList<ProyectoDTO> filtrados = FXCollections.observableArrayList();
        for (ProyectoDTO proyecto : todosLosProyectos) {
            boolean coincideNombre = proyecto.getNombre().toLowerCase().contains(termino);
            boolean coincideDescripcion = proyecto.getDescripcion().toLowerCase().contains(termino);
            if (coincideNombre || coincideDescripcion) {
                filtrados.add(proyecto);
            }
        }
        tablaProyectos.setItems(filtrados);
    }

    @FXML
    private void manejarLimpiar() {
        txtBuscar.clear();
        tablaProyectos.setItems(todosLosProyectos);
    }

    @FXML
    private void manejarEnviarSolicitud() {
        lblError.setVisible(false);

        String mensajeValidacion = validarCampos();
        if (mensajeValidacion != null) {
            lblError.setText(mensajeValidacion);
            lblError.setVisible(true);
            return;
        }

        try {
            SolicitaProyectoDAO solicitaProyectoDAO = new SolicitaProyectoDAO();
            String matricula = ""; // obtener del SesionUsuario - Cambiale Jared

            for (ProyectoDTO proyecto : seleccionados) {
                SolicitaProyectoDTO solicitud = new SolicitaProyectoDTO(
                        matricula,
                        proyecto.getIdProyecto(),
                        TipoEstadoSolicitud.PENDIENTE,
                        txtPeriodo.getText().trim()
                );
                solicitaProyectoDAO.insertarSolicitudProyecto(solicitud);
            }

            cerrarVentana();

        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al enviar solicitudes de proyecto", e);
            lblError.setText("No se pudieron enviar las solicitudes. Intente más tarde.");
            lblError.setVisible(true);
        }
    }

    public void agregarSeleccion(ProyectoDTO proyecto) {
        boolean yaSeleccionado = false;
        for (ProyectoDTO seleccionado : seleccionados) {
            if (seleccionado.getIdProyecto() == proyecto.getIdProyecto()) {
                yaSeleccionado = true;
                break;
            }
        }
        if (!yaSeleccionado && seleccionados.size() < MAXIMO_SELECCIONES) {
            seleccionados.add(proyecto);
            actualizarLabelsSeleccion();
        }
    }

    @FXML
    private void manejarCancelar() {
        cerrarVentana();
    }

    private String validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (seleccionados.isEmpty()) {
            errores.append("Debe seleccionar al menos un proyecto.\n");
        }
        if (txtPeriodo.getText().trim().isEmpty()) {
            errores.append("El periodo no puede estar vacío.\n");
        }

        boolean hayErrores = errores.length() > 0;
        return hayErrores ? errores.toString() : null;
    }

    private void actualizarLabelsSeleccion() {
        lblPrioridad1.setText(seleccionados.size() > 0 ? seleccionados.get(0).getNombre() : "Sin selección");
        lblPrioridad2.setText(seleccionados.size() > 1 ? seleccionados.get(1).getNombre() : "Sin selección");
        lblPrioridad3.setText(seleccionados.size() > 2 ? seleccionados.get(2).getNombre() : "Sin selección");
    }

    private void cerrarVentana() {
        ((Stage) btnEnviar.getScene().getWindow()).close();
    }
}