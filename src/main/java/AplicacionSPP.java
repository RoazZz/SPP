import excepciones.DAOExcepcion;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logica.dao.AdministradorDAO;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AplicacionSPP extends Application {
    private static final String RUTA_VISTA_INICIO_SESION = "/gui/vista/FXMLInicioSesion.fxml";
    private static final String RUTA_VISTA_BIENVENIDA = "/gui/vista/FXMLBienvenida.fxml";
    private static final String TITULO_APLICACION = "Sistema de Practicas Profesionales";
    private static final String RUTA_VISTA_ERROR_CONEXION = "/gui/vista/FXMLErrorConexion.fxml";
    private static final Logger REGISTRADOR = Logger.getLogger(AplicacionSPP.class.getName());

    private String determinarVistaInicial() {
        try {
            AdministradorDAO administradorDAO = new AdministradorDAO();
            if (administradorDAO.existeAlgunAdministrador()) {
                return RUTA_VISTA_INICIO_SESION;
            }
            return RUTA_VISTA_BIENVENIDA;
        } catch (DAOExcepcion daoExcepcion) {
            REGISTRADOR.log(Level.SEVERE, "No se pudo conectar con la base de datos en el inicio.", daoExcepcion);
            return RUTA_VISTA_ERROR_CONEXION;
        }
    }


    @Override
    public void start(Stage stage) throws IOException {

        Parent raiz = FXMLLoader.load(getClass().getResource(determinarVistaInicial()));
        Scene escena = new Scene(raiz);
        stage.setTitle(TITULO_APLICACION);
        stage.setScene(escena);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
