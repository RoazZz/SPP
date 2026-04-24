package gui.controladores;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import logica.dao.ProyectoDAO;
import logica.dto.ProyectoDTO;

public class ProyectoControlador {

    @FXML
    private TextField txtIdOrganizacion;

    @FXML
    private TextField txtNumeroDePersonal;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private TextField txtMensajeGuardar;

    @FXML
    public void manejarGuardarProyecto() {
        try{
            String idOrganizacion = txtIdOrganizacion.getText();
            String numeroDePersonal = txtNumeroDePersonal.getText();
            String nombre = txtNombre.getText();
            String descripcion = txtDescripcion.getText();

            ProyectoDTO proyectoDTO = new ProyectoDTO(0, idOrganizacion,
                    numeroDePersonal, nombre, descripcion);

            ProyectoDAO proyectoDAO = new ProyectoDAO();
            proyectoDAO.agregarProyecto(proyectoDTO);

            txtMensajeGuardar.setText("Proyecto guardado con exito");

        }catch(Exception e) {
            txtMensajeGuardar.setText("Error: " + e.getMessage());
        }

    }
}
