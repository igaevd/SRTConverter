import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;


import java.io.*;

public class SrtToPdf {

    public static void main(String[] args) {
        File directory = new File("/Users/dmitry.igayev/Movies");

        // Filter to get only .srt files
        File[] srtFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".srt"));

        if (srtFiles == null) {
            System.out.println("No .srt files found or an I/O error occurred.");
            return;
        }

        for (File srtFile : srtFiles) {
            String outputFileName = srtFile.getName().replaceFirst("[.][^.]+$", "") + ".pdf";
            File outputFile = new File(directory, outputFileName);

            convertFileToPdf(srtFile, outputFile);
        }
    }

    private static void convertFileToPdf(File inputFile, File outputFile) {
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

            saveAsPdf(outputText.toString().trim(), outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveAsPdf(String text, File outputFile) throws IOException {
        PdfWriter writer = new PdfWriter(outputFile);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);

        // Set margins
        document.setMargins(10, 10, 10, 10);

        // Set font size only
        Paragraph paragraph = new Paragraph(text).setFontSize(18);

        document.add(paragraph);
        document.close();
    }

    private static String convertBlock(String block) {
        String[] lines = block.split("\n");

        StringBuilder text = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            // Skip timestamps and sequence numbers
            if (isTimestamp(lines[i]) || isSequenceNumber(lines[i])) {
                continue;
            }

            text.append(lines[i]).append(" ");
        }

        String result = text.toString().trim();

        // Remove blocks that start with ( and end with )
        if (result.startsWith("(") && result.endsWith(")")) {
            return "";
        }

        // Remove blocks that start with [ and end with ]
        if (result.startsWith("[") && result.endsWith("]")) {
            return "";
        }

        return result;
    }

    private static boolean isTimestamp(String line) {
        return line.matches("^\\d{2}:\\d{2}:\\d{2},\\d{3} --> \\d{2}:\\d{2}:\\d{2},\\d{3}$");
    }

    private static boolean isSequenceNumber(String line) {
        return line.matches("^\\d+$");
    }
}
