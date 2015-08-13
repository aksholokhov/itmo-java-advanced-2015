package ru.ifmo.ctddev.sholokhov.uifilecopy;

import javax.swing.*;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiFunction;

/**
 * Here's the helping class, which implements coping of the files.
 * I use in in my {@link ru.ifmo.ctddev.sholokhov.copy.UIFileCopy}
 */

public class Helper {

    BiFunction<Long, Long, Void> interface_updater;
    final long[] total_size = {0, 0};
    public boolean copying_in_progress = false;
    static String filename_format = "Файл %d/%d: %s";
    JLabel filename_label;

    /**
     * Main constructor.
     * @param label - component, which displays file name in your UI
     * @param biFunction - function which updates your UI.
     */

    public Helper(JLabel label, BiFunction<Long, Long, Void> biFunction) {
        this.filename_label = label;
        this.interface_updater = biFunction;
    }

    /**
     * Copies one {@code file} from one directory to another
     * @param file - path to the copying file
     * @param from - path to the source directory
     * @param to - path to the destination directory
     */
    private void copy_single_file(Path file, Path from, Path to) {
        Path dest = Paths.get(to.toString() + "/" + from.relativize(file).toString());

        try {
            Files.deleteIfExists(dest);
            Files.createDirectories(dest.getParent());
            Files.createFile(dest);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            InputStream reader = Files.newInputStream(file);
            OutputStream writer = Files.newOutputStream(dest);

            byte[] buffer = new byte[32768];
            int readed;
            while ((readed = reader.read(buffer)) != -1) {
                if (!copying_in_progress) {
                    return;
                }
                writer.write(buffer, 0, readed);
                total_size[0] += readed;
                interface_updater.apply(total_size[0], total_size[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function which walks in the {@code src} file directory and calls
     * the {@code copy_single_file} to every file in direcotry
     * @param src - source directory which are being copied
     * @param dst - destination directory
     */
    public void multiple_copy(String src, String dst) {
        final Path from = Paths.get(src);
        final Path to = Paths.get(dst);


        copying_in_progress = true;
        interface_updater.apply(0L, 1L);
        final int[] counter = new int[2];
        counter[0] = 0;

        try {
            //Count total size for copying
            Files.walkFileTree(from, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!copying_in_progress) {
                        return FileVisitResult.TERMINATE;
                    }
                    total_size[1] += Files.size(file);
                    counter[0]++;
                    return FileVisitResult.CONTINUE;
                }
            });

            counter[1] = 1;

            total_size[1] = Math.max(total_size[1], 1L);

            //Copy files one by one
            Files.walkFileTree(from, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!copying_in_progress) {
                        return FileVisitResult.TERMINATE;
                    }
                    filename_label.setText(String.format(filename_format,counter[1], counter[0], file.getFileName().toString()));
                    copy_single_file(file, from, to);
                    counter[1]++;
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        copying_in_progress = false;
    }
}