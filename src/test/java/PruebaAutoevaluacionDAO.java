import logica.dao.AutoevaluacionDAO;
import logica.dto.AutoevaluacionDTO;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PruebaAutoevaluacionDAO {
    private static final Logger logger = Logger.getLogger(PruebaAutoevaluacionDAO.class.getName());

    private static final String MATRICULA_VALIDA   = "S23010001";
    private AutoevaluacionDAO dao;

    private int pruebasEjecutadas = 0;
    private int pruebasExitosas   = 0;

    public PruebaAutoevaluacionDAO() throws Exception {
        dao = new AutoevaluacionDAO();
    }

    //Caso valido
    public void pruebaAgregar() {
        pruebasEjecutadas++;
        String nombre = "pruebaAgregar";

        AutoevaluacionDTO dto = new AutoevaluacionDTO(0, MATRICULA_VALIDA, new BigDecimal("8.50"), "Comentario de prueba inicial");

        try {
            dao.agregar(dto);

            if (dto.getIdAutoevalaucion() <= 0) {
                logger.warning("FALLO: " + nombre + " La BD no devolvió un ID generado válido.");
                return;
            }

            logger.info("EXITO: " + nombre + " Registro insertado con id=" + dto.getIdAutoevalaucion());
            pruebasExitosas++;

        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "FALLO: " + nombre + " (caso válido) – Matrícula rechazada inesperadamente: " + e.getMessage(), e);
            return;
        } catch (Exception e) {
            logger.log(Level.SEVERE, ":FALLO: " + nombre + " (caso válido) – Error inesperado: " + e.getMessage(), e);
            return;
        }
    }

    public void pruebaBuscarPorMatricula() {
        pruebasEjecutadas++;
        String nombre = "pruebaBuscarPorMatricula";

        try {
            AutoevaluacionDTO resultado = dao.buscarPorMatricula(MATRICULA_VALIDA);

            if (resultado == null) {
                logger.warning("FALLO: " + nombre + " – No se encontró el registro con matrícula " + MATRICULA_VALIDA);
                return;
            }

            if (!MATRICULA_VALIDA.equals(resultado.getMatricula())) {
                logger.warning("FALLO: " + nombre + " – La matrícula devuelta no coincide: " + resultado.getMatricula());
                return;
            }

            logger.info("EXITO: " + nombre + " – Registro encontrado: matrícula=" + resultado.getMatricula() + ", calificación=" + resultado.getCalificacion());
            pruebasExitosas++;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "FALLO: " + nombre + " – Error inesperado: " + e.getMessage(), e);
        }
    }

    public void pruebaActualizar() {
        pruebasEjecutadas++;
        String nombre = "pruebaActualizar";

        AutoevaluacionDTO dto = new AutoevaluacionDTO(0, MATRICULA_VALIDA, new BigDecimal("9.75"), "Comentario actualizado en prueba");

        try {
            dao.actualizar(dto);

            AutoevaluacionDTO verificacion = dao.buscarPorMatricula(MATRICULA_VALIDA);
            if (verificacion == null) {
                logger.warning("FALLO: " + nombre + " – No se pudo recuperar el registro después de actualizar.");
                return;
            }

            if (new BigDecimal("9.75").compareTo(verificacion.getCalificacion()) != 0) {
                logger.warning("FALLO: " + nombre + " – La calificación no coincide. Valor en BD: " + verificacion.getCalificacion());
                return;
            }

            logger.info("EXITO: " + nombre + " – Registro actualizado. Nueva calificación=" + verificacion.getCalificacion());
            pruebasExitosas++;

        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "FALLO: " + nombre + " – Matrícula inválida: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "FALLO: " + nombre + " – Error inesperado: " + e.getMessage(), e);
        }
    }

    private void mostrarResumen() {
        logger.info("RESUMEN DE PRUEBAS");
        logger.info("Ejecutadas : " + pruebasEjecutadas);
        logger.info("Exitosas   : " + pruebasExitosas);
        logger.info("Fallidas   : " + (pruebasEjecutadas - pruebasExitosas));
    }

    public static void main(String[] args) throws Exception {
        PruebaAutoevaluacionDAO pruebas = new PruebaAutoevaluacionDAO();

        pruebas.pruebaAgregar();
        pruebas.pruebaBuscarPorMatricula();
        pruebas.pruebaActualizar();

        pruebas.mostrarResumen();
    }

}


