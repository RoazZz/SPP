package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.ActividadDAO;
import logica.dto.ActividadDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaActividadDAO {

    private static final int ID_USUARIO_PRUEBA = 910;
    private static final int ID_SECCION_PRUEBA = 910;
    private static final String MATRICULA_PRUEBA = "S20009100";

    private static ActividadDAO actividadDAO;
    private ActividadDTO actividadValida;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        actividadDAO = new ActividadDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM seccion WHERE idSeccion = 910");
            statement.execute("INSERT INTO seccion (idSeccion, Nombre) VALUES (910, 'Seccion Base')");
            statement.execute("DELETE FROM usuario WHERE idUsuario = 910");
            statement.execute("INSERT INTO usuario (idUsuario, Nombre, ApellidoP, ApellidoM, Contrasenia, Estado, TipoUsuario) VALUES (910, 'PracBase', 'Ap', 'Am', 'clave', 'ACTIVO', 'PRACTICANTE')");
            statement.execute("DELETE FROM practicante WHERE Matricula = 'S20009100'");
            statement.execute("INSERT INTO practicante (Matricula, idSeccion, Semestre, Genero, Edad, LenguaIndigena, idUsuario) VALUES ('S20009100', 910, '5', 'MASCULINO', 22, 0, 910)");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM actividad WHERE Matricula = '" + MATRICULA_PRUEBA + "'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        actividadValida = new ActividadDTO(0, MATRICULA_PRUEBA, "Actividad Prueba", "Descripcion", LocalDate.of(2026, 4, 17), "ruta/doc.pdf");
    }

    @Test
    public void pruebaRegistrarActividadExitoso() throws Exception {
        boolean resultado = actividadDAO.registrarActividad(actividadValida);
        assertTrue(resultado);
    }

    @Test
    public void pruebaActualizarActividadExitoso() throws Exception {
        actividadDAO.registrarActividad(actividadValida);
        actividadValida.setTitulo("Actividad Modificada");
        actividadDAO.actualizarActividad(actividadValida);
        ActividadDTO actividadRecuperada = actividadDAO.buscarActividadPorIdActividad(actividadValida.getIdActividad());
        assertEquals("Actividad Modificada", actividadRecuperada.getTitulo());
    }

    @Test
    public void pruebaBuscarActividadPorIdActividadExitoso() throws Exception {
        actividadDAO.registrarActividad(actividadValida);
        ActividadDTO actividadRecuperada = actividadDAO.buscarActividadPorIdActividad(actividadValida.getIdActividad());
        assertEquals(actividadValida.getIdActividad(), actividadRecuperada.getIdActividad());
    }

    @Test
    public void pruebaListarActividadesPorMatriculaExitoso() throws Exception {
        actividadDAO.registrarActividad(actividadValida);
        List<ActividadDTO> actividadesRecuperadas = actividadDAO.listarActividadesPorMatricula(MATRICULA_PRUEBA);
        assertFalse(actividadesRecuperadas.isEmpty());
    }

    @Test
    public void pruebaListarActividadesExitoso() throws Exception {
        actividadDAO.registrarActividad(actividadValida);
        List<ActividadDTO> actividadesRecuperadas = actividadDAO.listarActividades();
        assertFalse(actividadesRecuperadas.isEmpty());
    }

    @Test
    public void pruebaBuscarActividadPorIdActividadExcepcionNoEncontrado() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () ->
                actividadDAO.buscarActividadPorIdActividad(-1));
    }

    @Test
    public void pruebaRegistrarActividadExcepcionMatriculaNula() {
        actividadValida.setMatricula(null);
        assertThrows(DAOExcepcion.class, () ->
                actividadDAO.registrarActividad(actividadValida));
    }
}