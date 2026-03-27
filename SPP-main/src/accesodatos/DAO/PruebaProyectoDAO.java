package accesodatos.DAO;

import accesodatos.DTO.ProyectoDTO;

public class PruebaProyectoDAO {

    public static void main(String[] args) {
        System.out.println("Prueba insertar Proyecto");
        ProyectoDAO proyectoDao = new ProyectoDAO();

        ProyectoDTO nuevoProyecto = new ProyectoDTO(1, "1", "00001", "Prueba", "Prueba");

        if (proyectoDao.insertar(nuevoProyecto)) {
            System.out.println("Proyecto insertado con éxito");
        } else {
            System.out.println(">> Error no se pudo insertar el proyecto.");
        }
    }
}