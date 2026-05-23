package logica.dao;

import accesodatos.ConexionBD;
import excepciones.ConsultaIndicadoresExcepcion;
import logica.dto.ReporteIndicadoresDTO;
import logica.enums.FiltrosIndicadores;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteIndicadoresDAO {

    private static final String COLUMNA_CATEGORIA = "categoria";
    private static final String COLUMNA_TOTAL = "total";
    private static final String TABLA_BASE = "practicante p";

    public List<ReporteIndicadoresDTO> contarPracticantesPor(FiltrosIndicadores filtro)
            throws ConsultaIndicadoresExcepcion {

        if (filtro == null) {
            throw new IllegalArgumentException(
                    "El filtro de análisis no puede ser nulo.");
        }

        String consultaSql = construirConsultaAgrupada(filtro);
        List<ReporteIndicadoresDTO> resultados = new ArrayList<>();

        try {
            Connection conexion = ConexionBD.obtenerInstancia().obtenerConexion();

            try (PreparedStatement sentencia = conexion.prepareStatement(consultaSql);
                 ResultSet conjuntoResultado = sentencia.executeQuery()) {

                while (conjuntoResultado.next()) {
                    String valorCategoria = conjuntoResultado.getString(COLUMNA_CATEGORIA);
                    int cantidadPracticantes = conjuntoResultado.getInt(COLUMNA_TOTAL);
                    resultados.add(new ReporteIndicadoresDTO(
                            valorCategoria, cantidadPracticantes));
                }
            }

        } catch (SQLException sqlExcepcion) {
            throw new ConsultaIndicadoresExcepcion(
                    "Error al consultar el indicador " + filtro.name() + ".",
                    sqlExcepcion);
        } catch (IOException ioExcepcion) {
            throw new ConsultaIndicadoresExcepcion(
                    "No se pudo cargar la configuración de la base de datos.",
                    ioExcepcion);
        }

        return resultados;
    }

    private String construirConsultaAgrupada(FiltrosIndicadores filtro) {
        String expresionSeleccion = obtenerExpresionSeleccion(filtro);
        String expresionAgrupacion = obtenerExpresionAgrupacion(filtro);

        return "SELECT " + expresionSeleccion + " AS " + COLUMNA_CATEGORIA
                + ", COUNT(*) AS " + COLUMNA_TOTAL
                + " FROM " + TABLA_BASE
                + " GROUP BY " + expresionAgrupacion
                + " ORDER BY " + COLUMNA_CATEGORIA;
    }

    private String obtenerExpresionSeleccion(FiltrosIndicadores filtro) {
        switch (filtro) {
            case GENERO:
                return "p.Genero";
            case EDAD:
                return "p.Edad";
            case SEMESTRE:
                return "p.Semestre";
            case LENGUA_INDIGENA:
                return "CASE p.LenguaIndigena WHEN 1 THEN 'Sí' ELSE 'No' END";
            default:
                throw new IllegalArgumentException(
                        "Filtro no soportado: " + filtro);
        }
    }

    private String obtenerExpresionAgrupacion(FiltrosIndicadores filtro) {
        switch (filtro) {
            case GENERO:
                return "p.Genero";
            case EDAD:
                return "p.Edad";
            case SEMESTRE:
                return "p.Semestre";
            case LENGUA_INDIGENA:
                return "p.LenguaIndigena";
            default:
                throw new IllegalArgumentException(
                        "Filtro no soportado: " + filtro);
        }
    }
}