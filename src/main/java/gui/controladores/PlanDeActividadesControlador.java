package gui.controladores;

import excepciones.DAOExcepcion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logica.dao.PlanDeActividadesDAO;
import logica.dto.PlanDeActividadesDTO;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlanDeActividadesControlador implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(PlanDeActividadesControlador.class.getName());

    @FXML private TextField txtMatricula;
    @FXML private TextField txtIdProyecto;
    @FXML private TextArea txtDescripcion;
    @FXML private Label lblError;
    @FXML private Button btnGuardar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblError.setVisible(false);
    }

    @FXML
    private void manejarGuardar() {
        lblError.setVisible(false);

        String mensajeValidacion = validarCampos();
        if (mensajeValidacion != null) {
            lblError.setText(mensajeValidacion);
            lblError.setVisible(true);
            return;
        }

        try {
            int idProyecto = Integer.parseInt(txtIdProyecto.getText().trim());

            PlanDeActividadesDTO planDeActividadesDTO = new PlanDeActividadesDTO(
                    0,
                    txtMatricula.getText().trim(),
                    idProyecto,
                    txtDescripcion.getText().trim()
            );

            PlanDeActividadesDAO planDeActividadesDAO = new PlanDeActividadesDAO();
            planDeActividadesDAO.agregarPlanDeActividades(planDeActividadesDTO);

            cerrarVentana();

        } catch (NumberFormatException e) {
            lblError.setText("El ID del proyecto debe ser un número válido.");
            lblError.setVisible(true);
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al guardar plan de actividades", e);
            lblError.setText("No se pudo guardar el plan. Intente más tarde.");
            lblError.setVisible(true);
        }
    }

    @FXML
    private void manejarCancelar() {
        cerrarVentana();
    }

    private String validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtMatricula.getText().trim().isEmpty()) {
            errores.append("La matrícula no puede estar vacía.\n");
        }
        if (txtIdProyecto.getText().trim().isEmpty()) {
            errores.append("El ID del proyecto no puede estar vacío.\n");
        } else {
            try {
                Integer.parseInt(txtIdProyecto.getText().trim());
            } catch (NumberFormatException e) {
                errores.append("El ID del proyecto debe ser un número válido.\n");
            }
        }
        if (txtDescripcion.getText().trim().isEmpty()) {
            errores.append("La descripción no puede estar vacía.\n");
        }

        boolean hayErrores = errores.length() > 0;
        return hayErrores ? errores.toString() : null;
    }

    private void cerrarVentana() {
        ((Stage) btnGuardar.getScene().getWindow()).close();
    }
}