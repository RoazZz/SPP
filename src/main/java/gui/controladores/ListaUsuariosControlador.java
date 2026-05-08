package gui.controladores;

import excepciones.DAOExcepcion;
import interfaces.Regresable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import logica.dao.UsuarioDAO;
import logica.dto.UsuarioDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;
import javafx.scene.layout.HBox;
import logica.utilidades.PermisosRol;
import logica.utilidades.SesionUsuarioSingleton;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListaUsuariosControlador implements Regresable{
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
    @FXML private Button btnAñadirUsuario;
    @FXML private Button btnCerrar;
    private Scene escenaAnterior;

    private ObservableList<UsuarioDTO> listaCompleta;
    private FilteredList<UsuarioDTO> listaFiltrada;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarColumnaAcciones();
        configurarFiltroTipo();
        cargarUsuarios();
        configurarBusquedaReactiva();
        cargarTiposPermitidos();
        btnCerrar.setOnAction(e -> regresar());
        btnAñadirUsuario.setOnAction(e -> NavegacionControlador.abrirVentana("/gui/vista/FXMLFormularioUsuario.fxml", btnAñadirUsuario));
    }

    private void cargarTiposPermitidos() {
        TipoDeUsuario rol = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getTipoDeUsuario();
        PermisosRol permisos = new PermisosRol(rol);
        btnAñadirUsuario.setVisible(permisos.puedeAgregarUsuario());
        btnAñadirUsuario.setManaged(permisos.puedeAgregarUsuario());
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
                    UsuarioDTO usuarioDTO = getTableView().getItems().get(getIndex());
                    manejarInactivar(usuarioDTO);
                });
            }

            @Override
            protected void updateItem(Void item, boolean vacio) {
                super.updateItem(item, vacio);
                if (vacio || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    UsuarioDTO usuarioDTO = getTableView().getItems().get(getIndex());
                    if (usuarioDTO.getTipoEstado() == TipoEstado.ACTIVO) {
                        btnInactivar.setVisible(true);
                        btnInactivar.setText("INACTIVAR");
                    } else {
                        btnInactivar.setVisible(false);
                        btnInactivar.setText("INACTIVO");
                    }
                    setGraphic(contenedor);
                }
            }
        });
    }

    private void configurarFiltroTipo() {
        TipoDeUsuario rol = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getTipoDeUsuario();
        PermisosRol permisos = new PermisosRol(rol);
        List<TipoDeUsuario> tiposPermitidos = permisos.tiposVisibles();

        cbFiltroTipo.getItems().add(TODOS);
        for (TipoDeUsuario tipo : tiposPermitidos) {
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
            listaFiltrada = new FilteredList<>(listaCompleta, usuarioDTO -> true);

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

        listaFiltrada.setPredicate(usuarioDTO -> {
            if (tipoSeleccionado != null && !TODOS.equals(tipoSeleccionado)) {
                if (usuarioDTO.getTipoDeUsuario() == null
                        || !usuarioDTO.getTipoDeUsuario().name().equals(tipoSeleccionado)) {
                    return false;
                }
            }

            if (!texto.isEmpty()) {
                String nombre = lowerSafe(usuarioDTO.getNombre());
                String apellidoPaterno = lowerSafe(usuarioDTO.getApellidoPaterno());
                String apellidoMaterno = lowerSafe(usuarioDTO.getApellidoMaterno());
                return nombre.contains(texto) || apellidoPaterno.contains(texto) || apellidoMaterno.contains(texto);
            }

            return true;
        });

        actualizarContador();
    }

    private void manejarInactivar(UsuarioDTO usuario) {
        if (!mostrarConfirmacion("¿Está seguro de inactivar al usuario " + usuario.getNombre() + "?")) {
            return;
        }
        try {
            inactivarUsuario(usuario);
            tablaUsuarios.refresh();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario inactivado.");
        } catch (DAOExcepcion e) {
            logger.log(Level.SEVERE, "Error al inactivar usuario", e);
            usuario.setTipoEstado(TipoEstado.ACTIVO);
            tablaUsuarios.refresh();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo completar la acción.");
        }
    }

    private void inactivarUsuario(UsuarioDTO usuario) throws DAOExcepcion {
        usuario.setTipoEstado(TipoEstado.INACTIVO);
        new UsuarioDAO().actualizarUsuario(usuario);
    }

    private boolean mostrarConfirmacion(String mensaje) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar acción");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(mensaje);

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        return respuesta.isPresent() && respuesta.get() == ButtonType.OK;
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

    @Override
    public void setEscenaAnterior(Scene escena) {
        this.escenaAnterior = escena;
    }

    private void regresar() {
        if (escenaAnterior != null) {
            Stage escenario = (Stage) tablaUsuarios.getScene().getWindow();
            escenario.setScene(escenaAnterior);
            escenario.show();
        }
    }
}