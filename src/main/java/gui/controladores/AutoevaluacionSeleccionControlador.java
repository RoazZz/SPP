package gui.controladores;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import logica.interfaces.Regresable;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import static gui.controladores.NavegacionControlador.abrirVentana;
import static gui.controladores.NavegacionControlador.regresar;

public class AutoevaluacionSeleccionControlador implements Regresable {

    @FXML private Button btnGenerar;
    @FXML private Button btnSubir;

    private Scene escenaAnterior;

    @FXML
    public void initialize() {
        btnGenerar.setOnAction(evento -> abrirVentana("/gui/vista/FXMLAutoevaluacionGenerar.fxml", btnGenerar)
        );
        btnSubir.setOnAction(evento -> abrirVentana("/gui/vista/FXMLAutoevaluacionSubir.fxml", btnSubir)
        );
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void manejarSalir(ActionEvent eventoBoton) {
        Node nodoOrigenDeAtras = (Node) eventoBoton.getSource();
        regresar(nodoOrigenDeAtras, this.escenaAnterior);
    }
}