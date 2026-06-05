package logica.utilidades;

import excepciones.DAOExcepcion;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import logica.dao.SolicitaProyectoDAO;
import logica.dto.SolicitaProyectoDTO;
import logica.enums.TipoEstadoSolicitud;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GestorDocumento {

    private static final Logger REGISTRADOR = Logger.getLogger(GestorDocumento.class.getName());

    private static final String CARPETA_RAIZ = System.getProperty("user.dir");
    private static final String CARPETA_HORARIOS = "Horarios";
    private static final String CARPETA_BITACORAS = "BitacorasPSP";
    private static final String CARPETA_REPORTES = "Reportes";
    private static final String CARPETA_AUTOEVALUACIONES = "Autoevaluaciones";
    private static final String CARPETA_PLANES = "PlanesDeActividades";

    private GestorDocumento() {
    }

    public static boolean practicanteTieneProyectoAceptado(String matricula) throws DAOExcepcion {
        SolicitaProyectoDAO solicitaProyectoDAO = new SolicitaProyectoDAO();
        List<SolicitaProyectoDTO> solicitudes = solicitaProyectoDAO.obtenerSolicitudesProyectoPorMatricula(matricula);

        for (SolicitaProyectoDTO solicitud : solicitudes) {
            if (solicitud.getTipoEstadoSolicitud() == TipoEstadoSolicitud.ACEPTADO) {
                return true;
            }
        }

        return false;
    }

    public static Path construirRutaHorario(String matricula) {
        return Paths.get(CARPETA_RAIZ, CARPETA_HORARIOS, matricula);
    }

    public static Path construirRutaBitacora(String matricula) {
        return Paths.get(CARPETA_RAIZ, CARPETA_BITACORAS, matricula);
    }

    public static Path construirRutaReporte(String tipoReporte) {
        return Paths.get(CARPETA_RAIZ, CARPETA_REPORTES, tipoReporte, "ANADIDOS");
    }

    public static Path construirRutaAutoevaluacion(String matricula) {
        return Paths.get(CARPETA_RAIZ, CARPETA_AUTOEVALUACIONES, matricula);
    }

    public static Path construirRutaPlan(String matricula) {
        return Paths.get(CARPETA_RAIZ, CARPETA_PLANES, matricula);
    }

    public static String construirNombreArchivo(String tipo, String matricula) {
        return tipo + "_" + matricula + ".pdf";
    }

    public static boolean existeDocumentoEnCarpeta(Path carpeta) throws IOException {
        if (!Files.exists(carpeta)) {
            return false;
        }

        try (var flujoArchivos = Files.list(carpeta)) {
            return flujoArchivos.anyMatch(ruta ->
                    Files.isRegularFile(ruta) && ruta.getFileName().toString().toLowerCase().endsWith(".pdf")
            );
        }
    }

    public static boolean confirmarReemplazo() {
        Alert alertaReemplazo = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Ya existe un documento registrado.\nSolo puedes tener un documento subido a la vez.\n\n¿Deseas reemplazarlo?",
                ButtonType.YES, ButtonType.NO
        );
        alertaReemplazo.setTitle("Documento existente");
        alertaReemplazo.setHeaderText(null);

        Optional<ButtonType> respuesta = alertaReemplazo.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.YES;
    }

    public static boolean confirmarSubida() {
        Alert alertaConfirmacion = new Alert(
                Alert.AlertType.CONFIRMATION,
                "¿Está seguro de subir este documento?",
                ButtonType.YES, ButtonType.NO
        );
        alertaConfirmacion.setTitle("Confirmar subida");
        alertaConfirmacion.setHeaderText(null);

        Optional<ButtonType> respuesta = alertaConfirmacion.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.YES;
    }

    public static Path guardarDocumento(Path carpetaDestino, File archivoOrigen) throws IOException {
        if (!Files.exists(carpetaDestino)) {
            Files.createDirectories(carpetaDestino);
        }

        eliminarDocumentosPrevios(carpetaDestino);

        Path rutaDestino = carpetaDestino.resolve(archivoOrigen.getName());
        Files.copy(archivoOrigen.toPath(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

        return rutaDestino;
    }

    private static void eliminarDocumentosPrevios(Path carpeta) throws IOException {
        try (var flujoArchivos = Files.list(carpeta)) {
            List<Path> previos = flujoArchivos
                    .filter(ruta -> Files.isRegularFile(ruta) && ruta.getFileName().toString().toLowerCase().endsWith(".pdf"))
                    .toList();

            for (Path archivoParaEliminar : previos) {
                try {
                    Files.deleteIfExists(archivoParaEliminar);
                } catch (IOException ioExcepcion) {
                    REGISTRADOR.log(Level.SEVERE, "Error al eliminar documento previo: " + archivoParaEliminar, ioExcepcion);
                }
            }
        }
    }
}