package gui.controladores;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logica.dto.UsuarioDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PruebaListaUsuarioControlador extends PruebaBaseGUI {

    private static final long PRUEBA_PAUSA_VISUAL_MS = 800;

    private static final String PRUEBA_NOMBRE_PROFESOR = "JuanPrueba";
    private static final String PRUEBA_APELLIDO_P_PROFESOR = "PerezPrueba";
    private static final String PRUEBA_APELLIDO_M_PROFESOR = "LopezPrueba";

    private static final String PRUEBA_NOMBRE_PRACTICANTE = "MariaPrueba";
    private static final String PRUEBA_APELLIDO_P_PRACTICANTE = "GarciaPrueba";
    private static final String PRUEBA_APELLIDO_M_PRACTICANTE = "RuizPrueba";

    private static final String PRUEBA_NOMBRE_PROFESOR_INACTIVO = "CarlosPrueba";
    private static final String PRUEBA_APELLIDO_P_PROFESOR_INACTIVO = "SanchezPrueba";
    private static final String PRUEBA_APELLIDO_M_PROFESOR_INACTIVO = "DiazPrueba";

    private PruebaUtilDatos pruebaUtilDatos;
    private ListaUsuariosControlador controlador;

    @Override
    public void start(Stage escenario) throws Exception {
        FXMLLoader cargador = new FXMLLoader(getClass().getResource("/gui/vista/FXMLListaUsuarios.fxml"));
        Parent raiz = cargador.load();
        controlador = cargador.getController();
        escenario.setScene(new Scene(raiz));
        escenario.show();
    }

    @BeforeEach
    void prepararDatosPrueba() throws Exception {
        pruebaUtilDatos = new PruebaUtilDatos();

        pruebaUtilDatos.insertarUsuarioPruebaBase(
                PRUEBA_NOMBRE_PROFESOR, PRUEBA_APELLIDO_P_PROFESOR, PRUEBA_APELLIDO_M_PROFESOR,
                TipoDeUsuario.PROFESOR, TipoEstado.ACTIVO);

        pruebaUtilDatos.insertarUsuarioPruebaBase(
                PRUEBA_NOMBRE_PRACTICANTE, PRUEBA_APELLIDO_P_PRACTICANTE, PRUEBA_APELLIDO_M_PRACTICANTE,
                TipoDeUsuario.PRACTICANTE, TipoEstado.ACTIVO);

        pruebaUtilDatos.insertarUsuarioPruebaBase(
                PRUEBA_NOMBRE_PROFESOR_INACTIVO, PRUEBA_APELLIDO_P_PROFESOR_INACTIVO, PRUEBA_APELLIDO_M_PROFESOR_INACTIVO,
                TipoDeUsuario.PROFESOR, TipoEstado.INACTIVO);

        Platform.runLater(() -> controlador.recargar());
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
    }

    @AfterEach
    void limpiarDatosPrueba() throws Exception {
        pruebaUtilDatos.limpiarTodosLosDatosPrueba();
    }

    @Test
    void busquedaPorNombreFiltraUnSoloResultado() {
        FxRobot robot = new FxRobot();

        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("#txtBuscar").write(PRUEBA_NOMBRE_PROFESOR);
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);

        TableView<UsuarioDTO> tabla = robot.lookup("#tablaUsuarios").queryTableView();
        assertEquals(1, tabla.getItems().size());
    }

    @Test
    void busquedaPorApellidoPaternoEncuentraUsuarioCorrecto() {
        FxRobot robot = new FxRobot();

        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("#txtBuscar").write(PRUEBA_APELLIDO_P_PRACTICANTE);
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);

        TableView<UsuarioDTO> tabla = robot.lookup("#tablaUsuarios").queryTableView();
        assertEquals(PRUEBA_NOMBRE_PRACTICANTE, tabla.getItems().get(0).getNombre());
    }

    @Test
    void busquedaSinCoincidenciasDejaTablaVacia() {
        FxRobot robot = new FxRobot();

        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("#txtBuscar").write("TextoInexistentePrueba");
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);

        TableView<UsuarioDTO> tabla = robot.lookup("#tablaUsuarios").queryTableView();
        assertEquals(0, tabla.getItems().size());
    }

    @Test
    void filtroPorTipoProfesorMuestraSoloProfesores() {
        FxRobot robot = new FxRobot();

        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("#cbFiltroTipo");
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("PROFESOR");
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);

        TableView<UsuarioDTO> tabla = robot.lookup("#tablaUsuarios").queryTableView();
        boolean todosSonProfesores = tabla.getItems().stream()
                .allMatch(usuario -> usuario.getTipoDeUsuario() == TipoDeUsuario.PROFESOR);
        assertTrue(todosSonProfesores);
    }

    @Test
    void botonLimpiarFiltrosVaciaElCampoDeBusqueda() {
        FxRobot robot = new FxRobot();

        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("#txtBuscar").write("TextoAleatorioPrueba");
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("LIMPIAR FILTROS");
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);

        TextField campoBusqueda = robot.lookup("#txtBuscar").queryAs(TextField.class);
        assertEquals("", campoBusqueda.getText());
    }

    @Test
    void contadorMuestraPluralCuandoHayVariosUsuarios() {
        FxRobot robot = new FxRobot();

        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);

        Label etiquetaContador = robot.lookup("#lblContador").queryAs(Label.class);
        assertTrue(etiquetaContador.getText().endsWith(" usuarios"));
    }

    @Test
    void contadorMuestraSingularCuandoSoloHayUnUsuario() {
        FxRobot robot = new FxRobot();

        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("#txtBuscar").write(PRUEBA_NOMBRE_PROFESOR);
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);

        Label etiquetaContador = robot.lookup("#lblContador").queryAs(Label.class);
        assertEquals("1 usuario", etiquetaContador.getText());
    }
}