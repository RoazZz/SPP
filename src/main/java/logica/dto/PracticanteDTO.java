package logica.dto;

import logica.enums.EstadoDelPracticante;
import logica.enums.GeneroDelPracticante;
import logica.enums.TipoDeUsuario;
import logica.enums.TipoEstado;

public class PracticanteDTO extends UsuarioDTO{
    private String matricula;
    private int idSeccion;
    private String semestre;
    private GeneroDelPracticante generoDelPracticante;
    private int edad;
    private boolean lenguaIndigena;

    public PracticanteDTO (int idUsuario, String nombre, String apellidoPaterno, String apellidoMaterno, String contrasenia, TipoEstado estado, TipoDeUsuario tipoDeUsuario, String matricula, int idSeccion, String semestre,
                           GeneroDelPracticante generoDelPracticante, int edad, boolean lenguaIndigena){
        super(idUsuario, nombre, apellidoPaterno, apellidoMaterno, contrasenia, estado, tipoDeUsuario);

        this.matricula = matricula;
        this.idSeccion = idSeccion;
        this.semestre = semestre;
        this.generoDelPracticante = generoDelPracticante;
        this.edad = edad;
        this.lenguaIndigena = lenguaIndigena;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public GeneroDelPracticante getGeneroDelPracticante() {
        return generoDelPracticante;
    }

    public void setGeneroDelPracticante(GeneroDelPracticante generoDelPracticante) {
        this.generoDelPracticante = generoDelPracticante;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public boolean isLenguaIndigena() {
        return lenguaIndigena;
    }

    public void setLenguaIndigena(boolean lenguaIndigena) {
        this.lenguaIndigena = lenguaIndigena;
    }
}
