package gui.controladores;

import excepciones.DAOExcepcion;
import interfaces.Regresable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import logica.dao.ProyectoDAO;
import logica.dao.SolicitaProyectoDAO;
import logica.dto.PracticanteDTO;
import logica.dto.ProyectoDTO;
import logica.dto.SolicitaProyectoDTO;
import logica.dto.UsuarioDTO;
import logica.enums.TipoEstadoSolicitud;
import logica.utilidades.SesionUsuarioSingleton;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SolicitarProyectoControlador implements Initializable, Regresable {

    private static final Logger LOGGER = Logger.getLogger(SolicitarProyectoControlador.class.getName());
    private static final int MAXIMO_SELECCIONES = 3;

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
    @FXML private Button btnEnviar;
    @FXML private Button btnCancelar;

    private Scene escenaAnterior;
    private final ObservableList<ProyectoDTO> listaProyectosDisponibles = FXCollections.observableArrayList();
    private final List<ProyectoDTO> proyectosSeleccionados = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        cargarProyectosDesdeBD();

        btnCancelar.setOnAction(accion -> regresar());
        btnEnviar.setOnAction(accion -> manejarEnviarSolicitud());
        lblError.setVisible(false);
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colOrganizacion.setCellValueFactory(new PropertyValueFactory<>("idOrganizacion"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        colSeleccionar.setCellFactory(parametro -> new TableCell<>() {
            private final Button btnAñadir = new Button("Añadir");
            {
                btnAñadir.getStyleClass().add("btn-guardar");
                btnAñadir.setOnAction(event -> {
                    ProyectoDTO proyecto = getTableView().getItems().get(getIndex());
                    agregarASeleccionados(proyecto);
                });
            }

            @Override
            protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                setGraphic(vacio ? null : btnAñadir);
            }
        });
    }

    private void cargarProyectosDesdeBD() {
        try {
            ProyectoDAO proyectoDAO = new ProyectoDAO();
            List<ProyectoDTO> proyectos = proyectoDAO.listarProyectos();
            listaProyectosDisponibles.setAll(proyectos);
            tvProyectos.setItems(listaProyectosDisponibles);
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al cargar proyectos", e);
            mostrarError("No se pudo conectar con la base de datos de proyectos.");
        }
    }

    @FXML
    private void manejarEnviarSolicitud() {
        lblError.setVisible(false);

        if (proyectosSeleccionados.isEmpty() || txtPeriodo.getText().trim().isEmpty()) {
            mostrarError("Debes seleccionar proyectos y especificar el periodo.");
            return;
        }

        try {
            UsuarioDTO usuarioActual = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual();
            if (!(usuarioActual instanceof PracticanteDTO)) {
                mostrarError("Solo los practicantes pueden realizar esta acción.");
                return;
            }

            String matricula = ((PracticanteDTO) usuarioActual).getMatricula();
            SolicitaProyectoDAO solicitaDAO = new SolicitaProyectoDAO();

            for (ProyectoDTO proyecto : proyectosSeleccionados) {
                SolicitaProyectoDTO solicitud = new SolicitaProyectoDTO(
                        matricula,
                        proyecto.getIdProyecto(),
                        TipoEstadoSolicitud.PENDIENTE,
                        txtPeriodo.getText().trim()
                );
                solicitaDAO.insertarSolicitudProyecto(solicitud);
            }

            regresar();
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al procesar solicitud", e);
            mostrarError("Hubo un error al guardar tu solicitud. Intenta de nuevo.");
        }
    }

    private void agregarASeleccionados(ProyectoDTO proyecto) {
        boolean yaExiste = proyectosSeleccionados.stream()
                .anyMatch(proyectoDTO -> proyectoDTO.getIdProyecto() == proyecto.getIdProyecto());

        if (!yaExiste && proyectosSeleccionados.size() < MAXIMO_SELECCIONES) {
            proyectosSeleccionados.add(proyecto);
            actualizarInterfazSeleccion();
        }
    }

    private void actualizarInterfazSeleccion() {
        lblPrioridad1.setText(proyectosSeleccionados.size() > 0 ? proyectosSeleccionados.get(0).getNombre() : "Sin selección");
        lblPrioridad2.setText(proyectosSeleccionados.size() > 1 ? proyectosSeleccionados.get(1).getNombre() : "Sin selección");
        lblPrioridad3.setText(proyectosSeleccionados.size() > 2 ? proyectosSeleccionados.get(2).getNombre() : "Sin selección");
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }

    @Override
    public void setEscenaAnterior(Scene escena) {
        this.escenaAnterior = escena;
    }

    private void regresar() {
        if (escenaAnterior != null) {
            Stage escenario = (Stage) btnCancelar.getScene().getWindow();
            escenario.setScene(escenaAnterior);
        }
    }
}