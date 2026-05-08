package gui.controladores;

import excepciones.AutenticacionDeUsuarioExcepcion;
import interfaces.Regresable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logica.enums.TipoDeUsuario;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NavegacionControlador {

    private static final Logger logger = Logger.getLogger(NavegacionControlador.class.getName());

    public void navegarSegunRol(TipoDeUsuario tipoDeUsuario, Stage stage) throws AutenticacionDeUsuarioExcepcion {
        String ruta = obtenerRutaSegunRol(tipoDeUsuario);
        cargarPantalla(ruta, stage);
    }

    public static void abrirVentana(String rutaFXML, Node nodoActual) {
        try {
            FXMLLoader cargador = new FXMLLoader(NavegacionControlador.class.getResource(rutaFXML));
            Parent vista = cargador.load();
            Stage escenario = (Stage) nodoActual.getScene().getWindow();
            Scene escenaAnterior = escenario.getScene();

            Object controlador = cargador.getController();
            if (controlador instanceof Regresable regresable) {
                regresable.setEscenaAnterior(escenaAnterior);
            }

            escenario.setScene(new Scene(vista));
            escenario.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al abrir ventana: " + rutaFXML, e);
        }
    }

    private String obtenerRutaSegunRol(TipoDeUsuario tipoDeUsuario) throws AutenticacionDeUsuarioExcepcion {
        return switch (tipoDeUsuario) {
            case PRACTICANTE -> "/gui/vista/FXMLPrincipalPracticante.fxml";
            case PROFESOR    -> "/gui/vista/FXMLPrincipalProfesor.fxml";
            case COORDINADOR -> "/gui/vista/FXMLPrincipalCoordinador.fxml";
            case ADMIN -> "/gui/vista/FXMLPrincipalAdministrador.fxml";
            default -> throw new AutenticacionDeUsuarioExcepcion("Tipo de usuario no reconocido");
        };
    }

    private void cargarPantalla(String ruta, Stage stage) throws AutenticacionDeUsuarioExcepcion {
        try {
            if (getClass().getResource(ruta) == null) {
                logger.log(Level.SEVERE, "Pantalla no encontrada");
                throw new AutenticacionDeUsuarioExcepcion("No se encontró la pantalla: " + ruta);
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al cargar la pantalla", e);
            throw new AutenticacionDeUsuarioExcepcion("Error al cargar la pantalla");
        }
    }
}