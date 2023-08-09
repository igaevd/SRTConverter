package com.igayev.srtconverter.processor;

import lombok.extern.log4j.Log4j2;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@Log4j2
public class Epub {

    public static void saveAsEpub(String text, File outputFile) throws IOException {

        Book book = new Book();

        // Set Metadata
        Metadata metadata = new Metadata();
        String title = outputFile.getName().replace(".epub", "");
        metadata.addTitle(title);
        book.setMetadata(metadata);

        // Valid XHTML for content
        Resource contentResource = getResource(text.replace(System.lineSeparator(), "<br/>"), title);
        book.addResource(contentResource);

        // Add the content as a section to the book
        book.addSection("Content", contentResource);

        // Set Table of Contents
        TOCReference tocReference = new TOCReference(title, contentResource);
        book.getTableOfContents().getTocReferences().add(tocReference);

        // Write the book to the file
        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            (new EpubWriter()).write(book, outputStream);
        }
    }

    private static Resource getResource(String text, String title) {
        String xhtmlContent = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3" +
                                            ".org/TR/xhtml11/DTD/xhtml11.dtd\">\n" +
                                            "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                                            "<head><title>%s</title></head>\n" +
                                            "<body>\n%s\n</body>\n</html>", title, text);
        return new Resource(xhtmlContent.getBytes(StandardCharsets.UTF_8), "content.xhtml");
    }
}
