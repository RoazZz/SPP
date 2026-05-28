package gui.controladores;

import excepciones.DAOExcepcion;
import logica.interfaces.Regresable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import logica.dao.OrganizacionVinculadaDAO;
import logica.dto.OrganizacionVinculadaDTO;

import java.util.logging.Level;
import java.util.logging.Logger;

import static gui.controladores.NavegacionControlador.regresar;

public class OrganizacionVinculadaControlador implements Regresable {
    private static final Logger REGISTRADOR = Logger.getLogger(OrganizacionVinculadaControlador.class.getName());

    @FXML private TextField txtIdOrganizacionVinculada;
    @FXML private TextField txtNombreOrganizacionVinculada;
    @FXML private TextField txtDireccionOrganizacionVinculada;
    @FXML private Label lblMensajeGuardar;
    @FXML private Label lblErrorDeCampos;

    private Scene escenaAnterior;

    @FXML
    private void manejarGuardar(ActionEvent eventoClic) {
        if (!validarCamposVacios()) {
            return;
        }

        try {
            OrganizacionVinculadaDTO organizacionNueva = new OrganizacionVinculadaDTO(
                    txtIdOrganizacionVinculada.getText().trim(),
                    txtNombreOrganizacionVinculada.getText().trim(),
                    txtDireccionOrganizacionVinculada.getText().trim()
            );

            new OrganizacionVinculadaDAO().agregarOrganizacionVinculada(organizacionNueva);

            lblMensajeGuardar.getStyleClass().add("label-exito");
            lblMensajeGuardar.setText("Organización Vinculada guardada con éxito");
            limpiarCampos();
        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al guardar organización vinculada", excepcionCapturada);
            lblErrorDeCampos.setText("Error al guardar en la base de datos.");
        }
    }

    private boolean validarCamposVacios() {
        StringBuilder camposVacios = new StringBuilder();

        if (txtIdOrganizacionVinculada.getText().trim().isEmpty()) {
            camposVacios.append("ID Organización Vinculada es obligatorio. \n");
        }
        if (txtNombreOrganizacionVinculada.getText().trim().isEmpty()) {
            camposVacios.append("Nombre es obligatorio. \n");
        }
        if (txtDireccionOrganizacionVinculada.getText().trim().isEmpty()) {
            camposVacios.append("Dirección es obligatoria. \n");
        }

        if (camposVacios.length() > 0) {
            lblErrorDeCampos.setText(camposVacios.toString());
            return false;
        }

        lblErrorDeCampos.setText("");
        return true;
    }

    private void limpiarCampos() {
        txtIdOrganizacionVinculada.clear();
        txtNombreOrganizacionVinculada.clear();
        txtDireccionOrganizacionVinculada.clear();
    }

    @FXML
    private void manejarCancelar(ActionEvent eventoClic) {
        Node nodoOrigen = (Node) eventoClic.getSource();
        regresar(nodoOrigen, this.escenaAnterior);
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }
}