package com.igayev.srtconverter.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.File;

@Data
@Slf4j
public class Config {

    // Command line options
    public static final Option HELP_OPTION =
            new Option("h", "help", false,
                    "Print this help message");
    public static final Option FOLDER_OPTION =
            new Option("f", "folder", true,
                    "Input folder name which is located in the user's home directory");
    public static final Option PATH_OPTION =
            new Option("p", "path", true,
                    "Input folder path");
    public static final Option OUTPUT_OPTION =
            new Option("o", "output", true,
                    "Output file format. Possible values: txt, pdf, epub");
    public static final Options OPTIONS = new Options()
            .addOption(HELP_OPTION)
            .addOption(FOLDER_OPTION)
            .addOption(PATH_OPTION)
            .addOption(OUTPUT_OPTION);


    public enum OutputFormat {
        TXT,
        PDF,
        EPUB
    }

    private File inputFolder;
    private OutputFormat outputFormat;

    public Config(String[] args) {
        parseOptions(args);
    }

    private void parseOptions(String[] args) {
        try {
            CommandLine cmd = new DefaultParser().parse(OPTIONS, args);

            if (cmd.hasOption(FOLDER_OPTION) && cmd.hasOption(PATH_OPTION)) {
                throw new Exception("Both input -f/--folder and -p/--path are present. Only one of them is allowed.");
            }

            if (!cmd.hasOption(FOLDER_OPTION) && !cmd.hasOption(PATH_OPTION)) {
                throw new Exception("-f/--folder or -p/--path is required.");
            }

            if (cmd.hasOption(FOLDER_OPTION)) {
                inputFolder = new File(new File(System.getProperty("user.home")), cmd.getOptionValue(FOLDER_OPTION));
            } else {
                inputFolder = new File(cmd.getOptionValue(PATH_OPTION));
            }

            if (!inputFolder.exists() || !inputFolder.isDirectory()) {
                throw new Exception("Specified input path is not a valid directory or doesn't exist.");
            }

            outputFormat = OutputFormat.valueOf(cmd.getOptionValue("o").toUpperCase());

        } catch (Exception e) {
            log.error("An error occurred.", e);
        }
    }


}
