package pruebasdao;

import accesodatos.ConexionBD;
import excepciones.DAOExcepcion;
import excepciones.EntidadNoEncontradaExcepcion;
import logica.dao.PracticanteDAO;
import logica.dto.PracticanteDTO;
import logica.enums.GeneroDelPracticante;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstadoUsuario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PruebaPracticanteDAO {

    private static final int ID_SECCION_PRUEBA = 904;
    private static final String MATRICULA_PRUEBA = "S20009040";

    private static PracticanteDAO practicanteDAO;
    private PracticanteDTO practicanteValido;

    @BeforeAll
    static void prepararEntorno() throws Exception {
        System.setProperty("db.enlace", "jdbc:mysql://localhost:3306/spptest1");
        System.setProperty("db.usuario", "testuser");
        System.setProperty("db.contrasenia", "testpass123");
        ConexionBD.reset();
        practicanteDAO = new PracticanteDAO();
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM seccion WHERE idSeccion = " + ID_SECCION_PRUEBA);
            statement.execute("INSERT INTO seccion (idSeccion, Nombre) VALUES (" + ID_SECCION_PRUEBA + ", 'Seccion Practicante')");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
    }

    @BeforeEach
    void prepararObjetosYLimpiar() throws Exception {
        Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();
        try (Statement statement = conexion.createStatement()) {
            statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            statement.execute("DELETE FROM practicante WHERE Matricula = '" + MATRICULA_PRUEBA + "'");
            statement.execute("DELETE FROM usuario WHERE Nombre = 'PracticantePrueba'");
            statement.execute("SET FOREIGN_KEY_CHECKS = 1");
        }
        practicanteValido = new PracticanteDTO(
                0,
                "PracticantePrueba",
                "Apellido",
                "Materno",
                "clave12345",
                TipoEstadoUsuario.ACTIVO,
                TipoDeUsuario.PRACTICANTE
        );
        practicanteValido.setMatricula(MATRICULA_PRUEBA);
        practicanteValido.setIdSeccion(ID_SECCION_PRUEBA);
        practicanteValido.setSemestre("5");
        practicanteValido.setGeneroDelPracticante(GeneroDelPracticante.MASCULINO);
        practicanteValido.setEdad(22);
        practicanteValido.setLenguaIndigena(false);
    }

    @Test
    public void pruebaAgregarPracticanteExitoso() throws Exception {
        boolean resultado = practicanteDAO.agregarPracticante(practicanteValido);
        assertTrue(resultado);
    }

    @Test
    public void pruebaActualizarPracticanteExitoso() throws Exception {
        practicanteDAO.agregarPracticante(practicanteValido);
        practicanteValido.setSemestre("8");
        practicanteDAO.actualizarPracticante(practicanteValido);
        PracticanteDTO practicanteRecuperado = practicanteDAO.buscarPracticantePorMatricula(MATRICULA_PRUEBA);
        assertEquals("8", practicanteRecuperado.getSemestre());
    }

    @Test
    public void pruebaBuscarPracticantePorMatriculaExitoso() throws Exception {
        practicanteDAO.agregarPracticante(practicanteValido);
        PracticanteDTO practicanteRecuperado = practicanteDAO.buscarPracticantePorMatricula(MATRICULA_PRUEBA);
        assertEquals(MATRICULA_PRUEBA, practicanteRecuperado.getMatricula());
    }

    @Test
    public void pruebaListarPracticantesExitoso() throws Exception {
        practicanteDAO.agregarPracticante(practicanteValido);
        List<PracticanteDTO> practicantesRecuperados = practicanteDAO.listarPracticantes();
        assertFalse(practicantesRecuperados.isEmpty());
    }

    @Test
    public void pruebaListarPracticantesPorSeccionExitoso() throws Exception {
        practicanteDAO.agregarPracticante(practicanteValido);
        List<PracticanteDTO> practicantesRecuperados = practicanteDAO.listarPracticantesPorSeccion(ID_SECCION_PRUEBA);
        assertFalse(practicantesRecuperados.isEmpty());
    }

    @Test
    public void pruebaExistePracticanteConMatriculaExitoso() throws Exception {
        practicanteDAO.agregarPracticante(practicanteValido);
        boolean existe = practicanteDAO.existePracticanteConMatricula(MATRICULA_PRUEBA);
        assertTrue(existe);
    }

    @Test
    public void pruebaBuscarPracticantePorMatriculaExcepcionNoEncontrado() {
        assertThrows(EntidadNoEncontradaExcepcion.class, () ->
                practicanteDAO.buscarPracticantePorMatricula("NOEXISTE00"));
    }

    @Test
    public void pruebaAgregarPracticanteExcepcionMatriculaNula() {
        practicanteValido.setMatricula(null);
        assertThrows(DAOExcepcion.class, () ->
                practicanteDAO.agregarPracticante(practicanteValido));
    }
}