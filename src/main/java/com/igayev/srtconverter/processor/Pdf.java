package com.igayev.srtconverter.processor;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;


import java.io.*;

public class Pdf {


    public static void saveAsPdf(String text, File outputFile) throws IOException {

        PdfWriter writer = new PdfWriter(outputFile);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(10, 20, 10, 20);

        Style style = new Style()
                .setFont(PdfFontFactory.createFont(StandardFonts.SYMBOL))
                .setFontSize(18);

        Paragraph paragraph = new Paragraph(text)
                .add(new Text(text).addStyle(style));
        document.add(paragraph);
        document.close();
    }
}
