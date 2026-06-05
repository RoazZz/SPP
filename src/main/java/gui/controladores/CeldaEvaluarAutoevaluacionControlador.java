package gui.controladores;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import logica.dto.AutoevaluacionDTO;
import java.util.function.Consumer;

public class CeldaEvaluarAutoevaluacionControlador extends TableCell<AutoevaluacionDTO, Void> {
    private static final double ESPACIADO_BOTONES = 10.0;
    private final Button btnEvaluar = new Button("EVALUAR");
    private final HBox contenedor = new HBox(ESPACIADO_BOTONES, btnEvaluar);
    public CeldaEvaluarAutoevaluacionControlador(Consumer<AutoevaluacionDTO> accionEvaluar) {
        contenedor.setAlignment(Pos.CENTER);
        btnEvaluar.getStyleClass().add("btn-guardar");
        btnEvaluar.setOnAction(eventoClic -> accionEvaluar.accept(obtenerAutoevaluacionDeFila()));
    }
    private AutoevaluacionDTO obtenerAutoevaluacionDeFila() {
        return getTableView().getItems().get(getIndex());
    }
    @Override
    protected void updateItem(Void elementoCelda, boolean estaVacio) {
        super.updateItem(elementoCelda, estaVacio);
        if (estaVacio) {
            setGraphic(null);
        } else {
            setGraphic(contenedor);
        }
    }
}
