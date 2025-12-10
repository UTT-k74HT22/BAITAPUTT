package org.example.export.base;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class BasePdfExporter<T> {

    protected PdfFont font;
    protected PdfFont boldFont;

    protected void initFonts() throws IOException {
        font = PdfFontFactory.createFont("Helvetica");
        boldFont = PdfFontFactory.createFont("Helvetica-Bold");
    }

    public void export(List<T> data, String filePath) throws IOException {
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            initFonts();

            document.add(createTitle());
            document.add(createDateInfo());
            document.add(createTable(data));
            document.add(createFooter(data.size()));

        } finally {
            document.close();
        }
    }

    protected abstract Paragraph createTitle();
    protected abstract Table createTable(List<T> data);

    protected Paragraph createDateInfo() {
        return new Paragraph("Ngay xuat: " +
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setFont(font)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT);
    }

    protected Paragraph createFooter(int total) {
        return new Paragraph("Tong so: " + total)
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.RIGHT);
    }
}
