package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.ReglaDeNegocioExcepcion;
import logica.interfaces.Regresable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import logica.dto.CoordinadorDTO;
import logica.dto.PracticanteDTO;
import logica.dto.ProfesorDTO;
import logica.enums.TipoDeUsuario;
import logica.utilidades.PermisosRol;
import logica.utilidades.RegistradorBitacora;
import logica.utilidades.SesionUsuarioSingleton;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static gui.controladores.NavegacionControlador.regresar;

public class FormularioUsuarioControlador implements Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(FormularioUsuarioControlador.class.getName());

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidoP;
    @FXML private TextField txtApellidoM;
    @FXML private PasswordField txtContrasenia;
    @FXML private ComboBox<TipoDeUsuario> cbTipoUsuario;
    @FXML private VBox contenedorDinamico;
    @FXML private Label lblError;

    private ProfesorControlador profesorControlador;
    private PracticanteControlador practicanteControlador;
    private CoordinadorControlador coordinadorControlador;
    private Object controladorHijo;
    private Scene escenaAnterior;

    private static final Map<TipoDeUsuario, String> FRAGMENTO_POR_TIPO = Map.of(
            TipoDeUsuario.PROFESOR, "Profesor",
            TipoDeUsuario.PRACTICANTE, "Practicante",
            TipoDeUsuario.COORDINADOR, "Coordinador"
    );

    @FXML
    public void initialize() {
        try {
            profesorControlador = new ProfesorControlador();
            practicanteControlador = new PracticanteControlador();
            coordinadorControlador = new CoordinadorControlador();
        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al inicializar controladores", excepcionCapturada);
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo inicializar el sistema.");
        }

        cargarTiposPermitidos();

        cbTipoUsuario.getSelectionModel().selectedItemProperty().addListener(
                (observador, valorAnterior, valorNuevo) -> {
                    if (valorNuevo != null) {
                        cambiarFragmento(valorNuevo);
                    }
                }
        );

        lblError.setVisible(false);
    }

    private void cambiarFragmento(TipoDeUsuario tipoUsuarioRecibido) {
        String nombreTipo = FRAGMENTO_POR_TIPO.get(tipoUsuarioRecibido);
        if (nombreTipo == null) {
            throw new IllegalArgumentException("Tipo de usuario no soportado: " + tipoUsuarioRecibido);
        }

        try {
            contenedorDinamico.getChildren().clear();
            String rutaArchivoFxml = "/gui/vista/FXMLFragmento" + nombreTipo + ".fxml";
            FXMLLoader cargadorDeVista = new FXMLLoader(getClass().getResource(rutaArchivoFxml));
            Node vistaFragmento = cargadorDeVista.load();
            contenedorDinamico.getChildren().add(vistaFragmento);
            controladorHijo = cargadorDeVista.getController();
        } catch (IOException excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al cargar fragmento", excepcionCapturada);
            mostrarErrorEnLinea("Error al cargar el formulario dinámico.");
        }
    }

    private void cargarTiposPermitidos() {
        TipoDeUsuario rolActual = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getTipoDeUsuario();
        PermisosRol permisos = new PermisosRol(rolActual);

        if (permisos.puedeAgregarCoordinador()) {
            cbTipoUsuario.getItems().add(TipoDeUsuario.COORDINADOR);
        }
        if (permisos.puedeAgregarProfesor()) {
            cbTipoUsuario.getItems().add(TipoDeUsuario.PROFESOR);
        }
        if (permisos.puedeAgregarPracticante()) {
            cbTipoUsuario.getItems().add(TipoDeUsuario.PRACTICANTE);
        }
    }

    @FXML
    private void manejarGuardar(ActionEvent eventoBoton) {
        lblError.setVisible(false);

        if (validarCamposFormulario()) {
            TipoDeUsuario tipoSeleccionado = cbTipoUsuario.getValue();
            if (tipoSeleccionado == TipoDeUsuario.PROFESOR) {
                guardarProfesor();
            } else if (tipoSeleccionado == TipoDeUsuario.PRACTICANTE) {
                guardarPracticante();
            } else if (tipoSeleccionado == TipoDeUsuario.COORDINADOR) {
                guardarCoordinador();
            }
        }
    }

    private boolean validarCamposFormulario() {
        if (cbTipoUsuario.getValue() == null) {
            mostrarErrorEnLinea("Debe seleccionar un tipo de usuario.");
            return false;
        }
        if (controladorHijo == null) {
            mostrarErrorEnLinea("Error de carga en formulario.");
            return false;
        }
        try {
            UsuarioControlador.validarCamposComunes(
                    txtNombre.getText(),
                    txtApellidoP.getText(),
                    txtApellidoM.getText(),
                    txtContrasenia.getText()
            );
            return true;
        } catch (ReglaDeNegocioExcepcion excepcionCapturada) {
            mostrarErrorEnLinea(excepcionCapturada.getMessage());
            return false;
        }
    }

    private void guardarProfesor() {
        CamposProfesorControlador camposHijo = (CamposProfesorControlador) controladorHijo;
        try {
            ProfesorDTO profesorNuevo = profesorControlador.construirProfesorDTO(
                    0,
                    txtNombre.getText().trim(),
                    txtApellidoP.getText().trim(),
                    txtApellidoM.getText().trim(),
                    txtContrasenia.getText(),
                    camposHijo.getNumeroPersonal(),
                    camposHijo.getTurno(),
                    camposHijo.getSeccion()
            );
            profesorControlador.procesarGuardadoProfesor(profesorNuevo, false);
            RegistradorBitacora.registrar("REGISTRO_PROFESOR", "Registró al profesor: " + profesorNuevo.getNumeroDePersonal());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Profesor registrado correctamente.");
            regresar(lblError, escenaAnterior);
        } catch (ReglaDeNegocioExcepcion | DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al guardar profesor", excepcionCapturada);
            mostrarErrorEnLinea(excepcionCapturada.getMessage());
        }
    }

    private void guardarPracticante() {
        CamposPracticanteControlador camposHijo = (CamposPracticanteControlador) controladorHijo;
        try {
            PracticanteDTO practicanteNuevo = practicanteControlador.construirPracticanteDTO(
                    0,
                    txtNombre.getText().trim(),
                    txtApellidoP.getText().trim(),
                    txtApellidoM.getText().trim(),
                    txtContrasenia.getText(),
                    camposHijo.getMatricula(),
                    camposHijo.getIdSeccion(),
                    camposHijo.getSemestre(),
                    camposHijo.getGenero(),
                    camposHijo.getEdad(),
                    camposHijo.isLenguaIndigena()
            );
            practicanteControlador.procesarGuardadoPracticante(practicanteNuevo, false);
            RegistradorBitacora.registrar("REGISTRO_PRACTICANTE", "Registró al practicante: " + practicanteNuevo.getMatricula());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Practicante registrado correctamente.");
            regresar(lblError, escenaAnterior);
        } catch (ReglaDeNegocioExcepcion | DAOExcepcion | NumberFormatException excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al guardar practicante", excepcionCapturada);
            mostrarErrorEnLinea("Verifique los datos del practicante.");
        }
    }

    private void guardarCoordinador() {
        CamposCoordinadorControlador camposHijo = (CamposCoordinadorControlador) controladorHijo;
        try {
            CoordinadorDTO coordinadorNuevo = coordinadorControlador.construirCoordinadorDTO(
                    0,
                    txtNombre.getText().trim(),
                    txtApellidoP.getText().trim(),
                    txtApellidoM.getText().trim(),
                    txtContrasenia.getText(),
                    camposHijo.getNumeroPersonal()
            );
            coordinadorControlador.procesarGuardadoCoordinador(coordinadorNuevo, false);
            RegistradorBitacora.registrar("REGISTRO_COORDINADOR", "Registró al coordinador: " + coordinadorNuevo.getNumeroPersonal());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Coordinador registrado correctamente.");
            regresar(lblError, escenaAnterior);
        } catch (ReglaDeNegocioExcepcion | DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al guardar coordinador", excepcionCapturada);
            mostrarErrorEnLinea(excepcionCapturada.getMessage());
        }
    }

    private void mostrarErrorEnLinea(String mensajeError) {
        lblError.setText(mensajeError);
        lblError.setVisible(true);
    }

    private void mostrarAlerta(Alert.AlertType tipoAlerta, String tituloAlerta, String mensajeAlerta) {
        Alert ventanaAlerta = new Alert(tipoAlerta);
        ventanaAlerta.setTitle(tituloAlerta);
        ventanaAlerta.setHeaderText(null);
        ventanaAlerta.setContentText(mensajeAlerta);
        ventanaAlerta.showAndWait();
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void manejarSalir(ActionEvent eventoBoton) {
        Node nodoOrigen = (Node) eventoBoton.getSource();
        regresar(nodoOrigen, this.escenaAnterior);
    }
}