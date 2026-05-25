import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AplicacionSPP extends Application {
    private static final String RUTA_VISTA_INICIO_SESION = "/gui/vista/FXMLInicioSesion.fxml";
    private static final String TITULO_APLICACION = "Sistema Gestor de Practicas Profesionales";

    @Override
    public void start(Stage stage) throws IOException {

        Parent raiz = FXMLLoader.load(getClass().getResource(RUTA_VISTA_INICIO_SESION));
        Scene escena = new Scene(raiz);
        stage.setTitle(TITULO_APLICACION);
        stage.setScene(escena);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
