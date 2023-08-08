import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SrtToEpub {

    public static void main(String[] args) {
        File directory = new File("/Users/dmitry.igayev/Movies");

        // Filter to get only .srt files
        File[] srtFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".srt"));

        if (srtFiles == null) {
            System.out.println("No .srt files found or an I/O error occurred.");
            return;
        }

        for (File srtFile : srtFiles) {
            String outputFileName = srtFile.getName().replaceFirst("[.][^.]+$", "") + ".epub";
            File outputFile = new File(directory, outputFileName);

            convertFileToEpub(srtFile, outputFile);
        }
    }

    private static void convertFileToEpub(File inputFile, File outputFile) {
        StringBuilder outputText = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            StringBuilder block = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    String convertedBlock = convertBlock(block.toString().trim());
                    if (!convertedBlock.isEmpty()) {
                        outputText.append(convertedBlock).append("\n");
                    }
                    block.setLength(0);
                } else {
                    block.append(line).append("\n");
                }
            }

            String convertedBlock = convertBlock(block.toString().trim());
            if (!convertedBlock.isEmpty()) {
                outputText.append(convertedBlock).append("\n");
            }

            saveAsEpub(outputText.toString().trim(), outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveAsEpub(String text, File outputFile) throws IOException {
        Book book = new Book();

        // Set Metadata
        Metadata metadata = new Metadata();
        String title = outputFile.getName().replace(".epub", "");
        metadata.addTitle(title);
        book.setMetadata(metadata);

//        // Cover Page (Let's use a generic cover for now)
//        Resource coverResource = new Resource(SrtToEpub.class.getClassLoader().getResourceAsStream("cover.jpeg"), "cover.jpeg");
//        book.setCoverImage(coverResource);
//        book.addResource(coverResource);

        // Valid XHTML for content
        String xhtmlContent = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                "<head><title>%s</title></head>\n" +
                "<body>\n%s\n</body>\n</html>", title, text);

        Resource contentResource = new Resource(xhtmlContent.getBytes(StandardCharsets.UTF_8), "content.xhtml");
        book.addResource(contentResource);

        // Add the content as a section to the book
        book.addSection("Content", contentResource);

        // Set Table of Contents
        TOCReference tocReference = new TOCReference(title, contentResource);
        book.getTableOfContents().getTocReferences().add(tocReference);

        // Write the book to the file
        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            (new EpubWriter()).write(book, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String convertBlock(String block) {
        String[] lines = block.trim().split("\n");

        // Skip blocks that have only the sequence number and timestamp (no actual text)
        if (lines.length <= 2) {
            return "";
        }

        StringBuilder textBuilder = new StringBuilder();

        for (int i = 2; i < lines.length; i++) { // Start from 2 to skip the sequence number and timestamp
            textBuilder.append(lines[i]);
            if (i != lines.length - 1) {
                textBuilder.append("<br />"); // Use <br /> for line breaks within a block
            }
        }

        textBuilder.append("<br /><br />"); // Use two <br /> tags after each block for separation

        String text = textBuilder.toString();

        // If the text starts with ( and ends with ) or starts with [ and ends with ], remove it
        if ((text.startsWith("(") && text.endsWith(")")) || (text.startsWith("[") && text.endsWith("]"))) {
            return "";
        }

        return text;
    }

    private static boolean isTimestamp(String line) {
        return line.matches("^\\d{2}:\\d{2}:\\d{2},\\d{3} --> \\d{2}:\\d{2}:\\d{2},\\d{3}$");
    }

    private static boolean isSequenceNumber(String line) {
        return line.matches("^\\d+$");
    }
}
