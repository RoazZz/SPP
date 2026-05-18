package gui.controladores;

import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnviarMensajeControlador implements Initializable {

    private static final Logger logger = Logger.getLogger(EnviarMensajeControlador.class.getName());

    @FXML private TextField txtBuscarDestinatario;
    @FXML private ComboBox<UsuarioDTO> cbDestinatario;
    @FXML private TextField txtAsunto;
    @FXML private TextArea  txtMensaje;
    @FXML private Label     lblMensaje;

    private ObservableList<UsuarioDTO> todosLosDestinatarios = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarDestinatarios();
        configurarComboBox();
        configurarBusqueda();
    }

    private void cargarDestinatarios() {
        try {
            int idUsuarioActual = SesionUsuarioSingleton.obtenerInstancia()
                    .obtenerUsuarioActual().getIdUsuario();

            PracticanteDAO practicanteDAO = new PracticanteDAO();
            for (PracticanteDTO practicante : practicanteDAO.listarPracticantes()) {
                if (practicante.getIdUsuario() != idUsuarioActual) {
                    todosLosDestinatarios.add(practicante);
                }
            }

            CoordinadorDAO coordinadorDAO = new CoordinadorDAO();
            for (CoordinadorDTO coordinador : coordinadorDAO.listarCoordinador()) {
                if (coordinador.getIdUsuario() != idUsuarioActual) {
                    todosLosDestinatarios.add(coordinador);
                }
            }

            ProfesorDAO profesorDAO = new ProfesorDAO();
            for (ProfesorDTO profesor : profesorDAO.listarProfesores()) {
                if (profesor.getIdUsuario() != idUsuarioActual) {
                    todosLosDestinatarios.add(profesor);
                }
            }

            AdministradorDAO administradorDAO = new AdministradorDAO();
            for (AdministradorDTO administrador : administradorDAO.listarAdministradores()) {
                if (administrador.getIdUsuario() != idUsuarioActual) {
                    todosLosDestinatarios.add(administrador);
                }
            }

            cbDestinatario.setItems(todosLosDestinatarios);
            logger.log(Level.INFO, "Destinatarios cargados correctamente");
        } catch (DAOExcepcion e) {
            logger.log(Level.SEVERE, "Error al cargar destinatarios", e);
            lblMensaje.setText("Error al cargar la lista de destinatarios.");
        }
    }

    private void configurarComboBox() {
        cbDestinatario.setCellFactory(lista -> new ListCell<>() {
            @Override
            protected void updateItem(UsuarioDTO usuario, boolean vacio) {
                super.updateItem(usuario, vacio);
                if (vacio || usuario == null) {
                    setText(null);
                } else {
                    setText(obtenerEtiqueta(usuario));
                }
            }
        });

        cbDestinatario.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(UsuarioDTO usuario, boolean vacio) {
                super.updateItem(usuario, vacio);
                if (vacio || usuario == null) {
                    setText(null);
                } else {
                    setText(obtenerEtiqueta(usuario));
                }
            }
        });
    }

    private void configurarBusqueda() {
        txtBuscarDestinatario.textProperty().addListener((observable, anterior, nuevo) -> {
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
        });
    }

    private String obtenerEtiqueta(UsuarioDTO usuario) {
        String nombreCompleto = usuario.getNombre() + " "
                + usuario.getApellidoPaterno() + " "
                + usuario.getApellidoMaterno();

        if (usuario instanceof PracticanteDTO) {
            return nombreCompleto + " - " + ((PracticanteDTO) usuario).getMatricula() + " (Practicante)";
        } else if (usuario instanceof CoordinadorDTO) {
            return nombreCompleto + " - " + ((CoordinadorDTO) usuario).getNumeroPersonal() + " (Coordinador)";
        } else if (usuario instanceof ProfesorDTO) {
            return nombreCompleto + " - " + ((ProfesorDTO) usuario).getNumeroDePersonal() + " (Profesor)";
        } else if (usuario instanceof AdministradorDTO) {
            return nombreCompleto + " - " + ((AdministradorDTO) usuario).getIdAdministrador() + " (Administrador)";
        }
        return nombreCompleto;
    }

    @FXML
    private void enviar() {
        UsuarioDTO destinatario = cbDestinatario.getValue();

        if (destinatario == null) {
            lblMensaje.setText("Selecciona un destinatario.");
            return;
        }
        if (txtAsunto.getText().isBlank()) {
            lblMensaje.setText("El asunto no puede estar vacío.");
            return;
        }
        if (txtMensaje.getText().isBlank()) {
            lblMensaje.setText("El mensaje no puede estar vacío.");
            return;
        }

        try {
            int idUsuarioOrigen = SesionUsuarioSingleton.obtenerInstancia()
                    .obtenerUsuarioActual().getIdUsuario();

            BuzonDAO buzonDAO = new BuzonDAO();
            BuzonDTO buzonOrigen = buzonDAO.obtenerBuzonPorIdUsuario(idUsuarioOrigen);
            BuzonDTO buzonDestino = buzonDAO.obtenerBuzonPorIdUsuario(destinatario.getIdUsuario());

            MensajeDTO mensaje = new MensajeDTO(
                    buzonOrigen.getIdBuzon(),
                    buzonDestino.getIdBuzon(),
                    txtAsunto.getText().trim(),
                    txtMensaje.getText().trim()
            );

            MensajeDAO mensajeDAO = new MensajeDAO();
            mensajeDAO.insertarMensaje(mensaje);

            lblMensaje.setStyle("-fx-text-fill: green;");
            lblMensaje.setText("Mensaje enviado correctamente.");
            limpiar();
            logger.log(Level.INFO, "Mensaje enviado correctamente");
        } catch (DAOExcepcion | EntidadNoEncontradaExcepcion e) {
            logger.log(Level.SEVERE, "Error al enviar el mensaje", e);
            lblMensaje.setText("Error al enviar el mensaje.");
        }
    }

    @FXML
    private void limpiar() {
        txtBuscarDestinatario.clear();
        cbDestinatario.setItems(todosLosDestinatarios);
        cbDestinatario.setValue(null);
        txtAsunto.clear();
        txtMensaje.clear();
        lblMensaje.setText("");
        lblMensaje.setStyle("");
    }

    @FXML
    private void salir() {
        ((Stage) txtAsunto.getScene().getWindow()).close();
    }
}