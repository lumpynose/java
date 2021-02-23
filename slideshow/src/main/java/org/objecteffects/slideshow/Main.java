package org.objecteffects.slideshow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/*
 * HelloWorldSwing.java requires no other files.
 */

public class Main {
    static int pause = 5;
    private static String dir = "/home/rusty/pics";

    public static void main(final String[] args) throws ParseException {
        getOptions(args);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Slideshow.createAndShowGUI();
            }
        });
    }

    private static void getOptions(final String[] args) throws ParseException {
        final var options = new Options();
        options.addOption("pause", true, "pause period for each picture");
        options.addOption("dir", true, "directory of pictures");

        final CommandLineParser parser = new DefaultParser();
        final var cmd = parser.parse(options, args);

        if (cmd.hasOption("dir")) {
            Main.dir = cmd.getOptionValue("dir");
        }

        if (cmd.hasOption("pause")) {
            Main.pause = Integer.parseInt(cmd.getOptionValue("pause"));
        }
    }

    static List<Path> getFiles() {
        final List<Path> result;

        try (var walk = Files.walk(Paths.get(dir))) {
            result = walk.filter(p -> isImgFile(p))
                    .collect(Collectors.toList());
        } catch (final IOException e) {
            e.printStackTrace();

            return Collections.emptyList();
        }

        System.out.printf("%d\n", Integer.valueOf(result.size()));

        return result;
    }

    private static boolean isImgFile(final Path path) {
        if (path.getFileName().toString().endsWith(".jpg")) {
            return true;
        }

        if (path.getFileName().toString().endsWith(".JPG")) {
            return true;
        }

        if (path.getFileName().toString().endsWith(".gif")) {
            return true;
        }

        if (path.getFileName().toString().endsWith(".GIF")) {
            return true;
        }

        if (path.getFileName().toString().endsWith(".jpe")) {
            return true;
        }

        if (path.getFileName().toString().endsWith(".JPE")) {
            return true;
        }

        if (path.getFileName().toString().endsWith(".jpeg")) {
            return true;
        }

        if (path.getFileName().toString().endsWith(".png")) {
            return true;
        }

        // System.out.printf("%s\n", path.getFileName().toString());

        return false;
    }
}