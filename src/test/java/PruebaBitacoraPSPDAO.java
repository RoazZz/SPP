import logica.dao.BitacoraPSPDAO;
import logica.dto.BitacoraPSPDTO;

import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PruebaBitacoraPSPDAO {
    private static final Logger logger = Logger.getLogger(PruebaBitacoraPSPDAO.class.getName());

    private static final String MATRICULA_PRUEBA = "S23010001";

    private BitacoraPSPDAO dao;
    private int pruebasEjecutadas = 0;
    private int pruebasExitosas   = 0;

    // id generado al agregar, usado en buscar y actualizar
    private int idGenerado = 0;

    public PruebaBitacoraPSPDAO() {
        dao = new BitacoraPSPDAO();
    }

    public void pruebaAgregar() {
        pruebasEjecutadas++;
        String nombre = "pruebaAgregar";

        BitacoraPSPDTO dto = new BitacoraPSPDTO(0, MATRICULA_PRUEBA, LocalDate.now());

        try {
            dao.agregar(dto);

            if (dto.getIdBBitacora() <= 0) {
                logger.warning("FALLO: " + nombre + " – La BD no devolvió un ID generado válido.");
                return;
            }

            idGenerado = dto.getIdBBitacora();
            logger.info("Exito: " + nombre + " – Bitácora insertada con id=" + idGenerado);
            pruebasExitosas++;

        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "FALLO: " + nombre + " – Matrícula inválida: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "FALLO: " + nombre + " – Error inesperado: " + e.getMessage(), e);
        }
    }

    public void pruebaBuscarPorId() {
        pruebasEjecutadas++;
        String nombre = "pruebaBuscarPorId";

        try {
            BitacoraPSPDTO resultado = dao.buscarPorId(idGenerado);

            if (resultado == null) {
                logger.warning("FALLO: " + nombre + " – No se encontró la bitácora con id=" + idGenerado);
                return;
            }

            if (resultado.getIdBBitacora() != idGenerado) {
                logger.warning("FALLO: " + nombre + " – El id devuelto no coincide: " + resultado.getIdBBitacora());
                return;
            }

            logger.info("Exito: " + nombre + " – Bitácora encontrada: id=" + resultado.getIdBBitacora() + ", matrícula=" + resultado.getMatricula() + ", fecha=" + resultado.getFecha());
            pruebasExitosas++;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "FALLO: " + nombre + " – Error inesperado: " + e.getMessage(), e);
        }
    }

    public void pruebaActualizar() {
        pruebasEjecutadas++;
        String nombre = "pruebaActualizar";

        LocalDate nuevaFecha = LocalDate.now().plusDays(1);
        BitacoraPSPDTO dto = new BitacoraPSPDTO(idGenerado, MATRICULA_PRUEBA, nuevaFecha);

        try {
            dao.actualizar(dto);

            BitacoraPSPDTO verificacion = dao.buscarPorId(idGenerado);
            if (verificacion == null) {
                logger.warning("FALLO: " + nombre + " – No se pudo recuperar la bitácora después de actualizar.");
                return;
            }

            if (!nuevaFecha.equals(verificacion.getFecha())) {
                logger.warning("FALLO: " + nombre + " – La fecha no coincide. Valor en BD: " + verificacion.getFecha());
                return;
            }

            logger.info("Exito: " + nombre + " – Bitácora actualizada. Nueva fecha=" + verificacion.getFecha());
            pruebasExitosas++;

        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Exito: " + nombre + " – Matrícula inválida: " + e.getMessage(), e);
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

    public static void main(String[] args) {
        PruebaBitacoraPSPDAO pruebas = new PruebaBitacoraPSPDAO();

        pruebas.pruebaAgregar();
        pruebas.pruebaBuscarPorId();
        pruebas.pruebaActualizar();

        pruebas.mostrarResumen();
    }
}
