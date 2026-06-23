package gui.controladores;

import excepciones.DAOExcepcion;
import logica.interfaces.Regresable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import logica.dao.UsuarioDAO;
import logica.dto.UsuarioDTO;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstadoUsuario;
import javafx.scene.layout.HBox;
import logica.utilidades.PermisosRol;
import logica.utilidades.RegistradorBitacora;
import logica.utilidades.SesionUsuarioSingleton;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static gui.controladores.NavegacionControlador.abrirVentana;
import static gui.controladores.NavegacionControlador.regresar;

public class ListaUsuariosControlador implements Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(ListaUsuariosControlador.class.getName());
    private static final String TODOS = "TODOS";

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbFiltroTipo;
    @FXML private TableView<UsuarioDTO> tablaUsuarios;
    @FXML private TableColumn<UsuarioDTO, String> colNombre;
    @FXML private TableColumn<UsuarioDTO, String> colApellidoP;
    @FXML private TableColumn<UsuarioDTO, String> colApellidoM;
    @FXML private TableColumn<UsuarioDTO, String> colTipo;
    @FXML private TableColumn<UsuarioDTO, String> colEstado;
    @FXML private TableColumn<UsuarioDTO, Void> colAcciones;
    @FXML private Label lblContador;
    @FXML private Button btnAnadirUsuario;

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
        btnAnadirUsuario.setOnAction(eventoClic -> abrirVentana("/gui/vista/FXMLFormularioUsuario.fxml", btnAnadirUsuario));
    }

    private void cargarTiposPermitidos() {
        TipoDeUsuario rolActual = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getTipoDeUsuario();
        PermisosRol permisosUsuario = new PermisosRol(rolActual);
        btnAnadirUsuario.setVisible(permisosUsuario.puedeAgregarUsuario());
        btnAnadirUsuario.setManaged(permisosUsuario.puedeAgregarUsuario());
    }

    private void configurarColumnas() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellidoP.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        colApellidoM.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));

        colTipo.setCellValueFactory(celdaTabla -> {
            String valorCelda = "";
            if (celdaTabla.getValue().getTipoDeUsuario() != null) {
                valorCelda = celdaTabla.getValue().getTipoDeUsuario().name();
            }
            return new SimpleStringProperty(valorCelda);
        });

        colEstado.setCellValueFactory(celdaTabla -> {
            String valorCelda = "";
            if (celdaTabla.getValue().getTipoEstado() != null) {
                valorCelda = celdaTabla.getValue().getTipoEstado().name();
            }
            return new SimpleStringProperty(valorCelda);
        });
    }


    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(parametroColumna -> new TableCell<UsuarioDTO, Void>() {
            private final Button btnInactivar = new Button("INACTIVAR");
            private final HBox contenedorBotones = new HBox(btnInactivar);

            {
                btnInactivar.getStyleClass().add("btn-cancelar");
                contenedorBotones.setAlignment(Pos.CENTER);
                btnInactivar.setOnAction(eventoClic -> {
                    UsuarioDTO usuarioSeleccionado = getTableView().getItems().get(getIndex());
                    manejarInactivar(usuarioSeleccionado);
                });
            }

            @Override
            protected void updateItem(Void elementoCelda, boolean estaVacio) {
                super.updateItem(elementoCelda, estaVacio);

                if (estaVacio || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }

                TipoDeUsuario rolActual = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getTipoDeUsuario();
                PermisosRol permisosUsuario = new PermisosRol(rolActual);
                UsuarioDTO usuarioEnFila = getTableView().getItems().get(getIndex());

                if (permisosUsuario.puedeInactivarUsuario() && usuarioEnFila.getTipoEstado() == TipoEstadoUsuario.ACTIVO) {
                    btnInactivar.setVisible(true);
                    setGraphic(contenedorBotones);
                } else {
                    setGraphic(null);
                }
            }
        });
    }

    private void configurarFiltroTipo() {
        TipoDeUsuario rolActual = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getTipoDeUsuario();
        PermisosRol permisosUsuario = new PermisosRol(rolActual);
        List<TipoDeUsuario> tiposPermitidos = permisosUsuario.tiposVisibles();

        cbFiltroTipo.getItems().add(TODOS);
        for (TipoDeUsuario tipoActual : tiposPermitidos) {
            cbFiltroTipo.getItems().add(tipoActual.name());
        }
        cbFiltroTipo.setValue(TODOS);
        cbFiltroTipo.valueProperty().addListener((observadorPropiedad, valorAnterior, valorNuevo) -> aplicarFiltros());
    }

    private void cargarUsuarios() {
        try {
            TipoDeUsuario rolActual = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getTipoDeUsuario();
            PermisosRol permisosUsuario = new PermisosRol(rolActual);
            List<TipoDeUsuario> tiposPermitidos = permisosUsuario.tiposVisibles();

            UsuarioDAO usuarioObjetoDeAcceso = new UsuarioDAO();
            List<UsuarioDTO> usuariosRecuperados = usuarioObjetoDeAcceso.listarUsuariosPorTipos(tiposPermitidos);
            RegistradorBitacora.registrar("CONSULTA_USUARIOS", "Consultó el listado de usuarios");
            listaCompleta = FXCollections.observableArrayList(usuariosRecuperados);
            listaFiltrada = new FilteredList<>(listaCompleta, usuarioFiltro -> true);

            SortedList<UsuarioDTO> listaOrdenada = new SortedList<>(listaFiltrada);
            listaOrdenada.comparatorProperty().bind(tablaUsuarios.comparatorProperty());

            tablaUsuarios.setItems(listaOrdenada);
            actualizarContador();
        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al cargar la lista de usuarios", excepcionCapturada);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los usuarios.");
        }
    }

    private void configurarBusquedaReactiva() {
        txtBuscar.textProperty().addListener((observadorPropiedad, valorAnterior, valorNuevo) -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        if (listaFiltrada == null) {
            return;
        }

        String textoBusqueda = "";
        if (txtBuscar.getText() != null) {
            textoBusqueda = txtBuscar.getText().trim().toLowerCase();
        }

        String tipoSeleccionado = cbFiltroTipo.getValue();
        final String textoFinalBusqueda = textoBusqueda;

        listaFiltrada.setPredicate(usuarioParaFiltrar -> {
            if (tipoSeleccionado != null && !TODOS.equals(tipoSeleccionado)) {
                if (usuarioParaFiltrar.getTipoDeUsuario() == null || !usuarioParaFiltrar.getTipoDeUsuario().name().equals(tipoSeleccionado)) {
                    return false;
                }
            }

            if (!textoFinalBusqueda.isEmpty()) {
                String nombreTransformado = convertirMinusculasSeguro(usuarioParaFiltrar.getNombre());
                String apellidoPaternoTransformado = convertirMinusculasSeguro(usuarioParaFiltrar.getApellidoPaterno());
                String apellidoMaternoTransformado = convertirMinusculasSeguro(usuarioParaFiltrar.getApellidoMaterno());

                return nombreTransformado.contains(textoFinalBusqueda) ||
                        apellidoPaternoTransformado.contains(textoFinalBusqueda) ||
                        apellidoMaternoTransformado.contains(textoFinalBusqueda);
            }

            return true;
        });

        actualizarContador();
    }

    private void manejarInactivar(UsuarioDTO usuarioObjetivo) {
        if (!mostrarConfirmacion("¿Está seguro de inactivar al usuario " + usuarioObjetivo.getNombre() + "?")) {
            return;
        }
        try {
            inactivarUsuario(usuarioObjetivo);
            tablaUsuarios.refresh();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario inactivado.");
        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al inactivar usuario", excepcionCapturada);
            usuarioObjetivo.setTipoEstado(TipoEstadoUsuario.ACTIVO);
            tablaUsuarios.refresh();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo completar la acción.");
        }
    }

    private void inactivarUsuario(UsuarioDTO usuarioObjetivo) throws DAOExcepcion {
        usuarioObjetivo.setTipoEstado(TipoEstadoUsuario.INACTIVO);
        new UsuarioDAO().actualizarUsuario(usuarioObjetivo);
        RegistradorBitacora.registrar("INACTIVAR_USUARIO", "Inactivó al usuario: " + usuarioObjetivo.getNombre());
    }

    private boolean mostrarConfirmacion(String mensajeConfirmacion) {
        Alert ventanaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        ventanaConfirmacion.setTitle("Confirmar acción");
        ventanaConfirmacion.setHeaderText(null);
        ventanaConfirmacion.setContentText(mensajeConfirmacion);

        Optional<ButtonType> respuestaUsuario = ventanaConfirmacion.showAndWait();
        boolean fueConfirmado = false;

        if (respuestaUsuario.isPresent() && respuestaUsuario.get() == ButtonType.OK) {
            fueConfirmado = true;
        }

        return fueConfirmado;
    }

    private String convertirMinusculasSeguro(String textoOriginal) {
        String textoTransformado = "";
        if (textoOriginal != null) {
            textoTransformado = textoOriginal.toLowerCase();
        }
        return textoTransformado;
    }

    private void actualizarContador() {
        int totalUsuarios = 0;
        if (listaFiltrada != null) {
            totalUsuarios = listaFiltrada.size();
        }

        String sufijoTexto = " usuarios";
        if (totalUsuarios == 1) {
            sufijoTexto = " usuario";
        }

        lblContador.setText(totalUsuarios + sufijoTexto);
    }

    @FXML
    private void manejarLimpiarFiltros() {
        txtBuscar.clear();
        cbFiltroTipo.setValue(TODOS);
    }

    private void mostrarAlerta(Alert.AlertType tipoAlerta, String tituloAlerta, String mensajeAlerta) {
        Alert ventanaAlerta = new Alert(tipoAlerta);
        ventanaAlerta.setTitle(tituloAlerta);
        ventanaAlerta.setHeaderText(null);
        ventanaAlerta.setContentText(mensajeAlerta);
        ventanaAlerta.showAndWait();
    }

    public void recargar() {
        cargarUsuarios();
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void manejarClicCancelar(ActionEvent eventoBoton) {
        Node nodoOrigenDeAtras = (Node) eventoBoton.getSource();
        regresar(nodoOrigenDeAtras, this.escenaAnterior);
    }
}