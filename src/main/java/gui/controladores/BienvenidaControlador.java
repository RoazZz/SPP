package gui.controladores;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;

import static gui.controladores.NavegacionControlador.abrirVentana;

public class BienvenidaControlador {

    @FXML
    private void manejarComenzar(ActionEvent eventoClic) {
        Node nodoOrigen = (Node) eventoClic.getSource();
        abrirVentana("/gui/vista/FXMLRegistroAdministrador.fxml", nodoOrigen);
    }
}