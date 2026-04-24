package gui.controladores;

import excepciones.DAOExcepcion;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import logica.dao.UsuarioDAO;
import logica.dto.UsuarioDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListaUsuariosControlador {
    private static final Logger logger = Logger.getLogger(ListaUsuariosControlador.class.getName());
    private static final String TODOS = "TODOS";

    @FXML
    private TextField txtBuscar;
    @FXML private ComboBox<String> cbFiltroTipo;
    @FXML private TableView<UsuarioDTO> tablaUsuarios;
    @FXML private TableColumn<UsuarioDTO, String> colNombre;
    @FXML private TableColumn<UsuarioDTO, String> colApellidoP;
    @FXML private TableColumn<UsuarioDTO, String> colApellidoM;
    @FXML private TableColumn<UsuarioDTO, String> colTipo;
    @FXML private TableColumn<UsuarioDTO, String> colEstado;
    @FXML private TableColumn<UsuarioDTO, Void> colAcciones;
    @FXML private Label lblContador;

    private ObservableList<UsuarioDTO> listaCompleta;
    private FilteredList<UsuarioDTO> listaFiltrada;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarColumnaAcciones();
        configurarFiltroTipo();
        cargarUsuarios();
        configurarBusquedaReactiva();
    }

    private void configurarColumnas(){
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidoP.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        colApellidoM.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        colTipo.setCellValueFactory(
                cell->new SimpleStringProperty(
                        cell.getValue().getTipoDeUsuario() != null ? cell.getValue().getTipoDeUsuario().name() : ""
                )
        );

        colEstado.setCellValueFactory(
                cell -> new SimpleStringProperty(
                        cell.getValue().getTipoEstado() != null
                                ? cell.getValue().getTipoEstado().name() : ""
                )
        );
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnInactivar = new Button("INACTIVAR");
            private final HBox contenedor = new HBox(btnInactivar);

            {
                btnInactivar.getStyleClass().add("btn-cancelar");
                contenedor.setAlignment(Pos.CENTER);
                btnInactivar.setOnAction(e -> {
                    UsuarioDTO usuario = getTableView().getItems().get(getIndex());
                    manejarInactivar(usuario);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    UsuarioDTO usuario = getTableView().getItems().get(getIndex());
                    // Solo mostrar el botón si el usuario está activo
                    if (usuario.getTipoEstado() == TipoEstado.ACTIVO) {
                        btnInactivar.setVisible(false);
                        btnInactivar.setText("INACTIVAR");
                    } else {
                        btnInactivar.setVisible(true);
                        btnInactivar.setText("INACTIVO");
                    }
                    setGraphic(contenedor);
                }
            }
        });
    }

    private void configurarFiltroTipo() {
        cbFiltroTipo.getItems().add(TODOS);
        for (TipoDeUsuario tipo : TipoDeUsuario.values()) {
            cbFiltroTipo.getItems().add(tipo.name());
        }
        cbFiltroTipo.setValue(TODOS);
        cbFiltroTipo.valueProperty().addListener((obs, viejo, nuevo) -> aplicarFiltros());
    }

    private void cargarUsuarios() {
        try {
            UsuarioDAO dao = new UsuarioDAO();
            List<UsuarioDTO> usuarios = dao.listarUsuarios();

            listaCompleta = FXCollections.observableArrayList(usuarios);
            listaFiltrada = new FilteredList<>(listaCompleta, u -> true);

            SortedList<UsuarioDTO> ordenada = new SortedList<>(listaFiltrada);
            ordenada.comparatorProperty().bind(tablaUsuarios.comparatorProperty());

            tablaUsuarios.setItems(ordenada);
            actualizarContador();
        } catch (DAOExcepcion e) {
            logger.log(Level.SEVERE, "Error al cargar la lista de usuarios", e);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los usuarios.");
        }
    }

    private void configurarBusquedaReactiva() {
        txtBuscar.textProperty().addListener((observable, viejo, nuevo) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        if (listaFiltrada == null) return;

        String texto = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
        String tipoSeleccionado = cbFiltroTipo.getValue();

        listaFiltrada.setPredicate(usuario -> {
            if (tipoSeleccionado != null && !TODOS.equals(tipoSeleccionado)) {
                if (usuario.getTipoDeUsuario() == null
                        || !usuario.getTipoDeUsuario().name().equals(tipoSeleccionado)) {
                    return false;
                }
            }

            if (!texto.isEmpty()) {
                String nombre = lowerSafe(usuario.getNombre());
                String apellidoPaterno = lowerSafe(usuario.getApellidoPaterno());
                String apellidoMaterno = lowerSafe(usuario.getApellidoMaterno());
                return nombre.contains(texto) || apellidoPaterno.contains(texto) || apellidoMaterno.contains(texto);
            }

            return true;
        });

        actualizarContador();
    }

    private void manejarInactivar(UsuarioDTO usuario) {
        // Confirmación antes de inactivar
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar inactivación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de inactivar al usuario " + usuario.getNombre() + " " + usuario.getApellidoPaterno() + "?");
        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (respuesta.isEmpty() || respuesta.get() != ButtonType.OK) {
            return;
        }

        try {
            usuario.setTipoEstado(TipoEstado.INACTIVO);

            UsuarioDAO dao = new UsuarioDAO();
            dao.actualizarUsuario(usuario);

            tablaUsuarios.refresh();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito","El usuario ha sido inactivado correctamente.");
        } catch (DAOExcepcion e) {
            logger.log(Level.SEVERE, "Error al inactivar al usuario", e);
            usuario.setTipoEstado(TipoEstado.ACTIVO);
            tablaUsuarios.refresh();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo inactivar al usuario.");
        }
    }

    private String lowerSafe(String s) {
        return s == null ? "" : s.toLowerCase();
    }

    private void actualizarContador() {
        int total = listaFiltrada == null ? 0 : listaFiltrada.size();
        lblContador.setText(total + (total == 1 ? " usuario" : " usuarios"));
    }

    @FXML
    private void manejarLimpiarFiltros() {
        txtBuscar.clear();
        cbFiltroTipo.setValue(TODOS);
    }

    @FXML
    private void manejarCerrar() {
        ((Stage) tablaUsuarios.getScene().getWindow()).close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void recargar() {
        cargarUsuarios();
    }
}