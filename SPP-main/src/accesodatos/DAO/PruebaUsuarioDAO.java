package accesodatos.DAO;

import accesodatos.DTO.UsuarioDTO;

public class PruebaUsuarioDAO {

    public static void main(String[] args) {
        System.out.println("Prueba insertar Usuario");
        UsuarioDAO usuarioDao = new UsuarioDAO();
        UsuarioDTO nuevoUsuario = new UsuarioDTO(1, "Jared", "Morales", "Tirado", "123");

        if (usuarioDao.insertar(nuevoUsuario)) {
            System.out.println("Usuario insertado con éxito");
        } else {
            System.out.println("Error no se pudo insertar el usuario.");
        }
    }
}