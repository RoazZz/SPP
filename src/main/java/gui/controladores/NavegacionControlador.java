package gui.controladores;

import excepciones.AutenticacionDeUsuarioExcepcion;
import logica.interfaces.Regresable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logica.enums.TipoDeUsuario;
import logica.utilidades.RegistradorBitacora;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NavegacionControlador {

    private static final Logger REGISTRADOR = Logger.getLogger(NavegacionControlador.class.getName());

    public void navegarSegunRol(TipoDeUsuario tipoDeUsuario, Stage stage) throws AutenticacionDeUsuarioExcepcion {
        String ruta = obtenerRutaSegunRol(tipoDeUsuario);
        cargarPantalla(ruta, stage);
        RegistradorBitacora.registrar("NAVEGACION", "Ingreso a la pantalla principal: " + ruta);
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
            RegistradorBitacora.registrar("NAVEGACION", "Abrio la ventana: " + rutaFXML);
        } catch (IOException ioException) {
            REGISTRADOR.log(Level.SEVERE, "Error al abrir ventana: " + rutaFXML, ioException);
        }
    }

    private String obtenerRutaSegunRol(
            TipoDeUsuario tipoDeUsuario)
            throws AutenticacionDeUsuarioExcepcion {
        String ruta;
        switch (tipoDeUsuario) {
            case PRACTICANTE:
                ruta = "/gui/vista/FXMLPrincipalPracticante.fxml";
                break;
            case PROFESOR:
                ruta = "/gui/vista/FXMLPrincipalProfesor.fxml";
                break;
            case COORDINADOR:
                ruta = "/gui/vista/FXMLPrincipalCoordinador.fxml";
                break;
            case ADMIN:
                ruta = "/gui/vista/FXMLPrincipalAdministrador.fxml";
                break;
            default:
                throw new AutenticacionDeUsuarioExcepcion("Tipo de usuario no reconocido");
        }
        return ruta;
    }


    private void cargarPantalla(String ruta, Stage stage) throws AutenticacionDeUsuarioExcepcion {
        try {
            if (getClass().getResource(ruta) == null) {
                REGISTRADOR.log(Level.SEVERE, "Pantalla no encontrada");
                throw new AutenticacionDeUsuarioExcepcion("No se encontró la pantalla: " + ruta);
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ioException) {
            REGISTRADOR.log(Level.SEVERE, "Error al cargar la pantalla", ioException);
            throw new AutenticacionDeUsuarioExcepcion("Error al cargar la pantalla");
        }
    }

    public static void regresar(Node nodoActual, Scene escenaAnterior) {
        if (escenaAnterior != null) {
            Stage escenario = (Stage) nodoActual.getScene().getWindow();
            escenario.setScene(escenaAnterior);
        } else {
            REGISTRADOR.log(Level.WARNING, "Se intentó regresar, pero la escena anterior es null.");
        }
    }
}