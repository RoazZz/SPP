package gui.controladores;

import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import logica.dao.OrganizacionVinculadaDAO;
import logica.dto.OrganizacionVinculadaDTO;

import java.util.logging.Level;
import java.util.logging.Logger;

public class OrganizaciónVinculadaControlador {
    private static final Logger logger = Logger.getLogger(OrganizaciónVinculadaControlador.class.getName());

    @FXML
    private TextField txtIdOrganizacionVinculada;
    @FXML
    private TextField txtNombreOrganizacionVinculada;
    @FXML
    private TextField txtDireccionOrganizacionVinculada;
    @FXML
    private Label txtMensajeGuardar;
    @FXML
    private Label txtErrorDeCampos;



    @FXML
    public void manejarGuardar() {

        if (!validarCamposVacios()){
            return;
        }

        try{
            String idOrganizacion = txtIdOrganizacionVinculada.getText();
            String nombre = txtNombreOrganizacionVinculada.getText();
            String direccion = txtDireccionOrganizacionVinculada.getText();

            OrganizacionVinculadaDTO organizacionVinculadaDTO = new OrganizacionVinculadaDTO(idOrganizacion, nombre, direccion);

            OrganizacionVinculadaDAO organizacionVinculadaDAO = new OrganizacionVinculadaDAO();
            organizacionVinculadaDAO.agregarOrganizacionVinculada(organizacionVinculadaDTO);

            txtMensajeGuardar.setText("Organización Vinculada guardada con exito");

        }catch (Exception e){
           logger.log(Level.SEVERE,"Error al guardar Organizacion vinculada");
        }
    }

    public boolean validarCamposVacios(){
        StringBuilder camposVacios = new StringBuilder();

        if (txtIdOrganizacionVinculada.getText().trim().isEmpty()){
            camposVacios.append("ID Organizacion Vinculada es obligatorio. \n");
        }

        if (txtNombreOrganizacionVinculada.getText().trim().isEmpty()){
            camposVacios.append("Nombre es obligatorio. \n");
        }

        if (txtDireccionOrganizacionVinculada.getText().trim().isEmpty()){
            camposVacios.append("Direccion es obligatorio. \n");
        }

        if (camposVacios.length() > 0 ){
            txtErrorDeCampos.setText(camposVacios.toString());
            return false;
        }

        txtErrorDeCampos.setText("");
        return true;
    }
}
