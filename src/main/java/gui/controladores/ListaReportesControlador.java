package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.interfaces.Regresable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import logica.utilidades.SesionUsuarioSingleton;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListaReportesControlador implements Regresable {

    @FXML private ComboBox<String> cbFiltroTipo;
    @FXML private TableView<ReporteDTO> tablaReportes;
    @FXML private TableColumn<ReporteDTO, String> colIdReporte;
    @FXML private TableColumn<ReporteDTO, String> colIdUsuario;
    @FXML private TableColumn<ReporteDTO, String> colTipoReporte;
    @FXML private TableColumn<ReporteDTO, String> colFecha;
    @FXML private TableColumn<ReporteDTO, String> colEstado;
    @FXML private TableColumn<ReporteDTO, String> colMes;
    @FXML private TableColumn<ReporteDTO, Void> colAcciones;
    @FXML private Label lblContador;
    @FXML private Button btnLimpiarFiltro;
    @FXML private Button btnCerrar;

    private ObservableList<ReporteDTO> listaCompleta;
    private FilteredList<ReporteDTO> listaFiltrada;

    private Scene escenaAnterior;

    private static final Logger LOGGER = Logger.getLogger(ListaReportesControlador.class.getName());

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarColumnaAcciones();
        configurarFiltroPorTipo();
        cargarReportes();
        btnLimpiarFiltro.setOnAction(e -> cbFiltroTipo.setValue("TODOS"));
        btnCerrar.setOnAction(e -> regresar());
    }

    private void configurarColumnas() {
        colIdReporte.setCellValueFactory(
                celda -> new SimpleStringProperty(String.valueOf(celda.getValue().getIdReporte()))
        );
        colIdUsuario.setCellValueFactory(
                celda -> new SimpleStringProperty(String.valueOf(celda.getValue().getIdUsuario()))
        );
        colTipoReporte.setCellValueFactory(
                celda -> new SimpleStringProperty(celda.getValue().getTipoReporte().name())
        );
        colFecha.setCellValueFactory(
                celda -> new SimpleStringProperty(celda.getValue().getFecha().toString())
        );
        colEstado.setCellValueFactory(
                celda -> new SimpleStringProperty(celda.getValue().getEstado().name())
        );
        colMes.setCellValueFactory(celda -> {
            String mes = celda.getValue().getMes();
            return new SimpleStringProperty(mes != null ? mes : "—");
        });
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(columna -> new TableCell<>() {
            private final Button btnVerReporte = new Button("VER");
            private final Button btnEvaluar = new Button("EVALUAR");
            private final HBox contenedor = new HBox(10, btnVerReporte, btnEvaluar);

            {
                btnVerReporte.getStyleClass().add("btn-cancelar");
                btnEvaluar.getStyleClass().add("btn-guardar");

                btnVerReporte.setOnAction(evento -> {
                    ReporteDTO reporte = getTableView().getItems().get(getIndex());
                    abrirArchivo(reporte);
                });

                btnEvaluar.setOnAction(evento -> {
                    ReporteDTO reporte = getTableView().getItems().get(getIndex());
                    manejarEvaluar(reporte);
                });
            }

            @Override
            protected void updateItem(Void elemento, boolean empty) {
                super.updateItem(elemento, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    ReporteDTO reporte = getTableView().getItems().get(getIndex());
                    btnEvaluar.setDisable(reporte.getEstado().name().equals("CALIFICADO"));
                    setGraphic(contenedor);
                }
            }
        });
    }

    private void configurarFiltroPorTipo() {
        cbFiltroTipo.setItems(FXCollections.observableArrayList("TODOS", "PARCIAL", "MENSUAL"));
        cbFiltroTipo.setValue("TODOS");
        cbFiltroTipo.valueProperty().addListener(
                (observable, valorAnterior, valorNuevo) -> aplicarFiltro(valorNuevo)
        );
    }

    private void cargarReportes() {
        try {
            ProfesorDTO profesor = (ProfesorDTO) SesionUsuarioSingleton.obtenerInstancia()
                    .obtenerUsuarioActual();

            ProfesorDAO profesorDAO = new ProfesorDAO();
            ProfesorDTO profesorCompleto = profesorDAO.buscarProfesorPorNumPersonal(
                    profesor.getNumeroDePersonal()
            );

            ReporteDAO reporteDAO = new ReporteDAO();
            List<ReporteDTO> reportes = reporteDAO.listarReportesPorSeccion(
                    profesorCompleto.getIdSeccion()
            );

            listaCompleta = FXCollections.observableArrayList(reportes);
            listaFiltrada = new FilteredList<>(listaCompleta, r -> true);
            tablaReportes.setItems(listaFiltrada);
            actualizarContador();
        } catch (DAOExcepcion | EntidadNoEncontradaExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al cargar reportes", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Imposible acceder. Intente más tarde.");
        }
    }

    private void aplicarFiltro(String tipo) {
        if (listaFiltrada == null) {
            return;
        }

        if (tipo == null || tipo.equals("TODOS")) {
            listaFiltrada.setPredicate(reporte -> true);
        } else {
            listaFiltrada.setPredicate(reporte -> reporte.getTipoReporte().name().equals(tipo));
        }

        actualizarContador();
    }

    private void abrirArchivo(ReporteDTO reporte) {
        try {
            File archivo = new File(reporte.getRuta());

            if (!archivo.exists()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se encontró el archivo del reporte.");
                return;
            }

            Desktop.getDesktop().open(archivo);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al abrir archivo del reporte", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir el archivo.");
        }
    }

    private void manejarEvaluar(ReporteDTO reporte) {
        try {
            FXMLLoader cargador = new FXMLLoader(
                    getClass().getResource("/gui/vista/FXMLEvaluarReporte.fxml")
            );
            Parent vista = cargador.load();

            EvaluarReporteControlador controlador = cargador.getController();
            controlador.cargarReporte(reporte);

            Stage escenario = new Stage();
            escenario.setScene(new Scene(vista));
            escenario.initOwner(btnCerrar.getScene().getWindow());
            escenario.showAndWait();

            cargarReportes();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al abrir vista de evaluación", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo abrir la evaluación. Intente más tarde.");
        }
    }

    private void actualizarContador() {
        int total = listaFiltrada != null ? listaFiltrada.size() : 0;
        lblContador.setText(total + " reportes");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    @Override
    public void setEscenaAnterior(Scene escena) {
        this.escenaAnterior = escena;
    }

    private void regresar() {
        if (escenaAnterior != null) {
            Stage escenario = (Stage) btnCerrar.getScene().getWindow();
            escenario.setScene(escenaAnterior);
            escenario.show();
        }
    }
}