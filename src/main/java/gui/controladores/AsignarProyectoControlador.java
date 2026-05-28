package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.ReglaDeNegocioExcepcion;
import logica.interfaces.Regresable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
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
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AsignarProyectoControlador implements Initializable, Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(AsignarProyectoControlador.class.getName());

    private final CoordinadorAsignaProyectoDAO coordinadorAsignaProyectoDAO;
    private final SolicitaProyectoDAO solicitaProyectoDAO;

    @FXML private TextField txtPeriodo;
    @FXML private TableView<SolicitaProyectoDTO> tvSolicitudes;
    @FXML private TableColumn<SolicitaProyectoDTO, String> colMatricula;
    @FXML private TableColumn<SolicitaProyectoDTO, Integer> colIdProyecto;
    @FXML private TableColumn<SolicitaProyectoDTO, String> colPeriodo;
    @FXML private TableColumn<SolicitaProyectoDTO, TipoEstadoSolicitud> colEstado;
    @FXML private TableColumn<SolicitaProyectoDTO, Void> colAsignar;
    @FXML private Label lblMensaje;

    private final ObservableList<SolicitaProyectoDTO> listaSolicitudes = FXCollections.observableArrayList();
    private Scene escenaAnterior;

    public AsignarProyectoControlador() throws DAOExcepcion {
        this.coordinadorAsignaProyectoDAO = new CoordinadorAsignaProyectoDAO();
        this.solicitaProyectoDAO = new SolicitaProyectoDAO();
    }

    @Override
    public void initialize(URL urlRecibida, ResourceBundle recursoRecibido) {
        configurarTabla();
        lblMensaje.setVisible(false);
    }

    private void configurarTabla() {
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colIdProyecto.setCellValueFactory(new PropertyValueFactory<>("idProyecto"));
        colPeriodo.setCellValueFactory(new PropertyValueFactory<>("periodo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("tipoEstadoSolicitud"));
        colAsignar.setCellFactory(new Callback<TableColumn<SolicitaProyectoDTO, Void>, TableCell<SolicitaProyectoDTO, Void>>() {
            @Override
            public TableCell<SolicitaProyectoDTO, Void> call(TableColumn<SolicitaProyectoDTO, Void> parametroColumna) {
                return new CeldaBotonAsignar(solicitudClic -> procesarAsignar(solicitudClic));
            }
        });

        tvSolicitudes.setItems(listaSolicitudes);
    }

    private static class CeldaBotonAsignar extends TableCell<SolicitaProyectoDTO, Void> {
        private final Button btnAsignar;
        private final Consumer<SolicitaProyectoDTO> accionAsignar;

        public CeldaBotonAsignar(Consumer<SolicitaProyectoDTO> accionAsignar) {
            this.accionAsignar = accionAsignar;
            this.btnAsignar = new Button("Asignar");
            this.btnAsignar.getStyleClass().add("btn-guardar");
            this.btnAsignar.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent eventoClic) {
                    SolicitaProyectoDTO solicitudObtenida = getTableView().getItems().get(getIndex());
                    CeldaBotonAsignar.this.accionAsignar.accept(solicitudObtenida);
                }
            });
        }

        @Override
        protected void updateItem(Void elementoVacio, boolean estaVacio) {
            super.updateItem(elementoVacio, estaVacio);
            if (estaVacio) {
                setGraphic(null);
            } else {
                setGraphic(btnAsignar);
            }
        }
    }

    @FXML
    private void manejarBuscar(ActionEvent eventoClic) {
        lblMensaje.setVisible(false);
        String periodoIngresado = txtPeriodo.getText().trim();

        if (periodoIngresado.isEmpty()) {
            mostrarMensaje("Debes ingresar un periodo para buscar.", false);
        } else {
            try {
                List<SolicitaProyectoDTO> solicitudesEncontradas = obtenerSolicitudesPendientes(periodoIngresado);
                listaSolicitudes.setAll(solicitudesEncontradas);

                if (solicitudesEncontradas.isEmpty()) {
                    mostrarMensaje("No hay solicitudes pendientes para el periodo indicado.", false);
                }
            } catch (DAOExcepcion excepcionCapturada) {
                REGISTRADOR.log(Level.SEVERE, "Error al buscar solicitudes", excepcionCapturada);
                mostrarMensaje("Error al buscar solicitudes. Intente de nuevo.", false);
            }
        }
    }

    public List<SolicitaProyectoDTO> obtenerSolicitudesPendientes(String periodoBuscado) throws DAOExcepcion {
        return solicitaProyectoDAO.obtenerTodasLasSolicitudesProyecto()
                .stream()
                .filter(solicitud -> solicitud.getTipoEstadoSolicitud() == TipoEstadoSolicitud.PENDIENTE)
                .filter(solicitud -> solicitud.getPeriodo().toLowerCase().contains(periodoBuscado.toLowerCase()))
                .collect(Collectors.toList());
    }

    @FXML
    private void manejarLimpiar(ActionEvent eventoClic) {
        txtPeriodo.clear();
        listaSolicitudes.clear();
        lblMensaje.setVisible(false);
    }

    private void procesarAsignar(SolicitaProyectoDTO solicitudSeleccionada) {
        try {
            if (SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual() instanceof CoordinadorDTO coordinadorActivo) {
                String numeroPersonal = coordinadorActivo.getNumeroPersonal();
                validarAsignacion(solicitudSeleccionada, numeroPersonal);

                solicitudSeleccionada.setTipoEstadoSolicitud(TipoEstadoSolicitud.ACEPTADO);
                solicitaProyectoDAO.actualizarSolicitudProyecto(solicitudSeleccionada);

                CoordinadorAsignaProyectoDTO nuevaAsignacion = new CoordinadorAsignaProyectoDTO(
                        numeroPersonal,
                        solicitudSeleccionada.getIdProyecto(),
                        EstadoAsignacionProyecto.EN_REVISION
                );
                coordinadorAsignaProyectoDAO.insertarAsignacionDeProyecto(nuevaAsignacion);

                listaSolicitudes.remove(solicitudSeleccionada);
                mostrarMensaje("Proyecto asignado correctamente.", true);
            } else {
                mostrarMensaje("Solo un coordinador puede asignar proyectos.", false);
            }
        } catch (ReglaDeNegocioExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.WARNING, "Regla de negocio violada", excepcionCapturada);
            mostrarMensaje(excepcionCapturada.getMessage(), false);
        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error de BD al asignar", excepcionCapturada);
            mostrarMensaje("Error al asignar el proyecto. Intente de nuevo.", false);
        }
    }

    private void validarAsignacion(SolicitaProyectoDTO solicitudParaValidar, String identificadorCoordinador) throws ReglaDeNegocioExcepcion {
        if (solicitudParaValidar == null) {
            throw new ReglaDeNegocioExcepcion("Debe seleccionar una solicitud.");
        }
        if (solicitudParaValidar.getTipoEstadoSolicitud() != TipoEstadoSolicitud.PENDIENTE) {
            throw new ReglaDeNegocioExcepcion("Solo se pueden asignar solicitudes en estado Pendiente.");
        }
        if (identificadorCoordinador == null || identificadorCoordinador.trim().isEmpty()) {
            throw new ReglaDeNegocioExcepcion("El número de personal del coordinador no puede estar vacío.");
        }
    }

    private void mostrarMensaje(String mensajeParaUsuario, boolean esExitoso) {
        lblMensaje.setText(mensajeParaUsuario);
        lblMensaje.getStyleClass().removeAll("mensaje-exito", "mensaje-error");

        if (esExitoso) {
            lblMensaje.getStyleClass().add("mensaje-exito");
        } else {
            lblMensaje.getStyleClass().add("mensaje-error");
        }

        lblMensaje.setVisible(true);
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void manejarSalir(ActionEvent eventoClic) {
        Node nodoAtras = (Node) eventoClic.getSource();
        NavegacionControlador.regresar(nodoAtras, this.escenaAnterior);
    }
}