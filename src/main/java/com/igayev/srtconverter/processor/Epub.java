package com.igayev.srtconverter.processor;

import lombok.extern.log4j.Log4j2;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubWriter;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;


@Log4j2
public class Epub {

    public static void saveAsEpub(String text, File outputFile) throws IOException {
        Book book = new Book();

        // Metadata
        Metadata metadata = new Metadata();
        String title = outputFile.getName().replace(".epub", "");
        metadata.addTitle(title);
        metadata.addAuthor(new Author("SRT Converter"));
        book.setMetadata(metadata);

        // Cover Image
        Resource coverResource = new Resource(createCoverImage(title), "cover.png");
        book.setCoverImage(coverResource);

        // Content
        Resource contentResource = createContent(text.replace(System.lineSeparator(), "<br/>"), title);
        book.addResource(contentResource);
        book.addSection("Content", contentResource);
        TOCReference tocReference = new TOCReference(title, contentResource);
        book.getTableOfContents().getTocReferences().add(tocReference);

        // Write the book to the file
        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            (new EpubWriter()).write(book, outputStream);
        }
    }

    private static byte[] createCoverImage(String title) throws IOException {
        int width = 600;
        int height = 900;
        BufferedImage coverImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = coverImage.createGraphics();
        GradientPaint gradient = new GradientPaint(0, 0, new Color(153, 0, 0), 0, height, new Color(102, 0, 0));
        g2.setPaint(gradient);
        g2.fillRect(0, 0, width, height);

        // Set font
        int fontSize = 72;
        g2.setFont(new Font("Serif", Font.BOLD, fontSize));
        g2.setColor(Color.WHITE);
        FontMetrics metrics = g2.getFontMetrics();

        List<String> lines = Arrays.stream(title.split(" "))
                .reduce(new ArrayList<>(Arrays.asList("")), (lineList, word) -> {
                    String currentLine = lineList.get(lineList.size() - 1);
                    String testLine = currentLine + " " + word;
                    int testLineWidth = metrics.stringWidth(testLine.trim());
                    if (testLineWidth > width - 40) {
                        lineList.add(word);
                    } else {
                        lineList.set(lineList.size() - 1, testLine.trim());
                    }
                    return lineList;
                }, (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                });

        // Draw each line centered
        AtomicInteger yPosition = new AtomicInteger(fontSize + 50);
        lines.forEach(line -> {
            int xPosition = (width - metrics.stringWidth(line)) / 2;
            g2.drawString(line, xPosition, yPosition.get());
            yPosition.addAndGet(metrics.getHeight());
        });


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(coverImage, "jpg", baos);
        return baos.toByteArray();
    }


    private static Resource createContent(String text, String title) {
        String xhtmlContent = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3" +
                                            ".org/TR/xhtml11/DTD/xhtml11.dtd\">\n" +
                                            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                                            "<head><title>%s</title></head>\n" +
                                            "<body>\n%s\n</body>\n</html>", title, text);
        return new Resource(xhtmlContent.getBytes(StandardCharsets.UTF_8), "content.xhtml");
    }
}
