package pruebas;

import excepciones.DAOExcepcion;
import logica.dao.*;
import logica.dto.*;
import logica.enums.*;
import java.time.LocalDate;
import java.util.List;

public class PruebaConsola {

    public static void main(String[] args) {

        try {
            // 1. PROBAR REPORTE (No depende de nadie)
            System.out.println("[1/4] Probando ReporteDAO...");
            ReporteDAO reporteDAO = new ReporteDAO();
            int idReporteTest = (int) (Math.random() * 1000);
            ReporteDTO reporte = new ReporteDTO(idReporteTest, TipoReporte.PARCIAL, LocalDate.now(), "ruta/prueba.pdf");
            reporteDAO.agregarReporte(reporte);
            System.out.println(">> Reporte insertado correctamente.");

            // PROBAR PROFESOR (Inserta Usuario + Profesor)
            System.out.println("\n[2/4] Probando ProfesorDAO (Inserción Doble)...");
            ProfesorDAO profesorDAO = new ProfesorDAO();
            String numPersonalTest = "PER-" + (int)(Math.random() * 5000);
            ProfesorDTO profe = new ProfesorDTO(0, "Miguel", "Hidalgo", "Costilla", "patria123",
                    TipoEstado.ACTIVO, TipoDeUsuario.PROFESOR, numPersonalTest, TipoTurno.VESPERTINO);
            profesorDAO.agregarProfesor(profe);
            System.out.println(">> Profesor insertado. ID Usuario generado: " + profe.getIdUsuario());

            // 3. PROBAR ORGANIZACIÓN VINCULADA (No depende de nadie)
            System.out.println("\n[3/4] Probando OrganizacionVinculadaDAO...");
            OrganizacionVinculadaDAO orgDAO = new OrganizacionVinculadaDAO();
            String idOrgTest = "ORG-" + (int)(Math.random() * 5000);
            OrganizacionVinculadaDTO org = new OrganizacionVinculadaDTO(idOrgTest, "Tecnologías Xalapa", "Calle Enríquez s/n");
            orgDAO.agregarOrganizacionVinculada(org);
            System.out.println(">> Organización insertada: " + org.getNombre());

            // 4. PROBAR PROYECTO (DEPENDE DE LOS PASOS 2 y 3)
            System.out.println("\n[4/4] Probando ProyectoDAO (Con Dependencias)...");
            ProyectoDAO proyectoDAO = new ProyectoDAO();
            // Usamos el numPersonalTest y el idOrgTest que acabamos de crear arriba
            ProyectoDTO proyecto = new ProyectoDTO(0, idOrgTest, numPersonalTest, "App de Prácticas", "Desarrollo de sistema");
            proyectoDAO.agregarProyecto(proyecto);
            System.out.println(">> Proyecto insertado con éxito. ID: " + proyecto.getIdProyecto());

            //5. PRUEBA DE SELECT (LISTAR)
            System.out.println("\n=== VERIFICACIÓN FINAL: LISTADO DE PROYECTOS ===");
            List<ProyectoDTO> lista = proyectoDAO.listarProyectos();
            for (ProyectoDTO p : lista) {
                System.out.println("Proyecto: " + p.getNombre() + " | Org: " + p.getIdOrganizacion());
            }

        } catch (DAOExcepcion e) {
            System.err.println("\n¡ERROR DE DAO!: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Causa técnica: " + e.getCause().getMessage());
            }
        } catch (Exception e) {
            System.err.println("\n¡ERROR INESPERADO!: " + e.toString());
            e.printStackTrace();
        }
    }
}