package com.codeforces.iomarkup;

import com.codeforces.iomarkup.daemon.IoMarkupDaemon;
import com.codeforces.iomarkup.exec.TargetComponent;
import com.codeforces.iomarkup.exec.TargetLanguage;
import com.codeforces.iomarkup.generation.TranslatedFile;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class IoMarkupCli {
    private static final Options options = new Options();

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            printHelpAndExit(1);
            return;
        }

        if (cmd.hasOption("h")) {
            printHelpAndExit(0);
            return;
        }

        if (cmd.hasOption("d")) {
            handleDaemonMode(cmd);
        } else {
            handleCliMode(cmd);
        }
    }

    private static void handleCliMode(CommandLine cmd) {
        if (!cmd.hasOption("t") || !cmd.hasOption("c")) {
            printHelpAndExit(5);
            return;
        }

        IoMarkup ioMarkup;
        try {
            if (cmd.hasOption("f")) {
                Path path = Path.of(cmd.getOptionValue("f"));
                ioMarkup = new IoMarkup(path);
            } else {
                ioMarkup = new IoMarkup(new InputStreamReader(System.in));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
            return;
        }

        Collection<TranslatedFile> result;
        try {
            result = ioMarkup.generate(
                    TargetComponent.valueOf(cmd.getOptionValue("c").toUpperCase()),
                    TargetLanguage.valueOf(cmd.getOptionValue("t").toUpperCase())
            );
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.exit(3);
            return;
        }

        if (cmd.hasOption("o")) {
            Path basePath = Path.of(cmd.getOptionValue("o"));
            for (TranslatedFile translatedFile : result) {
                try {
                    Files.writeString(basePath.resolve(translatedFile.name()), translatedFile.content());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(4);
                    return;
                }
            }
        } else {
            for (TranslatedFile translatedFile : result) {
                System.out.println(translatedFile.content());
            }
        }
    }

    private static void handleDaemonMode(CommandLine cmd) {
        if (!cmd.hasOption("p")) {
            printHelpAndExit(6);
            return;
        }

        int port;
        try {
            port = Integer.parseInt(cmd.getOptionValue("p"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            printHelpAndExit(7);
            return;
        }

        if (port < 1 || port > 65535) {
            printHelpAndExit(8);
            return;
        }

        var daemon = new IoMarkupDaemon(port);
        daemon.run();
    }

    private static void printHelpAndExit(int exitCode) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ant", options);
        System.exit(exitCode);
    }

    static {
        options.addOption(
                Option.builder("f")
                        .longOpt("file")
                        .required(false)
                        .desc("Input file with input-output markup. If not specified, markup will be read from stdin.")
                        .type(String.class)
                        .hasArg(true)
                        .numberOfArgs(1)
                        .argName("file")
                        .build()
        );

        options.addOption(
                Option.builder("t")
                        .longOpt("target")
                        .required(false)
                        .desc("Target language. Can be 'cpp', 'java' or 'python'")
                        .type(String.class)
                        .hasArg(true)
                        .numberOfArgs(1)
                        .argName("lang")
                        .build()
        );

        options.addOption(
                Option.builder("c")
                        .longOpt("component")
                        .required(false)
                        .desc("Target component. Can be 'checker', 'validator', 'solution' or 'grader'")
                        .type(String.class)
                        .hasArg(true)
                        .numberOfArgs(1)
                        .argName("component")
                        .build()
        );

        options.addOption(
                Option.builder("o")
                        .longOpt("output")
                        .required(false)
                        .desc("Output directory. If not specified, results will be written to stdout.")
                        .type(String.class)
                        .hasArg(true)
                        .numberOfArgs(1)
                        .argName("directory")
                        .build()
        );

        options.addOption(
                Option.builder("h")
                        .longOpt("help")
                        .required(false)
                        .desc("Prints help. All other arguments will be ignored.")
                        .build()
        );

        options.addOption(
                Option.builder("d")
                        .longOpt("daemon")
                        .required(false)
                        .desc("Run as daemon")
                        .build()
        );

        options.addOption(
                Option.builder("p")
                        .longOpt("port")
                        .required(false)
                        .desc("Port to listen for daemon mode")
                        .hasArg(true)
                        .numberOfArgs(1)
                        .type(int.class)
                        .build()
        );
    }
}
