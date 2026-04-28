package gui.controladores;

import excepciones.DAOExcepcion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logica.dao.AutoevaluacionDAO;
import logica.dto.AutoevaluacionDTO;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoevaluacionControlador implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(AutoevaluacionControlador.class.getName());
    private static final BigDecimal CALIFICACION_MINIMA = BigDecimal.ZERO;
    private static final BigDecimal CALIFICACION_MAXIMA = new BigDecimal("10.00");

    @FXML private TextField txtCalificacion;
    @FXML private TextArea txtComentarios;
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
            BigDecimal calificacion = new BigDecimal(txtCalificacion.getText().trim());
            String matricula = ""; //  obtener del SesionUsuario - Cambiale Jared

            AutoevaluacionDTO autoevaluacionDTO = new AutoevaluacionDTO(
                    0,
                    matricula,
                    calificacion,
                    txtComentarios.getText().trim()
            );

            AutoevaluacionDAO autoevaluacionDAO = new AutoevaluacionDAO();
            autoevaluacionDAO.agregarAutoevalaucion(autoevaluacionDTO);

            cerrarVentana();

        } catch (NumberFormatException e) {
            lblError.setText("La calificación debe ser un número válido.");
            lblError.setVisible(true);
        } catch (DAOExcepcion e) {
            LOGGER.log(Level.SEVERE, "Error al guardar autoevaluación", e);
            lblError.setText("No se pudo guardar la autoevaluación. Intente más tarde.");
            lblError.setVisible(true);
        }
    }

    @FXML
    private void manejarCancelar() {
        cerrarVentana();
    }

    private String validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtCalificacion.getText().trim().isEmpty()) {
            errores.append("La calificación no puede estar vacía.\n");
        } else {
            try {
                BigDecimal calificacion = new BigDecimal(txtCalificacion.getText().trim());
                boolean menorQueMinima = calificacion.compareTo(CALIFICACION_MINIMA) < 0;
                boolean mayorQueMaxima = calificacion.compareTo(CALIFICACION_MAXIMA) > 0;
                if (menorQueMinima || mayorQueMaxima) {
                    errores.append("La calificación debe estar entre 0.00 y 10.00.\n");
                }
            } catch (NumberFormatException e) {
                errores.append("La calificación debe ser un número válido.\n");
            }
        }

        if (txtComentarios.getText().trim().isEmpty()) {
            errores.append("Los comentarios no pueden estar vacíos.\n");
        }

        boolean hayErrores = errores.length() > 0;
        return hayErrores ? errores.toString() : null;
    }

    private void cerrarVentana() {
        ((Stage) btnGuardar.getScene().getWindow()).close();
    }
}