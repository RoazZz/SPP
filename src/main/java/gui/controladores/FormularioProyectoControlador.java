package gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import logica.dao.ProyectoDAO;
import logica.dto.ProyectoDTO;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FormularioProyectoControlador {

    private static final Logger logger = Logger.getLogger(FormularioProyectoControlador.class.getName());

    @FXML
    private TextField txtIdOrganizacionVinculada;

    @FXML
    private TextField txtNumeroDePersonal;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private Label txtMensajeGuardar;

    @FXML
    private Label txtErrorDeCampos;


    @FXML
    public void manejarGuardarProyecto() {

        if (!validarCamposVacios()){
            return;
        }

        try{
            String idOrganizacion = txtIdOrganizacionVinculada.getText();
            String numeroDePersonal = txtNumeroDePersonal.getText();
            String nombre = txtNombre.getText();
            String descripcion = txtDescripcion.getText();

            ProyectoDTO proyectoDTO = new ProyectoDTO(0, idOrganizacion,
                    numeroDePersonal, nombre, descripcion);

            ProyectoDAO proyectoDAO = new ProyectoDAO();
            proyectoDAO.agregarProyecto(proyectoDTO);

            txtMensajeGuardar.setText("Proyecto guardado con exito");

        }catch(Exception e) {
            logger.log(Level.SEVERE, "Error al guardar Proyecto.");
        }

    }

    private boolean validarCamposVacios () {
        StringBuilder camposVacios = new StringBuilder();

        if (txtIdOrganizacionVinculada.getText().trim().isEmpty()){
            camposVacios.append("Id Organización Vinculada es obligatorio. \n");
        }

        if (txtNumeroDePersonal.getText().trim().isEmpty()){
            camposVacios.append("Numero de Personal es obligatorio. \n");
        }

        if (txtNombre.getText().trim().isEmpty()){
            camposVacios.append("Nombre es obligatorio. \n");
        }

        if (txtDescripcion.getText().trim().isEmpty()){
            camposVacios.append("Descripción es obligatorio. \n");
        }

        if (camposVacios.length() > 0 ){
            txtErrorDeCampos.setText(camposVacios.toString());
            return false;
        }

        txtErrorDeCampos.setText("");
        return true;

    }
}
