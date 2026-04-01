package logica.dto;

import logica.enums.EstadoDelPracticante;
import logica.enums.GeneroDelPracticante;

public class PracticanteDTO {
    private String matricula;
    private int idSeccion;
    private String semestre;
    private EstadoDelPracticante estadoDelPracticante;
    private GeneroDelPracticante generoDelPracticante;
    private int edad;
    private boolean lenguaIndigena;

    public PracticanteDTO (String matricula, int idSeccion, String semestre, EstadoDelPracticante estadoDelPracticante,
                           GeneroDelPracticante generoDelPracticante, int edad, boolean lenguaIndigena){
        this.matricula = matricula;
        this.idSeccion = idSeccion;
        this.semestre = semestre;
        this.estadoDelPracticante = estadoDelPracticante;
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

    public EstadoDelPracticante getEstadoDelPracticante() {
        return estadoDelPracticante;
    }

    public void setEstadoDelPracticante(EstadoDelPracticante estadoDelPracticante) {
        this.estadoDelPracticante = estadoDelPracticante;
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
