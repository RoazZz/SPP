package logica.utilidades;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import excepciones.EntidadNoCreadaExcepcion;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.BarChart;
import javafx.scene.image.WritableImage;
import logica.dto.ReporteIndicadoresDTO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExportadorIndicadoresPDF {

    private static final float ANCHO_MAXIMO_GRAFICA = 480f;
    private static final float ALTO_MAXIMO_GRAFICA = 300f;
    private static final float TAMANO_FUENTE_TITULO = 18f;
    private static final float TAMANO_FUENTE_SUBTITULO = 13f;
    private static final float TAMANO_FUENTE_DETALLE = 11f;
    private static final float TAMANO_FUENTE_PIE_PAGINA = 9f;
    private static final String FORMATO_FECHA_REPORTE = "dd/MM/yyyy HH:mm";
    private static final DeviceRgb COLOR_AZUL_ENCABEZADO = new DeviceRgb(24, 82, 157);

    public void exportar(File archivoDestino,
                         String tituloReporte,
                         List<ReporteIndicadoresDTO> datos,
                         BarChart<?, ?> grafica)
            throws EntidadNoCreadaExcepcion {

        validarParametros(archivoDestino, tituloReporte, datos, grafica);

        BufferedImage imagenGrafica = capturarGraficaComoImagen(grafica);

        try (ByteArrayOutputStream bufferImagenPng = new ByteArrayOutputStream();
             PdfWriter escritor = new PdfWriter(archivoDestino);
             PdfDocument pdfDocumento = new PdfDocument(escritor);
             Document documento = new Document(pdfDocumento)) {

            ImageIO.write(imagenGrafica, "png", bufferImagenPng);

            agregarEncabezado(documento, tituloReporte);
            agregarImagenGrafica(documento, bufferImagenPng.toByteArray());
            agregarTablaDetalle(documento, datos);
            agregarPiePagina(documento);

        } catch (IOException ioExcepcion) {
            throw new EntidadNoCreadaExcepcion("No se pudo generar el archivo PDF: " + archivoDestino.getAbsolutePath());
        }
    }

    private void validarParametros(File archivoDestino,
                                   String tituloReporte,
                                   List<ReporteIndicadoresDTO> datos,
                                   BarChart<?, ?> grafica) {
        if (archivoDestino == null) {
            throw new IllegalArgumentException(
                    "El archivo destino es obligatorio.");
        }
        if (tituloReporte == null || tituloReporte.isBlank()) {
            throw new IllegalArgumentException(
                    "El título del reporte es obligatorio.");
        }
        if (datos == null || datos.isEmpty()) {
            throw new IllegalArgumentException(
                    "Los datos del reporte no pueden estar vacíos.");
        }
        if (grafica == null) {
            throw new IllegalArgumentException(
                    "La gráfica del reporte es obligatoria.");
        }
    }

    private BufferedImage capturarGraficaComoImagen(BarChart<?, ?> grafica) {
        WritableImage imagenJavaFx = grafica.snapshot(null, null);
        return SwingFXUtils.fromFXImage(imagenJavaFx, null);
    }

    private void agregarEncabezado(Document documento, String tituloReporte)
            throws IOException {
        PdfFont fuenteNegrita = PdfFontFactory.createFont(
                com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);

        Paragraph tituloPrincipal = new Paragraph(tituloReporte)
                .setFont(fuenteNegrita)
                .setFontSize(TAMANO_FUENTE_TITULO)
                .setFontColor(COLOR_AZUL_ENCABEZADO)
                .setTextAlignment(TextAlignment.CENTER);

        Paragraph subtituloFecha = new Paragraph(
                "Generado el " + obtenerFechaActualFormateada())
                .setFontSize(TAMANO_FUENTE_DETALLE)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER);

        documento.add(tituloPrincipal);
        documento.add(subtituloFecha);
        documento.add(new Paragraph(" "));
    }

    private void agregarImagenGrafica(Document documento, byte[] datosImagenPng) {
        Image imagenPdf = new Image(ImageDataFactory.create(datosImagenPng));
        imagenPdf.scaleToFit(ANCHO_MAXIMO_GRAFICA, ALTO_MAXIMO_GRAFICA);
        imagenPdf.setHorizontalAlignment(HorizontalAlignment.CENTER);
        documento.add(imagenPdf);
        documento.add(new Paragraph(" "));
    }

    private void agregarTablaDetalle(Document documento,
                                     List<ReporteIndicadoresDTO> datos)
            throws IOException {
        PdfFont fuenteNegrita = PdfFontFactory.createFont(
                com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);

        Paragraph subtituloTabla = new Paragraph("Detalle de información")
                .setFont(fuenteNegrita)
                .setFontSize(TAMANO_FUENTE_SUBTITULO)
                .setFontColor(COLOR_AZUL_ENCABEZADO);
        documento.add(subtituloTabla);

        Table tablaDetalle = new Table(UnitValue.createPercentArray(
                new float[]{2f, 1f}))
                .setWidth(UnitValue.createPercentValue(80))
                .setHorizontalAlignment(HorizontalAlignment.CENTER);

        tablaDetalle.addHeaderCell(crearCeldaEncabezado("Categoría", fuenteNegrita));
        tablaDetalle.addHeaderCell(crearCeldaEncabezado("Total", fuenteNegrita));

        int totalAcumulado = 0;
        for (ReporteIndicadoresDTO indicador : datos) {
            tablaDetalle.addCell(crearCeldaContenido(indicador.getCategoria()));
            tablaDetalle.addCell(crearCeldaContenido(
                    String.valueOf(indicador.getTotal())));
            totalAcumulado += indicador.getTotal();
        }

        tablaDetalle.addCell(crearCeldaTotal("Total general", fuenteNegrita));
        tablaDetalle.addCell(crearCeldaTotal(
                String.valueOf(totalAcumulado), fuenteNegrita));

        documento.add(tablaDetalle);
    }

    private Cell crearCeldaEncabezado(String texto, PdfFont fuenteNegrita) {
        return new Cell()
                .add(new Paragraph(texto).setFont(fuenteNegrita))
                .setBackgroundColor(COLOR_AZUL_ENCABEZADO)
                .setFontColor(ColorConstants.WHITE)
                .setFontSize(TAMANO_FUENTE_DETALLE)
                .setBorder(new SolidBorder(ColorConstants.WHITE, 0.5f))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(6);
    }

    private Cell crearCeldaContenido(String texto) {
        return new Cell()
                .add(new Paragraph(texto))
                .setFontSize(TAMANO_FUENTE_DETALLE)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }

    private Cell crearCeldaTotal(String texto, PdfFont fuenteNegrita) {
        return new Cell()
                .add(new Paragraph(texto).setFont(fuenteNegrita))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setFontSize(TAMANO_FUENTE_DETALLE)
                .setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
    }

    private void agregarPiePagina(Document documento) {
        documento.add(new Paragraph(" "));
        Paragraph piePagina = new Paragraph(
                "Sistema Web para las Prácticas Profesionales (SPP)")
                .setFontSize(TAMANO_FUENTE_PIE_PAGINA)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER);
        documento.add(piePagina);
    }

    private String obtenerFechaActualFormateada() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(FORMATO_FECHA_REPORTE));
    }
}