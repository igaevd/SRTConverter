package com.igayev.srtconverter.processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Txt {

    private static final String TIMESTAMP_REGEX = "^\\d{2}:\\d{2}:\\d{2},\\d{3} --> \\d{2}:\\d{2}:\\d{2},\\d{3}$";
    private static final String NUMBER_REGEX = "^\\d+$";

    public static void saveAsTxt(String text, File outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(text);
        }
    }

    public static String convertSrtFileToText(File inputFile) {

        StringBuilder outputText = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            StringBuilder block = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    String convertedBlock = convertBlock(block.toString().trim());
                    if (!convertedBlock.isEmpty()) {
                        outputText.append(convertedBlock).append(System.lineSeparator());
                        outputText.append(System.lineSeparator()); // Extra empty line between blocks
                    }
                    block.setLength(0);
                } else {
                    block.append(line).append(System.lineSeparator());
                }
            }

            String convertedBlock = convertBlock(block.toString().trim());
            if (!convertedBlock.isEmpty()) {
                outputText.append(convertedBlock).append(System.lineSeparator());
            }

            return outputText.toString().trim();

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static String convertBlock(String block) {
        String[] lines = block.split(System.lineSeparator());

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
        return line.matches(TIMESTAMP_REGEX);
    }

    private static boolean isSequenceNumber(String line) {
        return line.matches(NUMBER_REGEX);
    }


}
