package nl.novi.bloomtrail.services;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import nl.novi.bloomtrail.models.ManagingStrength;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;


@Service
public class PdfGeneratorService {

    public byte[] createPdf(List<ManagingStrength> strengths) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            for (ManagingStrength strength : strengths) {

                document.add(new Paragraph(strength.getStrengthNl() + " (" + strength.getStrengthEn() + ")"));
            }

            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create PDF", e);
        }

        return baos.toByteArray();
    }
}
