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
