package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.ReglaDeNegocioExcepcion;
import javafx.event.ActionEvent;
import logica.interfaces.Regresable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import logica.dto.ProyectoDTO;
import logica.utilidades.RegistradorBitacora;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static gui.controladores.NavegacionControlador.regresar;

public class FormularioProyectoControlador implements Initializable, Regresable {

    private static final Logger REGISTRADOR = Logger.getLogger(FormularioProyectoControlador.class.getName());

    @FXML private Label lblTitulo;
    @FXML private TextField txtIdOrganizacionVinculada;
    @FXML private TextField txtNumeroDePersonal;
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private Label lblMensaje;

    private Scene escenaAnterior;
    private ProyectoControlador proyectoControlador;
    private ProyectoDTO proyectoEnEdicion = null;
    private ProyectosControlador controladorPadre;

    @Override
    public void initialize(URL urlRecibida, ResourceBundle recursoRecibido) {
        try {
            proyectoControlador = new ProyectoControlador();
        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al inicializar ProyectoControlador", excepcionCapturada);
            mostrarMensaje("Error al conectar con la base de datos.", false);
        }
    }

    public void cargarProyecto(ProyectoDTO proyectoRecibido) {
        this.proyectoEnEdicion = proyectoRecibido;
        txtIdOrganizacionVinculada.setText(proyectoRecibido.getIdOrganizacion());
        txtIdOrganizacionVinculada.setEditable(false);
        txtNumeroDePersonal.setText(proyectoRecibido.getNumeroDePersonal());
        txtNumeroDePersonal.setEditable(false);
        txtNombre.setText(proyectoRecibido.getNombre());
        txtDescripcion.setText(proyectoRecibido.getDescripcion());
    }

    @FXML
    private void manejarGuardar(ActionEvent eventoClic) {
        if (validarCamposVacios()) {
            procesarGuardado();
        }
    }

    private void procesarGuardado() {
        try {
            boolean modoEdicion = false;
            int idProyecto = 0;

            if (proyectoEnEdicion != null) {
                modoEdicion = true;
                idProyecto = proyectoEnEdicion.getIdProyecto();
            }

            ProyectoDTO proyectoValidado = new ProyectoDTO(
                    idProyecto,
                    txtIdOrganizacionVinculada.getText().trim(),
                    txtNumeroDePersonal.getText().trim(),
                    txtNombre.getText().trim(),
                    txtDescripcion.getText().trim()
            );

            proyectoControlador.procesarGuardadoProyecto(proyectoValidado, modoEdicion);

            String mensajeExito = "Proyecto guardado con éxito.";
            if (modoEdicion) {
                mensajeExito = "Proyecto actualizado con éxito.";
            }

            mostrarMensaje(mensajeExito, true);

            if (!modoEdicion) {
                limpiarCampos();
            }

            REGISTRADOR.log(Level.INFO, "Proyecto guardado correctamente");
            RegistradorBitacora.registrar("REGISTRO_PROYECTO", "Registró el proyecto: " + proyectoValidado.getNombre());

            if (controladorPadre != null) {
                controladorPadre.cargarProyectos();
            }
        } catch (ReglaDeNegocioExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.WARNING, "Regla de negocio violada al guardar proyecto", excepcionCapturada);
            mostrarMensaje(excepcionCapturada.getMessage(), false);
        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error de base de datos al guardar proyecto", excepcionCapturada);
            mostrarMensaje("Error al guardar el proyecto. Intente de nuevo.", false);
        }
    }

    private boolean validarCamposVacios() {
        StringBuilder camposVacios = new StringBuilder();
        if (txtIdOrganizacionVinculada.getText().trim().isEmpty()) {
            camposVacios.append("Id Organización Vinculada es obligatorio.\n");
        }
        if (txtNumeroDePersonal.getText().trim().isEmpty()) {
            camposVacios.append("Número de Personal es obligatorio.\n");
        }
        if (txtNombre.getText().trim().isEmpty()) {
            camposVacios.append("Nombre es obligatorio.\n");
        }
        if (txtDescripcion.getText().trim().isEmpty()) {
            camposVacios.append("Descripción es obligatoria.\n");
        }

        boolean esValido = true;
        if (camposVacios.length() > 0) {
            mostrarMensaje(camposVacios.toString(), false);
            esValido = false;
        } else {
            lblMensaje.setText("");
        }
        return esValido;
    }

    private void limpiarCampos() {
        txtIdOrganizacionVinculada.clear();
        txtNumeroDePersonal.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        proyectoEnEdicion = null;
    }

    private void mostrarMensaje(String mensajeParaMostrar, boolean esExitoso) {
        lblMensaje.setText(mensajeParaMostrar);
        lblMensaje.getStyleClass().removeAll("label-exito", "label-error");

        if (esExitoso) {
            lblMensaje.getStyleClass().add("label-exito");
        } else {
            lblMensaje.getStyleClass().add("label-error");
        }
    }

    public void setControladorPadre(ProyectosControlador controladorRecibido) {
        this.controladorPadre = controladorRecibido;
    }

    public void configurarTitulo(String tituloNuevo) {
        lblTitulo.setText(tituloNuevo);
    }

    @Override
    public void setEscenaAnterior(Scene escenaGuardada) {
        this.escenaAnterior = escenaGuardada;
    }

    @FXML
    private void manejarSalir(ActionEvent eventoClic) {
        if (controladorPadre != null) {
            controladorPadre.cargarProyectos();
        }
        Node nodoOrigen = (Node) eventoClic.getSource();
        regresar(nodoOrigen, this.escenaAnterior);
    }
}