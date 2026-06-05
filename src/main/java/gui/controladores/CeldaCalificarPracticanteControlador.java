package gui.controladores;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import logica.dto.PracticanteDTO;
import java.util.function.Consumer;
public class CeldaCalificarPracticanteControlador extends TableCell<PracticanteDTO, Void> {
    private static final double ESPACIADO_BOTONES = 10.0;
    private final Button btnCalificar = new Button("CALIFICAR");
    private final HBox contenedor = new HBox(ESPACIADO_BOTONES, btnCalificar);
    public CeldaCalificarPracticanteControlador(Consumer<PracticanteDTO> accionCalificar) {
        contenedor.setAlignment(Pos.CENTER);
        btnCalificar.getStyleClass().add("btn-guardar");
        btnCalificar.setOnAction(eventoClic -> accionCalificar.accept(obtenerPracticanteDeFila()));
    }
    private PracticanteDTO obtenerPracticanteDeFila() {
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
