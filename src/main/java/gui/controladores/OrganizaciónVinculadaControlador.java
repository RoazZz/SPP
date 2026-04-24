package gui.controladores;

import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import logica.dao.OrganizacionVinculadaDAO;
import logica.dto.OrganizacionVinculadaDTO;

public class OrganizaciónVinculadaControlador {
    @FXML
    private TextField txtIdOrganizacionVinculada;
    @FXML
    private TextField txtNombreOrganizacionVinculada;
    @FXML
    private TextField txtDireccionOrganizacionVinculada;
    @FXML
    private Label txtMensajeGuardar;

    @FXML
    public void manejarGuardar() {
        try{
            String idOrganizacion = txtIdOrganizacionVinculada.getText();
            String nombre = txtNombreOrganizacionVinculada.getText();
            String direccion = txtDireccionOrganizacionVinculada.getText();

            OrganizacionVinculadaDTO organizacionVinculadaDTO = new OrganizacionVinculadaDTO(idOrganizacion, nombre, direccion);

            OrganizacionVinculadaDAO organizacionVinculadaDAO = new OrganizacionVinculadaDAO();
            organizacionVinculadaDAO.agregarOrganizacionVinculada(organizacionVinculadaDTO);

            txtMensajeGuardar.setText("Organización Vinculada guardada con exito");

        }catch (Exception e){
            txtMensajeGuardar.setText("Error: " + e.getMessage());
        }
    }
}
