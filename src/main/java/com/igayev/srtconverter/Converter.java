package com.igayev.srtconverter;

import com.igayev.srtconverter.common.Config;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;

import static com.igayev.srtconverter.common.Config.OUTPUT_OPTION;
import static com.igayev.srtconverter.processor.Epub.saveAsEpub;
import static com.igayev.srtconverter.processor.Pdf.saveAsPdf;
import static com.igayev.srtconverter.processor.Txt.saveAsTxt;
import static com.igayev.srtconverter.processor.Txt.convertSrtFileToText;


@Log4j2
public class Converter {
    public static void main(String[] args) {
        try {
            new Converter(new Config(args)).convert();
        } catch (Exception e) {
            log.error("An error occurred.", e);
        }
    }

    private static final String FILE_NAME_REGEX = "[.][^.]+$";
    private final Config config;

    public Converter(Config config) {
        this.config = config;
    }

    public void convert() throws IOException {

        log.info("Converting all *.srt files to {} in {}",
                config.getOutputFormat().name(),
                config.getInputFolder().getName());

        // get all srt files from the input folder
        File[] srtFiles = getAllSrtFilesFromFolder(config.getInputFolder());

        if (srtFiles.length == 0) {
            throw new RuntimeException("No *.srt files found in " + config.getInputFolder().getName());
        }

        // convert each srt file to text and save it to the output folder
        for (File srtFile : srtFiles) {
            String outputFileName = getOutputFileName(srtFile);
            String outputText = convertSrtFileToText(srtFile);
            File outputFile = new File(config.getInputFolder(), outputFileName);
            switch (config.getOutputFormat()) {
                case PDF -> saveAsPdf(outputText, outputFile);
                case TXT -> saveAsTxt(outputText, outputFile);
                case EPUB -> saveAsEpub(outputText, outputFile);
                default -> log.error("Invalid {}/{} value.", OUTPUT_OPTION.getOpt(), OUTPUT_OPTION.getLongOpt());
            }
            log.info("{} OK", outputFile.getName());
        }

        log.info("Converted {} file(s)", srtFiles.length);
    }

    public static File[] getAllSrtFilesFromFolder(File directory) {

        File[] srtFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".srt"));
        if (srtFiles == null) {
            throw new RuntimeException("No *.srt files found or an I/O error occurred.");
        }
        return srtFiles;
    }

    private String getOutputFileName(File inputFile) {
        return String.format("%s.%s",
                inputFile.getName().replaceFirst(FILE_NAME_REGEX, ""),
                config.getOutputFormat().name().toLowerCase());
    }


}
