# SRT Converter

SRT Converter is a command-line tool written in Java for converting subtitle files in the SRT format into different output formats including PDF, TXT, and EPUB.

## Features

- Converts all SRT files in a specified folder.
- Supports various output formats: TXT, PDF, EPUB.
- Simple command-line interface.

## Prerequisites

Ensure you have Java installed on your machine. The application is built using Java, and you need a Java Runtime Environment (JRE) to run it.

## How to Use

Clone the repository or download the compiled JAR file.

### Command Line Options

- `-h, --help`: Prints the help message.
- `-f, --folder`: Input folder name located in the user's home directory.
- `-p, --path`: Full path to the input folder.
- `-o, --output`: Output file format. Possible values are `txt`, `pdf`, and `epub`.

You must specify either `-f` or `-p` for the input folder, and `-o` for the output format.

### Example Usage

```shell
java -jar srtconverter.jar -f subtitles -o pdf
