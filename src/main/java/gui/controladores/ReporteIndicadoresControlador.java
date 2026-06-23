package gui.controladores;

import excepciones.ConsultaIndicadoresExcepcion;
import excepciones.EntidadNoCreadaExcepcion;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import logica.dao.ReporteIndicadoresDAO;
import logica.dto.CoordinadorDTO;
import logica.dto.ReporteIndicadoresDTO;
import logica.dto.UsuarioDTO;
import logica.enums.FiltrosIndicadores;
import logica.interfaces.Regresable;
import logica.utilidades.ExportadorIndicadoresPDF;
import logica.utilidades.RegistradorBitacora;
import logica.utilidades.SesionUsuarioSingleton;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static gui.controladores.NavegacionControlador.regresar;

public class ReporteIndicadoresControlador implements Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(ReporteIndicadoresControlador.class.getName());

    private static final String CARPETA_RAIZ_REPORTES = "ReporteIndicadores";
    private static final String EXTENSION_PDF = ".pdf";
    private static final String PREFIJO_CARPETA_FILTRO = "Reporte Indicadores ";
    private static final String FORMATO_FECHA_HORA = "_yyyyMMdd_HHmmss";

    private Scene escenaAnterior;

    @FXML private ComboBox<FiltrosIndicadores> cbFiltroIndicador;
    @FXML private Button btnGenerarReportePdf;
    @FXML private BarChart<String, Number> bcIndicadores;
    @FXML private TableView<ReporteIndicadoresDTO> tvIndicadores;
    @FXML private TableColumn<ReporteIndicadoresDTO, String> colCategoria;
    @FXML private TableColumn<ReporteIndicadoresDTO, Number> colTotal;
    @FXML private Button btnCancelar;

    private ReporteIndicadoresDAO reporteIndicadoresDao;
    private ExportadorIndicadoresPDF exportadorPdf;
    private List<ReporteIndicadoresDTO> datosActuales;

    @FXML
    private void initialize() {
        reporteIndicadoresDao = new ReporteIndicadoresDAO();
        exportadorPdf = new ExportadorIndicadoresPDF();

        configurarComboFiltros();
        registrarEventos();
    }

    private void configurarComboFiltros() {
        ObservableList<FiltrosIndicadores> filtrosDisponibles = FXCollections.observableArrayList(FiltrosIndicadores.values());
        cbFiltroIndicador.setItems(filtrosDisponibles);

        cbFiltroIndicador.setConverter(new StringConverter<FiltrosIndicadores>() {
            @Override
            public String toString(FiltrosIndicadores filtroRecibido) {
                String etiquetaFiltro = "";
                if (filtroRecibido != null) {
                    etiquetaFiltro = obtenerEtiquetaUi(filtroRecibido);
                }
                return etiquetaFiltro;
            }

            @Override
            public FiltrosIndicadores fromString(String texto) {
                return null;
            }
        });
    }

    private void registrarEventos() {
        cbFiltroIndicador.valueProperty().addListener(new ChangeListener<FiltrosIndicadores>() {
            @Override
            public void changed(ObservableValue<? extends FiltrosIndicadores> observable,
                                FiltrosIndicadores valorAnterior,
                                FiltrosIndicadores valorNuevo) {
                if (valorNuevo != null) {
                    actualizarVisualizacion(valorNuevo);
                }
            }
        });
    }

    @FXML
    private void manejarGenerarReportePdf(ActionEvent eventoClic) {
        exportarReporteAPdf();
    }

    private void actualizarVisualizacion(FiltrosIndicadores filtroSeleccionado) {
        try {
            datosActuales = reporteIndicadoresDao.contarPracticantesPor(filtroSeleccionado);
            RegistradorBitacora.registrar("GENERAR_REPORTE_INDICADORES", "Generó el reporte de indicadores con filtro: " + filtroSeleccionado);
            llenarGrafica(filtroSeleccionado, datosActuales);
            llenarTabla(datosActuales);

            boolean hayDatos = !datosActuales.isEmpty();
            btnGenerarReportePdf.setDisable(!hayDatos);

        } catch (ConsultaIndicadoresExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.WARNING, "Fallo al consultar indicadores", excepcionCapturada);
            mostrarAlerta(AlertType.ERROR, "No se pudieron obtener los datos del indicador seleccionado.");
        }
    }

    private void llenarGrafica(FiltrosIndicadores filtroRecibido, List<ReporteIndicadoresDTO> datosRecibidos) {
        bcIndicadores.getData().clear();
        bcIndicadores.setTitle(obtenerTituloGrafica(filtroRecibido));

        XYChart.Series<String, Number> serieDatos = new XYChart.Series<>();
        serieDatos.setName(obtenerEtiquetaUi(filtroRecibido));

        for (ReporteIndicadoresDTO indicadorActual : datosRecibidos) {
            serieDatos.getData().add(new XYChart.Data<>(
                    indicadorActual.getCategoria(),
                    indicadorActual.getTotal()
            ));
        }

        bcIndicadores.getData().add(serieDatos);
    }

    private void llenarTabla(List<ReporteIndicadoresDTO> datosRecibidos) {
        ObservableList<ReporteIndicadoresDTO> filasTabla = FXCollections.observableArrayList(datosRecibidos);
        tvIndicadores.setItems(filasTabla);
    }

    private void exportarReporteAPdf() {
        FiltrosIndicadores filtroSeleccionado = cbFiltroIndicador.getValue();
        if (filtroSeleccionado == null || datosActuales == null) {
            return;
        }

        UsuarioDTO usuarioEnSesion = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual();

        if (usuarioEnSesion instanceof CoordinadorDTO coordinadorActivo) {
            String numeroPersonal = coordinadorActivo.getNumeroPersonal();
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
                        bcIndicadores);

                mostrarAlerta(AlertType.INFORMATION, "El reporte se exportó correctamente.");

            } catch (EntidadNoCreadaExcepcion excepcionCapturada) {
                REGISTRADOR.log(Level.WARNING, "Fallo al exportar el PDF", excepcionCapturada);
                mostrarAlerta(AlertType.ERROR, "No se pudo generar el archivo PDF.");
            }
        } else {
            mostrarAlerta(AlertType.ERROR, "Solo los coordinadores pueden generar este reporte.");
        }
    }

    private File construirRutaArchivoPdf(String numeroPersonalRecibido, FiltrosIndicadores filtroRecibido) {
        String rutaProyecto = System.getProperty("user.dir");
        String etiquetaFiltro = obtenerEtiquetaUi(filtroRecibido);
        String nombreCarpetaFiltro = PREFIJO_CARPETA_FILTRO + etiquetaFiltro;

        LocalDateTime momentoActual = LocalDateTime.now();
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern(FORMATO_FECHA_HORA);
        String marcaTiempo = momentoActual.format(formateador);

        String nombreArchivoPdf = PREFIJO_CARPETA_FILTRO + etiquetaFiltro + marcaTiempo + EXTENSION_PDF;

        return new File(rutaProyecto + File.separator
                + CARPETA_RAIZ_REPORTES + File.separator
                + nombreCarpetaFiltro + File.separator
                + numeroPersonalRecibido + File.separator
                + nombreArchivoPdf);
    }

    private void prepararCarpetaDestino(File archivoDestino) throws EntidadNoCreadaExcepcion {
        File carpetaContenedora = archivoDestino.getParentFile();
        if (!carpetaContenedora.exists()) {
            if (!carpetaContenedora.mkdirs()) {
                throw new EntidadNoCreadaExcepcion("No se pudo crear la carpeta de destino.");
            }
        }
    }

    private boolean confirmarReemplazoArchivo(File archivoExistente) {
        Alert dialogoConfirmacion = new Alert(AlertType.CONFIRMATION);
        dialogoConfirmacion.setTitle("Archivo existente");
        dialogoConfirmacion.setHeaderText("Ya existe un reporte con ese nombre");
        dialogoConfirmacion.setContentText("¿Deseas reemplazarlo?");

        Optional<ButtonType> respuestaUsuario = dialogoConfirmacion.showAndWait();
        return respuestaUsuario.isPresent() && respuestaUsuario.get() == ButtonType.OK;
    }

    private void mostrarAlerta(AlertType tipoAlerta, String mensajeAlerta) {
        Alert ventanaAlerta = new Alert(tipoAlerta);
        ventanaAlerta.setContentText(mensajeAlerta);
        ventanaAlerta.showAndWait();
    }

    private String obtenerEtiquetaUi(FiltrosIndicadores filtroRecibido) {
        String etiquetaFiltro;
        switch (filtroRecibido) {
            case GENERO:
                etiquetaFiltro = "Género";
                break;
            case EDAD:
                etiquetaFiltro = "Edad";
                break;
            case SEMESTRE:
                etiquetaFiltro = "Semestre";
                break;
            case LENGUA_INDIGENA:
                etiquetaFiltro = "Lengua Indígena";
                break;
            default:
                throw new IllegalArgumentException("Filtro no soportado: " + filtroRecibido);
        }
        return etiquetaFiltro;
    }

    private String obtenerTituloGrafica(FiltrosIndicadores filtroRecibido) {
        String tituloGrafica;
        switch (filtroRecibido) {
            case GENERO:
                tituloGrafica = "Practicantes por Género";
                break;
            case EDAD:
                tituloGrafica = "Practicantes por Edad";
                break;
            case SEMESTRE:
                tituloGrafica = "Practicantes por Semestre";
                break;
            case LENGUA_INDIGENA:
                tituloGrafica = "Practicantes con Lengua Indígena";
                break;
            default:
                throw new IllegalArgumentException("Filtro no soportado: " + filtroRecibido);
        }
        return tituloGrafica;
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void regresar(ActionEvent eventoClic) {
        if (escenaAnterior != null) {
            Stage escenarioActual = (Stage) ((Node) eventoClic.getSource()).getScene().getWindow();
            escenarioActual.setScene(escenaAnterior);
            escenarioActual.show();
        }
    }
}