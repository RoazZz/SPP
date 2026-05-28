package gui.controladores;

import excepciones.DAOExcepcion;
import javafx.event.ActionEvent;
import logica.interfaces.Regresable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import logica.dto.ProyectoDTO;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static gui.controladores.NavegacionControlador.regresar;

public class ProyectosControlador implements Initializable, Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(ProyectosControlador.class.getName());

    @FXML private TableView<ProyectoDTO> tablaProyectos;
    @FXML private Label lblMensaje;

    private Scene escenaAnterior;
    private ProyectoControlador proyectoControlador;

    @Override
    public void initialize(URL urlRecibida, ResourceBundle recursoRecibido) {
        try {
            proyectoControlador = new ProyectoControlador();
            tablaProyectos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            cargarProyectos();
        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al inicializar ProyectoControlador", excepcionCapturada);
            lblMensaje.setText("Error al conectar con la base de datos.");
        }
    }

    void cargarProyectos() {
        try {
            List<ProyectoDTO> proyectosEncontrados = proyectoControlador.listarProyectos();
            ObservableList<ProyectoDTO> listaProyectos = FXCollections.observableArrayList(proyectosEncontrados);
            tablaProyectos.setItems(listaProyectos);
            REGISTRADOR.log(Level.INFO, "Proyectos cargados correctamente");
        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al cargar proyectos", excepcionCapturada);
            lblMensaje.setText("Error al cargar los proyectos.");
        }
    }

    @FXML
    private void manejarAgregar(ActionEvent eventoClic) {
        try {
            FXMLLoader cargadorVista = new FXMLLoader(getClass().getResource("/gui/vista/FXMLFormularioProyecto.fxml"));
            Parent vistaCargada = cargadorVista.load();
            Stage escenarioActual = (Stage) ((Node) eventoClic.getSource()).getScene().getWindow();

            Object controladorObtenido = cargadorVista.getController();
            if (controladorObtenido instanceof Regresable controladorRegresable) {
                controladorRegresable.setEscenaAnterior(escenarioActual.getScene());
            }

            if (controladorObtenido instanceof FormularioProyectoControlador formularioControlador) {
                formularioControlador.setControladorPadre(this);
            }

            escenarioActual.setScene(new Scene(vistaCargada));
            escenarioActual.show();
        } catch (IOException excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al abrir formulario de proyecto", excepcionCapturada);
            lblMensaje.setText("No se pudo abrir el formulario.");
        }
    }

    @FXML
    private void manejarEditar(ActionEvent eventoClic) {
        ProyectoDTO proyectoSeleccionado = tablaProyectos.getSelectionModel().getSelectedItem();
        if (proyectoSeleccionado == null) {
            lblMensaje.setText("Selecciona un proyecto para editar.");
            return;
        }
        try {
            FXMLLoader cargadorVista = new FXMLLoader(getClass().getResource("/gui/vista/FXMLFormularioProyecto.fxml"));
            Parent vistaCargada = cargadorVista.load();
            Stage escenarioActual = (Stage) ((Node) eventoClic.getSource()).getScene().getWindow();

            FormularioProyectoControlador formularioControlador = cargadorVista.getController();
            formularioControlador.setEscenaAnterior(escenarioActual.getScene());
            formularioControlador.cargarProyecto(proyectoSeleccionado);
            formularioControlador.configurarTitulo("Editar Proyecto");
            formularioControlador.setControladorPadre(this);

            escenarioActual.setScene(new Scene(vistaCargada));
            escenarioActual.show();
        } catch (IOException excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al abrir formulario de edición", excepcionCapturada);
            lblMensaje.setText("No se pudo abrir el formulario de edición.");
        }
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void manejarSalir(ActionEvent eventoClic) {
        Node nodoOrigen = (Node) eventoClic.getSource();
        regresar(nodoOrigen, this.escenaAnterior);
    }
}