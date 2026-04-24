package gui.controladores;

import accesodatos.ConexionBD;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class PruebaFormularioUsuarioControlador extends PruebaBaseGUI {

    private static final long PRUEBA_PAUSA_VISUAL_MS = 800;

    private static final String PRUEBA_NOMBRE_PROFESOR = "NombreProfPrueba";
    private static final String PRUEBA_APELLIDO_P_PROFESOR = "ApellidoPPrueba";
    private static final String PRUEBA_APELLIDO_M_PROFESOR = "ApellidoMPrueba";
    private static final String PRUEBA_CONTRASENIA = "contraseniaPrueba123";
    private static final String PRUEBA_NUMERO_DE_PERSONAL = "999999999999";
    private static final String PRUEBA_TURNO = "MATUTINO";

    private PruebaUtilDatos pruebaUtilDatos;

    @Override
    public void start(Stage escenario) throws Exception {
        FXMLLoader cargador = new FXMLLoader(
                getClass().getResource("/gui/vista/FXMLFormularioUsuario.fxml"));
        Parent raiz = cargador.load();
        escenario.setScene(new Scene(raiz));
        escenario.show();
    }

    @BeforeEach
    void prepararDatosPrueba() {
        pruebaUtilDatos = new PruebaUtilDatos();
    }

    @AfterEach
    void limpiarDatosPrueba() throws Exception {
        eliminarProfesorPruebaPorNumeroDePersonal(PRUEBA_NUMERO_DE_PERSONAL);
        pruebaUtilDatos.limpiarTodosLosDatosPrueba();
    }

    @Test
    void formularioGuardaProfesorYQuedaPersistidoEnBaseDeDatos() throws Exception {
        FxRobot robot = new FxRobot();

        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        llenarFormularioProfesorPrueba(robot);
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("GUARDAR DATOS");
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);

        assertTrue(existeProfesorPruebaConNumeroDePersonal(PRUEBA_NUMERO_DE_PERSONAL));
    }

    @Test
    void formularioGuardaNombreCorrectoDelProfesor() throws Exception {
        FxRobot robot = new FxRobot();

        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        llenarFormularioProfesorPrueba(robot);
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("GUARDAR DATOS");
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);

        String nombrePersistido = obtenerNombreProfesorPruebaPorNumeroDePersonal(PRUEBA_NUMERO_DE_PERSONAL);
        assertEquals(PRUEBA_NOMBRE_PROFESOR, nombrePersistido);
    }

    @Test
    void formularioGuardaApellidoPaternoCorrectoDelProfesor() throws Exception {
        FxRobot robot = new FxRobot();

        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        llenarFormularioProfesorPrueba(robot);
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("GUARDAR DATOS");
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);

        String apellidoPersistido = obtenerApellidoPaternoProfesorPruebaPorNumeroDePersonal(PRUEBA_NUMERO_DE_PERSONAL);
        assertEquals(PRUEBA_APELLIDO_P_PROFESOR, apellidoPersistido);
    }

    private void llenarFormularioProfesorPrueba(FxRobot robot) {
        robot.clickOn("#cbTipoUsuario");
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("PROFESOR");
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);

        robot.clickOn("#txtNombre").write(PRUEBA_NOMBRE_PROFESOR);
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("#txtApellidoP").write(PRUEBA_APELLIDO_P_PROFESOR);
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("#txtApellidoM").write(PRUEBA_APELLIDO_M_PROFESOR);
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("#txtContrasenia").write(PRUEBA_CONTRASENIA);
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);

        robot.clickOn("#txtNumeroPersonal").write(PRUEBA_NUMERO_DE_PERSONAL);
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn("#cbTurno");
        pausarMilisegundos(PRUEBA_PAUSA_VISUAL_MS);
        robot.clickOn(PRUEBA_TURNO);
    }

    private boolean existeProfesorPruebaConNumeroDePersonal(String numeroDePersonal) throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        String sql = "SELECT COUNT(*) FROM Profesor WHERE NumeroDePersonal = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, numeroDePersonal);
            try (ResultSet resultado = sentencia.executeQuery()) {
                resultado.next();
                return resultado.getInt(1) > 0;
            }
        }
    }

    private String obtenerNombreProfesorPruebaPorNumeroDePersonal(String numeroDePersonal) throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        String sql = "SELECT u.Nombre FROM Usuario u " +
                "INNER JOIN Profesor p ON u.idUsuario = p.idUsuario " +
                "WHERE p.NumeroDePersonal = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, numeroDePersonal);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getString("Nombre");
                }
                return null;
            }
        }
    }

    private String obtenerApellidoPaternoProfesorPruebaPorNumeroDePersonal(String numeroDePersonal) throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        String sql = "SELECT u.ApellidoP FROM Usuario u " +
                "INNER JOIN Profesor p ON u.idUsuario = p.idUsuario " +
                "WHERE p.NumeroDePersonal = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, numeroDePersonal);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getString("ApellidoP");
                }
                return null;
            }
        }
    }

    private void eliminarProfesorPruebaPorNumeroDePersonal(String numeroDePersonal) throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();

        Integer idUsuario = null;
        String sqlBuscar = "SELECT idUsuario FROM Profesor WHERE NumeroDePersonal = ?";
        try (PreparedStatement sentencia = conexion.prepareStatement(sqlBuscar)) {
            sentencia.setString(1, numeroDePersonal);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    idUsuario = resultado.getInt("idUsuario");
                }
            }
        }
        if (idUsuario == null) return;

        try (PreparedStatement sentencia = conexion.prepareStatement(
                "DELETE FROM Profesor WHERE idUsuario = ?")) {
            sentencia.setInt(1, idUsuario);
            sentencia.executeUpdate();
        }
        try (PreparedStatement sentencia = conexion.prepareStatement(
                "DELETE FROM Usuario WHERE idUsuario = ?")) {
            sentencia.setInt(1, idUsuario);
            sentencia.executeUpdate();
        }
    }
}