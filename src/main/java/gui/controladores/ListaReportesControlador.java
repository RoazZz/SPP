package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.Regresable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import logica.dao.ProfesorDAO;
import logica.dao.ReporteDAO;
import logica.dto.ProfesorDTO;
import logica.dto.ReporteDTO;
import logica.utilidades.RegistradorBitacora;
import logica.utilidades.SesionUsuarioSingleton;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListaReportesControlador implements Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(ListaReportesControlador.class.getName());

    @FXML private ComboBox<String> cbFiltroTipo;
    @FXML private TableView<ReporteDTO> tablaReportes;
    @FXML private TableColumn<ReporteDTO, String> colMes;
    @FXML private TableColumn<ReporteDTO, Void> colAcciones;
    @FXML private Label lblContador;

    private ObservableList<ReporteDTO> listaCompleta;
    private FilteredList<ReporteDTO> listaFiltrada;

    private Scene escenaAnterior;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarColumnaAcciones();
        configurarFiltroPorTipo();
        cargarReportes();
    }

    private void configurarColumnas() {
        colMes.setCellValueFactory(celdaTabla -> {
            String mesExtraido = celdaTabla.getValue().getMes();
            String valorCelda = "—";
            if (mesExtraido != null) {
                valorCelda = mesExtraido;
            }
            return new SimpleStringProperty(valorCelda);
        });
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(columnaRecibida -> new CeldaBotonesReporte(
                reporteClicVer -> abrirArchivo(reporteClicVer),
                reporteClicEvaluar -> manejarEvaluar(reporteClicEvaluar)
        ));
    }

    private static class CeldaBotonesReporte extends TableCell<ReporteDTO, Void> {
        private final HBox contenedorBotones;
        private final Button btnVerArchivo;
        private final Button btnEvaluarReporte;

        public CeldaBotonesReporte(Consumer<ReporteDTO> accionVer, Consumer<ReporteDTO> accionEvaluar) {
            this.btnVerArchivo = new Button("VER");
            this.btnEvaluarReporte = new Button("EVALUAR");
            this.btnVerArchivo.getStyleClass().add("btn-cancelar");
            this.btnEvaluarReporte.getStyleClass().add("btn-guardar");
            this.contenedorBotones = new HBox(10, btnVerArchivo, btnEvaluarReporte);

            this.btnVerArchivo.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent eventoClic) {
                    ReporteDTO reporteBuscado = getTableView().getItems().get(getIndex());
                    accionVer.accept(reporteBuscado);
                }
            });

            this.btnEvaluarReporte.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent eventoClic) {
                    ReporteDTO reporteAValidar = getTableView().getItems().get(getIndex());
                    accionEvaluar.accept(reporteAValidar);
                }
            });
        }

        @Override
        protected void updateItem(Void elementoVacio, boolean estaVacio) {
            super.updateItem(elementoVacio, estaVacio);
            if (estaVacio) {
                setGraphic(null);
            } else {
                ReporteDTO reporteFila = getTableView().getItems().get(getIndex());
                btnEvaluarReporte.setDisable(reporteFila.getEstado().name().equals("CALIFICADO"));
                setGraphic(contenedorBotones);
            }
        }
    }

    private void configurarFiltroPorTipo() {
        cbFiltroTipo.setItems(FXCollections.observableArrayList("TODOS", "PARCIAL", "MENSUAL"));
        cbFiltroTipo.setValue("TODOS");
        cbFiltroTipo.valueProperty().addListener((observableValor, valorAnterior, valorNuevo) -> aplicarFiltro(valorNuevo));
    }

    private void cargarReportes() {
        try {
            if (SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual() instanceof ProfesorDTO profesorActivo) {
                ProfesorDAO profesorDAO = new ProfesorDAO();
                ProfesorDTO profesorCompleto = profesorDAO.buscarProfesorPorNumPersonal(profesorActivo.getNumeroDePersonal());
                ReporteDAO reporteDAO = new ReporteDAO();
                List<ReporteDTO> reportesEncontrados = reporteDAO.listarReportesPorSeccion(profesorCompleto.getIdSeccion());
                RegistradorBitacora.registrar("CONSULTA_REPORTES", "Consultó el listado de reportes");
                listaCompleta = FXCollections.observableArrayList(reportesEncontrados);
                listaFiltrada = new FilteredList<>(listaCompleta, reporteElemento -> true);
                tablaReportes.setItems(listaFiltrada);
                actualizarContador();
            }
        } catch (DAOExcepcion | EntidadNoEncontradaExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al cargar reportes", excepcionCapturada);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Imposible acceder. Intente más tarde.");
        }
    }

    @FXML
    private void manejarLimpiarFiltro(ActionEvent eventoClic) {
        cbFiltroTipo.setValue("TODOS");
    }

    private void aplicarFiltro(String tipoSeleccionado) {
        if (listaFiltrada != null) {
            if (tipoSeleccionado == null || tipoSeleccionado.equals("TODOS")) {
                listaFiltrada.setPredicate(new Predicate<ReporteDTO>() {
                    @Override
                    public boolean test(ReporteDTO reporteElemento) {
                        return true;
                    }
                });
            } else {
                listaFiltrada.setPredicate(new Predicate<ReporteDTO>() {
                    @Override
                    public boolean test(ReporteDTO reporteElemento) {
                        return reporteElemento.getTipoReporte().name().equals(tipoSeleccionado);
                    }
                });
            }
            actualizarContador();
        }
    }

    private void abrirArchivo(ReporteDTO reporteObjetivo) {
        try {
            File archivoSistema = new File(reporteObjetivo.getRuta());
            if (!archivoSistema.exists()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se encontró el archivo del reporte.");
            } else {
                Desktop.getDesktop().open(archivoSistema);
            }
        } catch (IOException excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al abrir archivo del reporte", excepcionCapturada);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir el archivo.");
        }
    }

    private void manejarEvaluar(ReporteDTO reporteObjetivo) {
        try {
            FXMLLoader cargadorVista = new FXMLLoader(getClass().getResource("/gui/vista/FXMLEvaluarReporte.fxml"));
            Parent vistaCargada = cargadorVista.load();
            EvaluarReporteControlador controladorEvaluacion = cargadorVista.getController();
            controladorEvaluacion.cargarReporte(reporteObjetivo);

            Stage escenarioEmergente = new Stage();
            escenarioEmergente.setScene(new Scene(vistaCargada));
            escenarioEmergente.showAndWait();

            cargarReportes();
        } catch (IOException excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al abrir vista de evaluación", excepcionCapturada);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la evaluación. Intente más tarde.");
        }
    }

    private void actualizarContador() {
        int totalReportes = 0;
        if (listaFiltrada != null) {
            totalReportes = listaFiltrada.size();
        }
        lblContador.setText(totalReportes + " reportes");
    }

    private void mostrarAlerta(Alert.AlertType tipoAlerta, String tituloAlerta, String mensajeAlerta) {
        Alert ventanaAlerta = new Alert(tipoAlerta);
        ventanaAlerta.setTitle(tituloAlerta);
        ventanaAlerta.setHeaderText(null);
        ventanaAlerta.setContentText(mensajeAlerta);
        ventanaAlerta.showAndWait();
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void manejarRegresar(ActionEvent eventoClic) {
        if (escenaAnterior != null) {
            Stage escenarioActual = (Stage) ((Node) eventoClic.getSource()).getScene().getWindow();
            escenarioActual.setScene(escenaAnterior);
            escenarioActual.show();
        }
    }
}