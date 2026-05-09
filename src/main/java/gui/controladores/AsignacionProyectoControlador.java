package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.ReglaDeNegocioExcepcion;
import interfaces.Regresable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import logica.dao.CoordinadorAsignaProyectoDAO;
import logica.dao.SolicitaProyectoDAO;
import logica.dto.CoordinadorAsignaProyectoDTO;
import logica.dto.CoordinadorDTO;
import logica.dto.SolicitaProyectoDTO;
import logica.enums.EstadoAsignacionProyecto;
import logica.enums.TipoEstadoSolicitud;
import logica.utilidades.SesionUsuarioSingleton;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsignacionProyectoControlador implements Initializable, Regresable {

    private final CoordinadorAsignaProyectoDAO coordinadorAsignaProyectoDAO;
    private final SolicitaProyectoDAO solicitaProyectoDAO;
    private static final Logger LOGGER = Logger.getLogger(AsignacionProyectoControlador.class.getName());

    @FXML private TableView<SolicitaProyectoDTO> tvSolicitudes;
    @FXML private TableColumn<SolicitaProyectoDTO, String> colMatricula;
    @FXML private TableColumn<SolicitaProyectoDTO, Integer> colIdProyecto;
    @FXML private TableColumn<SolicitaProyectoDTO, String> colPeriodo;
    @FXML private Button btnAsignar;
    @FXML private Button btnRegresar;
    @FXML private Label lblError;

    private Scene escenaAnterior;
    private final ObservableList<SolicitaProyectoDTO> olSolicitudes = FXCollections.observableArrayList();

    public AsignacionProyectoControlador() throws DAOExcepcion {
        this.coordinadorAsignaProyectoDAO = new CoordinadorAsignaProyectoDAO();
        this.solicitaProyectoDAO = new SolicitaProyectoDAO();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        cargarSolicitudes();

        btnRegresar.setOnAction(evento -> regresar());
        btnAsignar.setOnAction(evento -> manejarAsignacion());
        lblError.setVisible(false);
    }

    private void configurarTabla() {
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colIdProyecto.setCellValueFactory(new PropertyValueFactory<>("idProyecto"));
        colPeriodo.setCellValueFactory(new PropertyValueFactory<>("periodo"));
        tvSolicitudes.setItems(olSolicitudes);
    }

    private void cargarSolicitudes() {
        try {
            List<SolicitaProyectoDTO> pendientes = obtenerSolicitudesPendientes("ACTUAL");
            olSolicitudes.setAll(pendientes);
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al cargar solicitudes", e);
            mostrarError("No se pudieron cargar las solicitudes pendientes.");
        }
    }

    private void manejarAsignacion() {
        lblError.setVisible(false);
        SolicitaProyectoDTO seleccion = tvSolicitudes.getSelectionModel().getSelectedItem();

        try {
            CoordinadorDTO coordinador = (CoordinadorDTO) SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual();
            String numPersonal = coordinador.getNumeroPersonal();

            procesarAsignacionProyecto(seleccion, numPersonal);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Proyecto asignado correctamente.");
            cargarSolicitudes();
        } catch (ReglaDeNegocioExcepcion e) {
            mostrarError(e.getMessage());
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error en BD", e);
            mostrarError("Error al procesar la asignación en la base de datos.");
        }
    }

    public List<SolicitaProyectoDTO> obtenerSolicitudesPendientes(String periodo) throws DAOExcepcion {
        List<SolicitaProyectoDTO> todasLasSolicitudes = solicitaProyectoDAO.obtenerTodasLasSolicitudesProyecto();
        return todasLasSolicitudes.stream()
                .filter(s -> s.getEstadoProyecto() == TipoEstadoSolicitud.PENDIENTE)
                .toList();
    }

    public void procesarAsignacionProyecto(SolicitaProyectoDTO solicitudDTO, String numeroDePersonalCoordinador)
            throws DAOExcepcion, ReglaDeNegocioExcepcion {
        validarAsignacion(solicitudDTO, numeroDePersonalCoordinador);

        solicitudDTO.setEstadoProyecto(TipoEstadoSolicitud.ACEPTADO);
        solicitaProyectoDAO.actualizarSolicitudProyecto(solicitudDTO);
        LOGGER.log(Level.INFO, "Solicitud aprobada para matricula: ", solicitudDTO.getMatricula());

        CoordinadorAsignaProyectoDTO asignacionDTO = new CoordinadorAsignaProyectoDTO(
                numeroDePersonalCoordinador,
                solicitudDTO.getIdProyecto(),
                EstadoAsignacionProyecto.EN_REVISION
        );
        coordinadorAsignaProyectoDAO.insertarAsignacionDeProyecto(asignacionDTO);
    }

    private void validarAsignacion(SolicitaProyectoDTO solicitudDTO, String numeroDePersonalCoordinador)
            throws ReglaDeNegocioExcepcion {
        if (solicitudDTO == null) {
            throw new ReglaDeNegocioExcepcion("Debe seleccionar una solicitud.");
        }
        if (solicitudDTO.getEstadoProyecto() != TipoEstadoSolicitud.PENDIENTE) {
            throw new ReglaDeNegocioExcepcion("Solo se pueden asignar solicitudes en estado Pendiente.");
        }
        if (numeroDePersonalCoordinador == null || numeroDePersonalCoordinador.trim().isEmpty()) {
            throw new ReglaDeNegocioExcepcion("El número de personal del coordinador no puede estar vacío.");
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @Override
    public void setEscenaAnterior(Scene escena) {
        this.escenaAnterior = escena;
    }

    private void regresar() {
        if (escenaAnterior != null) {
            Stage escenario = (Stage) btnRegresar.getScene().getWindow();
            escenario.setScene(escenaAnterior);
        }
    }
}