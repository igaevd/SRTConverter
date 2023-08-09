package com.igayev.srtconverter.processor;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;


import java.io.*;

public class Pdf {


    public static void saveAsPdf(String text, File outputFile) throws IOException {

        PdfWriter writer = new PdfWriter(outputFile);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(10, 20, 10, 20);
        Paragraph paragraph = new Paragraph(text).setFontSize(18);
        document.add(paragraph);
        document.close();
    }
}
