import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FilenameFilter;

public class SrtToTxt {

    public static void main(String[] args) {
        File directory = new File("/Users/dmitry.igayev/Movies");

        // Filter to get only .srt files
        File[] srtFiles = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".srt");
            }
        });

        if (srtFiles == null) {
            System.out.println("No .srt files found or an I/O error occurred.");
            return;
        }

        for (File srtFile : srtFiles) {
            String outputFileName = srtFile.getName().replaceFirst("[.][^.]+$", "") + ".txt";
            File outputFile = new File(directory, outputFileName);

            convertFile(srtFile, outputFile);
        }
    }

    private static void convertFile(File inputFile, File outputFile) {
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

            // Handling the last block
            String convertedBlock = convertBlock(block.toString().trim());
            if (!convertedBlock.isEmpty()) {
                outputText.append(convertedBlock).append("\n");
            }

            // Write the output to the new file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                writer.write(outputText.toString().trim());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
