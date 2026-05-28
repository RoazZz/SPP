package gui.controladores;

import logica.interfaces.Regresable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import java.util.logging.Logger;

import static gui.controladores.NavegacionControlador.abrirVentana;
import static gui.controladores.NavegacionControlador.regresar;

public class ReporteSeleccionControlador implements Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(ReporteSeleccionControlador.class.getName());
    private Scene escenaAnterior;

    @FXML
    private void manejarGenerar(ActionEvent eventoClic) {
        Node nodoOrigen = (Node) eventoClic.getSource();
        abrirVentana("/gui/vista/FXMLReporteGenerar.fxml", nodoOrigen);
    }

    @FXML
    private void manejarAnadir(ActionEvent eventoClic) {
        Node nodoOrigen = (Node) eventoClic.getSource();
        abrirVentana("/gui/vista/FXMLReporteAnadir.fxml", nodoOrigen);
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void manejarCancelar(ActionEvent eventoClic) {
        Node nodoOrigen = (Node) eventoClic.getSource();
        regresar(nodoOrigen, this.escenaAnterior);
    }
}