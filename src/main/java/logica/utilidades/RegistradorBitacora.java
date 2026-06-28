package logica.utilidades;

import excepciones.DAOExcepcion;
import logica.dao.BitacoraSistemaDAO;
import logica.dto.BitacoraSistemaDTO;
import logica.dto.PracticanteDTO;
import logica.dto.UsuarioDTO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class RegistradorBitacora {

    private static final Logger REGISTRADOR = Logger.getLogger(RegistradorBitacora.class.getName());
    private static final Path RUTA_ARCHIVO = Paths.get("bitacora_sistema.txt");
    private static final String ROL_ANONIMO = "ANONIMO";
    private static final String NOMBRE_ANONIMO = "Sin sesion";
    private static final String MENSAJE_ARCHIVO_VACIO = "El archivo de bitacora aun no contiene registros.";
    private static final String MENSAJE_ERROR_LECTURA = "No se pudo leer el archivo de bitacora.";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private RegistradorBitacora() {
    }

    public static void registrar(String tipoEvento, String descripcion) {
        UsuarioDTO usuarioActual = SesionUsuarioSingleton.obtenerInstancia().obtenerUsuarioActual();

        String rol = ROL_ANONIMO;
        String nombre = NOMBRE_ANONIMO;
        if (usuarioActual != null) {
            rol = usuarioActual.getTipoDeUsuario().name();
            nombre = construirNombre(usuarioActual);
        }

        registrar(rol, nombre, tipoEvento, descripcion);
    }

    public static void registrar(String rol, String nombre, String tipoEvento, String descripcion) {
        LocalDateTime ahora = LocalDateTime.now();
        BitacoraSistemaDTO evento = new BitacoraSistemaDTO(0, rol, nombre, tipoEvento, ahora, descripcion);

        guardarEnBaseDeDatos(evento);
        guardarEnArchivo(evento);
    }

    private static void guardarEnBaseDeDatos(BitacoraSistemaDTO evento) {
        try {
            new BitacoraSistemaDAO().agregarBitacora(evento);
        } catch (DAOExcepcion daoExcepcion) {
            REGISTRADOR.log(Level.WARNING, "No se pudo registrar el evento en la base de datos", daoExcepcion);
        }
    }

    private static void guardarEnArchivo(BitacoraSistemaDTO evento) {
        String linea = String.format("[%s] %s (%s) - %s: %s%n",
                evento.getFechaHora().format(FORMATO_FECHA),
                evento.getNombreUsuario(),
                evento.getRolUsuario(),
                evento.getTipoEvento(),
                evento.getDescripcionEvento());

        try {
            Files.write(RUTA_ARCHIVO, linea.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException ioException) {
            REGISTRADOR.log(Level.WARNING, "No se pudo escribir el evento en el archivo de bitacora", ioException);
        }
    }

    private static String construirNombre(UsuarioDTO usuario) {
        StringBuilder nombreCompleto = new StringBuilder(usuario.getNombre());
        if (usuario.getApellidoPaterno() != null) {
            nombreCompleto.append(" ").append(usuario.getApellidoPaterno());
        }
        if (usuario instanceof PracticanteDTO practicante && practicante.getMatricula() != null) {
            nombreCompleto.append(" [").append(practicante.getMatricula()).append("]");
        }
        return nombreCompleto.toString();
    }

    public static String leerArchivoBitacora() {
        if (!Files.exists(RUTA_ARCHIVO)) {
            return MENSAJE_ARCHIVO_VACIO;
        }

        try {
            return Files.readString(RUTA_ARCHIVO, StandardCharsets.UTF_8);
        } catch (IOException ioException) {
            REGISTRADOR.log(Level.WARNING, "No se pudo leer el archivo de bitacora", ioException);
            return MENSAJE_ERROR_LECTURA;
        }
    }
}