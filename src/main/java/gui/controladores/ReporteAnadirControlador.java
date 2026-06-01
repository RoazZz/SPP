package gui.controladores;

import logica.interfaces.Regresable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import logica.dao.ReporteDAO;
import logica.dto.ReporteDTO;
import logica.dto.PracticanteDTO;
import logica.enums.TipoReporte;
import logica.enums.EstadoReporte;
import excepciones.DAOExcepcion;
import logica.utilidades.CifradorArchivo;
import logica.utilidades.SesionUsuarioSingleton;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReporteAnadirControlador implements Initializable, Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(ReporteAnadirControlador.class.getName());

    @FXML private ComboBox<TipoReporte> cbTipoReporte;
    @FXML private Label lblNombreArchivo;
    @FXML private Button btnVistaPrevia;
    @FXML private HBox hboxValidacionArchivo;
    @FXML private Button btnGuardar;
    @FXML private ComboBox<String> cbMes;

    private Scene escenaAnterior;
    private File archivoPdf = null;

    @Override
    public void initialize(URL urlRecibida, ResourceBundle recursoRecibido) {
        cbTipoReporte.getItems().setAll(TipoReporte.values());

        cbMes.getItems().setAll(
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        );

        cbTipoReporte.valueProperty().addListener(new ChangeListener<TipoReporte>() {
            @Override
            public void changed(ObservableValue<? extends TipoReporte> observable, TipoReporte viejoValor, TipoReporte nuevoValor) {
                boolean esMensual = false;
                if (nuevoValor == TipoReporte.MENSUAL) {
                    esMensual = true;
                }
                cbMes.setVisible(esMensual);
                cbMes.setManaged(esMensual);
                if (!esMensual) {
                    cbMes.setValue(null);
                }
                actualizarEstadoBotonGuardar();
            }
        });

        cbMes.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String viejoValor, String nuevoValor) {
                actualizarEstadoBotonGuardar();
            }
        });
    }

    @FXML
    private void manejarSeleccionarArchivo(ActionEvent eventoClic) {
        FileChooser selectorArchivoPDF = new FileChooser();
        selectorArchivoPDF.setTitle("Seleccionar Reporte PDF Firmado");
        selectorArchivoPDF.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documentos PDF", "*.pdf"));

        Stage escenarioActual = (Stage) ((Node) eventoClic.getSource()).getScene().getWindow();
        File archivoLocal = selectorArchivoPDF.showOpenDialog(escenarioActual);

        if (archivoLocal != null) {
            archivoPdf = archivoLocal;
            lblNombreArchivo.setText(archivoLocal.getName());

            hboxValidacionArchivo.setVisible(true);
            hboxValidacionArchivo.setManaged(true);
            btnVistaPrevia.setVisible(true);
            btnVistaPrevia.setManaged(true);

            actualizarEstadoBotonGuardar();
        }
    }

    @FXML
    private void manejarVistaPrevia(ActionEvent eventoClic) {
        if (archivoPdf != null) {
            try {
                Desktop.getDesktop().open(archivoPdf);
            } catch (IOException excepcionCapturada) {
                REGISTRADOR.log(Level.WARNING, "No se pudo abrir la vista previa del PDF", excepcionCapturada);
                mostrarAlerta(Alert.AlertType.WARNING, "Vista previa no disponible", "No se pudo abrir el visor de PDF predeterminado.");
            }
        }
    }

    @FXML
    private void manejarGuardar(ActionEvent eventoClic) {
        if (cbTipoReporte.getValue() == null || archivoPdf == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Faltan datos", "Por favor selecciona el tipo de reporte y el archivo PDF.");
            return;
        }

        Alert confirmacionDeEnvio = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacionDeEnvio.setTitle("Confirmar subida");
        confirmacionDeEnvio.setHeaderText(null);
        confirmacionDeEnvio.setContentText("¿Está seguro de subir el documento firmado?");

        Optional<ButtonType> respuestaUsuario = confirmacionDeEnvio.showAndWait();

        if (respuestaUsuario.isPresent() && respuestaUsuario.get() == ButtonType.OK) {
            try {
                if (esDuplicado()) {
                    return;
                }

                String nombreCarpetaTipo = cbTipoReporte.getValue().name();
                String carpetaPracticante = construirNombreCarpetaPracticante();
                Path carpetaDestinoFinal = Paths.get(System.getProperty("user.dir"),
                        "Reportes", nombreCarpetaTipo, "ANADIDOS", carpetaPracticante);

                if (!Files.exists(carpetaDestinoFinal)) {
                    Files.createDirectories(carpetaDestinoFinal);
                }

                Path archivoCopiaSistema = carpetaDestinoFinal.resolve(archivoPdf.getName());

                if (Files.exists(archivoCopiaSistema)) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Archivo duplicado",
                            "Ya existe un archivo con el nombre '" + archivoPdf.getName() + "' en tu carpeta de reportes.");
                    return;
                }

                Files.copy(archivoPdf.toPath(), archivoCopiaSistema, StandardCopyOption.REPLACE_EXISTING);

                String mesDeterminado = null;
                if (cbTipoReporte.getValue() == TipoReporte.MENSUAL) {
                    mesDeterminado = cbMes.getValue();
                }

                ReporteDAO gestorBaseDatos = new ReporteDAO();
                int idSesionBase = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getIdUsuario();

                ReporteDTO registroReporte = new ReporteDTO(
                        0,
                        idSesionBase,
                        cbTipoReporte.getValue(),
                        LocalDate.now(),
                        archivoCopiaSistema.toString(),
                        EstadoReporte.ENTREGADO,
                        mesDeterminado,
                        null
                );
                gestorBaseDatos.agregarReporte(registroReporte);

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Archivo firmado cargado y registrado exitosamente.");
                manejarRegresar(eventoClic);

            } catch (IOException excepcionCapturada) {
                REGISTRADOR.log(Level.SEVERE, "Error al copiar el archivo físico del reporte", excepcionCapturada);
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Archivo", "No se pudo guardar el archivo en el servidor local.");
            } catch (DAOExcepcion excepcionCapturada) {
                REGISTRADOR.log(Level.SEVERE, "Error al registrar el reporte en la base de datos", excepcionCapturada);
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Base de Datos", "El archivo se copió, pero no se pudo registrar.");
            }
        }
    }

    private boolean esDuplicado() throws DAOExcepcion, IOException {
        ReporteDAO herramientaReporteDAO = new ReporteDAO();
        int claveUsuarioBD = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getIdUsuario();
        String posibleMes = null;

        if (cbTipoReporte.getValue() == TipoReporte.MENSUAL) {
            posibleMes = cbMes.getValue();
        }

        if (herramientaReporteDAO.existeDuplicado(claveUsuarioBD, cbTipoReporte.getValue(), posibleMes, EstadoReporte.ENTREGADO)) {
            String alertaMes = "Ya subiste tu reporte parcial.";
            if (cbTipoReporte.getValue() == TipoReporte.MENSUAL) {
                alertaMes = "Ya subiste el reporte de " + posibleMes + ".";
            }
            mostrarAlerta(Alert.AlertType.WARNING, "Reporte duplicado", alertaMes);
            return true;
        }

        String reporteAnalizadoEnPDF = CifradorArchivo.extraerTipoReporte(archivoPdf.toPath());
        if (reporteAnalizadoEnPDF != null && !reporteAnalizadoEnPDF.equalsIgnoreCase(cbTipoReporte.getValue().name())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Tipo incorrecto",
                    "El PDF es de tipo " + reporteAnalizadoEnPDF + " pero seleccionaste " + cbTipoReporte.getValue().name() + ".");
            return true;
        }

        if (cbTipoReporte.getValue() == TipoReporte.MENSUAL) {
            String mesExtraidoDePDF = CifradorArchivo.extraerMes(archivoPdf.toPath());
            if (mesExtraidoDePDF != null && !mesExtraidoDePDF.equalsIgnoreCase(cbMes.getValue())) {
                mostrarAlerta(Alert.AlertType.WARNING, "Mes incorrecto",
                        "El PDF corresponde a " + mesExtraidoDePDF + " pero seleccionaste " + cbMes.getValue() + ".");
                return true;
            }
        }

        return false;
    }

    private void actualizarEstadoBotonGuardar() {
        boolean requiereMesYFalta = false;
        if (cbTipoReporte.getValue() == TipoReporte.MENSUAL && cbMes.getValue() == null) {
            requiereMesYFalta = true;
        }

        boolean deshabilitar = false;
        if (archivoPdf == null || cbTipoReporte.getValue() == null || requiereMesYFalta) {
            deshabilitar = true;
        }

        btnGuardar.setDisable(deshabilitar);
    }

    private void mostrarAlerta(Alert.AlertType tipoAlertaExhibida, String tituloParaAlerta, String mensajeParaAlerta) {
        Alert alertaEnPantalla = new Alert(tipoAlertaExhibida);
        alertaEnPantalla.setTitle(tituloParaAlerta);
        alertaEnPantalla.setHeaderText(null);
        alertaEnPantalla.setContentText(mensajeParaAlerta);
        alertaEnPantalla.showAndWait();
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void manejarRegresar(ActionEvent eventoClic) {
        if (escenaAnterior != null) {
            Stage escenarioControlado = (Stage) ((Node) eventoClic.getSource()).getScene().getWindow();
            escenarioControlado.setScene(escenaAnterior);
            escenarioControlado.show();
        }
    }

    private String construirNombreCarpetaPracticante() {
        var usuarioActual = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual();
        String nombreFormateado = usuarioActual.getNombre().trim().replace(" ", "_");
        String identificador;
        if (usuarioActual instanceof PracticanteDTO practicante) {
            identificador = practicante.getMatricula().trim();
        } else {
            identificador = String.valueOf(usuarioActual.getIdUsuario());
        }
        return nombreFormateado + "_" + identificador;
    }
}