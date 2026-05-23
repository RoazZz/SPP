package gui.controladores;

import excepciones.ConsultaIndicadoresExcepcion;
import excepciones.EntidadNoCreadaExcepcion;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import logica.dao.ReporteIndicadoresDAO;
import logica.dto.CoordinadorDTO;
import logica.dto.ReporteIndicadoresDTO;
import logica.dto.UsuarioDTO;
import logica.enums.FiltrosIndicadores;
import logica.utilidades.ExportadorIndicadoresPDF;
import logica.utilidades.SesionUsuarioSingleton;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReporteIndicadoresControlador {

    private static final Logger REGISTRADOR = Logger.getLogger(ReporteIndicadoresControlador.class.getName());

    private static final String CARPETA_RAIZ_REPORTES = "ReporteIndicadores";
    private static final String EXTENSION_PDF = ".pdf";
    private static final String PREFIJO_CARPETA_FILTRO = "Reporte Indicadores ";
    private static final String FORMATO_FECHA_HORA = "_yyyyMMdd_HHmmss";

    @FXML private ComboBox<FiltrosIndicadores> cmbFiltroIndicador;
    @FXML private Button btnGenerarReportePdf;
    @FXML private BarChart<String, Number> graficaIndicadores;
    @FXML private TableView<ReporteIndicadoresDTO> tablaIndicadores;
    @FXML private TableColumn<ReporteIndicadoresDTO, String> columnaCategoria;
    @FXML private TableColumn<ReporteIndicadoresDTO, Number> columnaTotal;

    private ReporteIndicadoresDAO reporteIndicadoresDao;
    private ExportadorIndicadoresPDF exportadorPdf;
    private List<ReporteIndicadoresDTO> datosActuales;

    @FXML
    private void initialize() {
        reporteIndicadoresDao = new ReporteIndicadoresDAO();
        exportadorPdf = new ExportadorIndicadoresPDF();

        configurarComboFiltros();
        configurarColumnasTabla();
        registrarEventos();
    }

    private void configurarComboFiltros() {
        ObservableList<FiltrosIndicadores> filtrosDisponibles = FXCollections.observableArrayList(FiltrosIndicadores.values());
        cmbFiltroIndicador.setItems(filtrosDisponibles);

        cmbFiltroIndicador.setConverter(new StringConverter<FiltrosIndicadores>() {
            @Override
            public String toString(FiltrosIndicadores filtro) {
                if (filtro == null) {
                    return "";
                }
                return obtenerEtiquetaUi(filtro);
            }

            @Override
            public FiltrosIndicadores fromString(String texto) {
                return null;
            }
        });
    }

    private void configurarColumnasTabla() {
        columnaCategoria.setCellValueFactory(celda -> new SimpleStringProperty(celda.getValue().getCategoria()));
        columnaTotal.setCellValueFactory(celda -> new SimpleIntegerProperty(celda.getValue().getTotal()));
    }

    private void registrarEventos() {
        cmbFiltroIndicador.valueProperty().addListener(
                (observable, valorAnterior, valorNuevo) -> {
                    if (valorNuevo != null) {
                        actualizarVisualizacion(valorNuevo);
                    }
                });

        btnGenerarReportePdf.setOnAction(eventoClic -> exportarReporteAPdf());
    }

    private void actualizarVisualizacion(FiltrosIndicadores filtroSeleccionado) {
        try {
            datosActuales = reporteIndicadoresDao.contarPracticantesPor(filtroSeleccionado);
            llenarGrafica(filtroSeleccionado, datosActuales);
            llenarTabla(datosActuales);
            btnGenerarReportePdf.setDisable(datosActuales.isEmpty());

        } catch (ConsultaIndicadoresExcepcion consultaExcepcion) {
            REGISTRADOR.log(Level.WARNING, "Fallo al consultar indicadores", consultaExcepcion);
            mostrarAlerta(AlertType.ERROR, "No se pudieron obtener los datos del indicador seleccionado.");
        }
    }

    private void llenarGrafica(FiltrosIndicadores filtro, List<ReporteIndicadoresDTO> datos) {
        graficaIndicadores.getData().clear();
        graficaIndicadores.setTitle(obtenerTituloGrafica(filtro));

        XYChart.Series<String, Number> serieDatos = new XYChart.Series<>();
        serieDatos.setName(obtenerEtiquetaUi(filtro));

        for (ReporteIndicadoresDTO indicador : datos) {
            serieDatos.getData().add(new XYChart.Data<>(indicador.getCategoria(), indicador.getTotal()));
        }

        graficaIndicadores.getData().add(serieDatos);
    }

    private void llenarTabla(List<ReporteIndicadoresDTO> datos) {
        ObservableList<ReporteIndicadoresDTO> filasTabla = FXCollections.observableArrayList(datos);
        tablaIndicadores.setItems(filasTabla);
    }

    private void exportarReporteAPdf() {
        FiltrosIndicadores filtroSeleccionado = cmbFiltroIndicador.getValue();
        if (filtroSeleccionado == null || datosActuales == null) {
            return;
        }

        UsuarioDTO usuarioEnSesion = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual();

        if (!(usuarioEnSesion instanceof CoordinadorDTO)) {
            mostrarAlerta(AlertType.ERROR, "Solo los coordinadores pueden generar este reporte.");
            return;
        }

        CoordinadorDTO coordinador = (CoordinadorDTO) usuarioEnSesion;
        String numeroPersonal = coordinador.getNumeroPersonal();

        File archivoDestino = construirRutaArchivoPdf(numeroPersonal, filtroSeleccionado);

        if (archivoDestino.exists() && !confirmarReemplazoArchivo(archivoDestino)) {
            return;
        }

        try {
            prepararCarpetaDestino(archivoDestino);
            exportadorPdf.exportar(
                    archivoDestino,
                    obtenerTituloGrafica(filtroSeleccionado),
                    datosActuales,
                    graficaIndicadores);

            mostrarAlerta(AlertType.INFORMATION, "El reporte se exportó correctamente en:\n" + archivoDestino.getAbsolutePath());

        } catch (EntidadNoCreadaExcepcion entidadNoCreadaExcepcion) {
            REGISTRADOR.log(Level.WARNING, "Fallo al exportar el PDF", entidadNoCreadaExcepcion);
            mostrarAlerta(AlertType.ERROR, "No se pudo generar el archivo PDF.");
        }
    }

    private File construirRutaArchivoPdf(String numeroPersonal,
                                         FiltrosIndicadores filtro) {
        String rutaProyecto = System.getProperty("user.dir");
        String etiquetaFiltro = obtenerEtiquetaUi(filtro);
        String nombreCarpetaFiltro = PREFIJO_CARPETA_FILTRO + etiquetaFiltro;

        LocalDateTime momentoActual = LocalDateTime.now();
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern(FORMATO_FECHA_HORA);
        String marcaTiempo = momentoActual.format(formateador);

        String nombreArchivoPdf = PREFIJO_CARPETA_FILTRO + etiquetaFiltro + marcaTiempo + EXTENSION_PDF;

        return new File(rutaProyecto + File.separator
                + CARPETA_RAIZ_REPORTES + File.separator
                + nombreCarpetaFiltro + File.separator
                + numeroPersonal + File.separator
                + nombreArchivoPdf);
    }

    private void prepararCarpetaDestino(File archivoDestino)
            throws EntidadNoCreadaExcepcion {
        File carpetaContenedora = archivoDestino.getParentFile();
        if (!carpetaContenedora.exists() && !carpetaContenedora.mkdirs()) {
            throw new EntidadNoCreadaExcepcion(
                    "No se pudo crear la carpeta de destino: "
                            + carpetaContenedora.getAbsolutePath());
        }
    }

    private boolean confirmarReemplazoArchivo(File archivoExistente) {
        Alert dialogoConfirmacion = new Alert(AlertType.CONFIRMATION);
        dialogoConfirmacion.setTitle("Archivo existente");
        dialogoConfirmacion.setHeaderText("Ya existe un reporte con ese nombre");
        dialogoConfirmacion.setContentText(
                "El archivo \"" + archivoExistente.getName()
                        + "\" ya existe en la carpeta de reportes.\n¿Deseas reemplazarlo?");

        Optional<ButtonType> respuestaUsuario = dialogoConfirmacion.showAndWait();
        return respuestaUsuario.isPresent()
                && respuestaUsuario.get() == ButtonType.OK;
    }

    private void mostrarAlerta(AlertType tipoAlerta, String mensaje) {
        Alert alerta = new Alert(tipoAlerta);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private String obtenerEtiquetaUi(FiltrosIndicadores filtro) {
        switch (filtro) {
            case GENERO:
                return "Género";
            case EDAD:
                return "Edad";
            case SEMESTRE:
                return "Semestre";
            case LENGUA_INDIGENA:
                return "Lengua Indígena";
            default:
                throw new IllegalArgumentException(
                        "Filtro no soportado: " + filtro);
        }
    }

    private String obtenerTituloGrafica(FiltrosIndicadores filtro) {
        switch (filtro) {
            case GENERO:
                return "Practicantes por Género";
            case EDAD:
                return "Practicantes por Edad";
            case SEMESTRE:
                return "Practicantes por Semestre";
            case LENGUA_INDIGENA:
                return "Practicantes con Lengua Indígena";
            default:
                throw new IllegalArgumentException(
                        "Filtro no soportado: " + filtro);
        }
    }
}