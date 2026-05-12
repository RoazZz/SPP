package logica.utilidades;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CifradorArchivo {
    private static final String ALGORITMO_HASH = "SHA-256";

    private CifradorArchivo() {}

    public static String generarHashArchivo(Path rutaArchivo) throws IOException {
        MessageDigest digest = obtenerDigest();
        byte[] buffer = new byte[8192];
        int bytesLeidos;

        try (InputStream flujo = Files.newInputStream(rutaArchivo)) {
            while ((bytesLeidos = flujo.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesLeidos);
            }
        }
        return bytesAHexadecimal(digest.digest());
    }

    public static String generarHashContenido(Path rutaArchivo) throws IOException {
        try (PdfDocument documento = new PdfDocument(new PdfReader(rutaArchivo.toString()))) {
            StringBuilder texto = new StringBuilder();
            boolean dentroDeActividades = false;

            for (int pagina = 1; pagina <= documento.getNumberOfPages(); pagina++) {
                String textoPagina = PdfTextExtractor.getTextFromPage(documento.getPage(pagina));
                for (String linea : textoPagina.split("\n")) {
                    if (linea.trim().equals("DETALLE DE ACTIVIDADES:")) {
                        dentroDeActividades = true;
                        continue;
                    }
                    if (dentroDeActividades) {
                        texto.append(linea.trim());
                    }
                }
            }

            String textoNormalizado = texto.toString()
                    .toLowerCase()
                    .trim()
                    .replaceAll("\\s+", " ");

            MessageDigest digest = obtenerDigest();
            digest.update(textoNormalizado.getBytes("UTF-8"));
            return bytesAHexadecimal(digest.digest());
        }
    }

    public static String extraerTipoReporte(Path rutaArchivo) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(rutaArchivo.toString()))) {
            String texto = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1));
            for (String linea : texto.split("\n")) {
                if (linea.trim().startsWith("Tipo:")) {
                    return linea.trim().replace("Tipo:", "").trim();
                }
            }
        }
        return null;
    }

    public static String extraerMes(Path rutaArchivo) throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(rutaArchivo.toString()))) {
            String texto = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1));
            for (String linea : texto.split("\n")) {
                if (linea.trim().startsWith("Mes:")) {
                    return linea.trim().replace("Mes:", "").trim();
                }
            }
        }
        return null;
    }

    private static MessageDigest obtenerDigest() {
        try {
            return MessageDigest.getInstance(ALGORITMO_HASH);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    private static String bytesAHexadecimal(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
