package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import logica.dao.AdministradorDAO;
import logica.dao.BuzonDAO;
import logica.dao.CoordinadorDAO;
import logica.dao.MensajeDAO;
import logica.dao.PracticanteDAO;
import logica.dao.ProfesorDAO;
import logica.dto.AdministradorDTO;
import logica.dto.BuzonDTO;
import logica.dto.CoordinadorDTO;
import logica.dto.MensajeDTO;
import logica.dto.PracticanteDTO;
import logica.dto.ProfesorDTO;
import logica.dto.UsuarioDTO;
import logica.utilidades.SesionUsuarioSingleton;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnviarMensajeControlador implements Initializable {

    private static final Logger REGISTRADOR = Logger.getLogger(EnviarMensajeControlador.class.getName());

    @FXML private TextField txtBuscarDestinatario;
    @FXML private ComboBox<UsuarioDTO> cbDestinatario;
    @FXML private TextField txtAsunto;
    @FXML private TextArea txtMensaje;
    @FXML private Label lblMensaje;

    private ObservableList<UsuarioDTO> todosLosDestinatarios = FXCollections.observableArrayList();

    @Override
    public void initialize(URL urlRecibida, ResourceBundle recursoRecibido) {
        cargarDestinatarios();
        configurarComboBox();
        configurarBusqueda();
    }

    private void cargarDestinatarios() {
        try {
            int idUsuarioActual = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getIdUsuario();

            PracticanteDAO practicanteDAO = new PracticanteDAO();
            List<PracticanteDTO> listaPracticantes = practicanteDAO.listarPracticantes();
            for (PracticanteDTO practicante : listaPracticantes) {
                if (practicante.getIdUsuario() != idUsuarioActual) {
                    todosLosDestinatarios.add(practicante);
                }
            }

            CoordinadorDAO coordinadorDAO = new CoordinadorDAO();
            List<CoordinadorDTO> listaCoordinadores = coordinadorDAO.listarCoordinador();
            for (CoordinadorDTO coordinador : listaCoordinadores) {
                if (coordinador.getIdUsuario() != idUsuarioActual) {
                    todosLosDestinatarios.add(coordinador);
                }
            }

            ProfesorDAO profesorDAO = new ProfesorDAO();
            List<ProfesorDTO> listaProfesores = profesorDAO.listarProfesores();
            for (ProfesorDTO profesor : listaProfesores) {
                if (profesor.getIdUsuario() != idUsuarioActual) {
                    todosLosDestinatarios.add(profesor);
                }
            }

            AdministradorDAO administradorDAO = new AdministradorDAO();
            List<AdministradorDTO> listaAdministradores = administradorDAO.listarAdministradores();
            for (AdministradorDTO administrador : listaAdministradores) {
                if (administrador.getIdUsuario() != idUsuarioActual) {
                    todosLosDestinatarios.add(administrador);
                }
            }

            cbDestinatario.setItems(todosLosDestinatarios);
        } catch (DAOExcepcion excepcionCapturada) {
            REGISTRADOR.log(Level.SEVERE, "Error al cargar destinatarios", excepcionCapturada);
            lblMensaje.getStyleClass().add("label-error");
            lblMensaje.setText("Error al cargar la lista de destinatarios.");
        }
    }

    private void configurarComboBox() {
        Callback<ListView<UsuarioDTO>, ListCell<UsuarioDTO>> factoryCelda = new Callback<>() {
            @Override
            public ListCell<UsuarioDTO> call(ListView<UsuarioDTO> lista) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(UsuarioDTO usuario, boolean vacio) {
                        super.updateItem(usuario, vacio);
                        if (vacio || usuario == null) {
                            setText(null);
                        } else {
                            setText(obtenerEtiqueta(usuario));
                        }
                    }
                };
            }
        };
        cbDestinatario.setCellFactory(factoryCelda);
        cbDestinatario.setButtonCell(factoryCelda.call(null));
    }

    private void configurarBusqueda() {
        txtBuscarDestinatario.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String anterior, String nuevo) {
                if (nuevo == null || nuevo.isBlank()) {
                    cbDestinatario.setItems(todosLosDestinatarios);
                } else {
                    String busqueda = nuevo.toLowerCase();
                    ObservableList<UsuarioDTO> filtrados = FXCollections.observableArrayList();
                    for (UsuarioDTO usuario : todosLosDestinatarios) {
                        if (obtenerEtiqueta(usuario).toLowerCase().contains(busqueda)) {
                            filtrados.add(usuario);
                        }
                    }
                    cbDestinatario.setItems(filtrados);
                }
            }
        });
    }

    private String obtenerEtiqueta(UsuarioDTO usuario) {
        String nombreCompleto = usuario.getNombre() + " " + usuario.getApellidoPaterno() + " " + usuario.getApellidoMaterno();
        String etiqueta = nombreCompleto;
        if (usuario instanceof PracticanteDTO practicante) {
            etiqueta = nombreCompleto + " - " + practicante.getMatricula() + " (Practicante)";
        } else if (usuario instanceof CoordinadorDTO coordinador) {
            etiqueta = nombreCompleto + " - " + coordinador.getNumeroPersonal() + " (Coordinador)";
        } else if (usuario instanceof ProfesorDTO profesor) {
            etiqueta = nombreCompleto + " - " + profesor.getNumeroDePersonal() + " (Profesor)";
        } else if (usuario instanceof AdministradorDTO administrador) {
            etiqueta = nombreCompleto + " - " + administrador.getIdAdministrador() + " (Administrador)";
        }
        return etiqueta;
    }

    @FXML
    private void manejarEnviar(ActionEvent eventoClic) {
        UsuarioDTO destinatario = cbDestinatario.getValue();
        if (destinatario == null) {
            lblMensaje.getStyleClass().add("label-error");
            lblMensaje.setText("Selecciona un destinatario.");
            return;
        }
        if (txtAsunto.getText().isBlank()) {
            lblMensaje.getStyleClass().add("label-error");
            lblMensaje.setText("El asunto no puede estar vacío.");
            return;
        }
        if (txtMensaje.getText().isBlank()) {
            lblMensaje.getStyleClass().add("label-error");
            lblMensaje.setText("El mensaje no puede estar vacío.");
            return;
        }
        try {
            int idUsuarioOrigen = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual().getIdUsuario();
            BuzonDAO buzonDAO = new BuzonDAO();
            BuzonDTO buzonOrigen = buzonDAO.obtenerBuzonPorIdUsuario(idUsuarioOrigen);
            BuzonDTO buzonDestino = buzonDAO.obtenerBuzonPorIdUsuario(destinatario.getIdUsuario());
            MensajeDTO mensaje = new MensajeDTO(buzonOrigen.getIdBuzon(), buzonDestino.getIdBuzon(), txtAsunto.getText().trim(), txtMensaje.getText().trim());
            new MensajeDAO().insertarMensaje(mensaje);
            lblMensaje.getStyleClass().remove("label-error");
            lblMensaje.getStyleClass().add("label-exito");
            lblMensaje.setText("Mensaje enviado correctamente.");
            manejarLimpiar(null);
        } catch (DAOExcepcion | EntidadNoEncontradaExcepcion excepcion) {
            REGISTRADOR.log(Level.SEVERE, "Error al enviar mensaje", excepcion);
            lblMensaje.getStyleClass().add("label-error");
            lblMensaje.setText("Error al enviar el mensaje.");
        }
    }

    @FXML
    private void manejarLimpiar(ActionEvent eventoClic) {
        txtBuscarDestinatario.clear();
        cbDestinatario.setItems(todosLosDestinatarios);
        cbDestinatario.setValue(null);
        txtAsunto.clear();
        txtMensaje.clear();
        if (eventoClic != null) {
            lblMensaje.setText("");
            lblMensaje.getStyleClass().removeAll("label-exito", "label-error");
        }
    }

    @FXML
    private void manejarCancelar(ActionEvent eventoClic) {
        Stage escenario = (Stage) txtAsunto.getScene().getWindow();
        escenario.close();
    }
}