package ru.ifmo.ctddev.sholokhov.uifilecopy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * My implementation of FileCopy utility. It has nice looking UI and
 * can display some statistic about copying process:
 *  File name (which are copying now)
 *  Elapsed time from start of copying process
 *  Estimate time of copying
 *  Average speed
 *  Current speed
 *  Progress bar
 *  and so on...
 * @author Sholokhov Alexey
 */

public class UIFileCopy {
    static int min_window_wight = 300;
    static int min_window_height = 180;
    static int progressbar_size = 10000;

    static String window_title = "Копирование...%d%%";
    static String elapsed_format = "Время работы %d секунд";
    static String average_speed_format = "Средняя скорость: %.3f мб/с";
    static String current_speed_format = "Текущая скорость: %.3f мб/с";
    static String estimate_format = "Осталось примерно %d секунд";

    /**
     * Main function. It wants form you two args:
     * argument[0] - file or directory you want to copy (full path)
     * argument[1] - path to the destination directory
     * @param args program arguments (see above)
     */

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Incorrect input format");
            return;
        }
        long start_time = System.currentTimeMillis();
        final long[] previous_time = {start_time};
        final long[] elapsed_downloaded_size = {0};

        String from = args[0];
        String to = args[1];

        JFrame window_frame = new JFrame();
        JLabel elapsed_time_label = new JLabel();
        JLabel average_speed_label = new JLabel();
        JLabel current_speed_label = new JLabel();
        JLabel estimate_time_label = new JLabel();
        JLabel filename_label = new JLabel();
        JProgressBar progressBar = new JProgressBar();
        JButton cancel_button = new JButton("Cancel");
        JPanel window_panel = new JPanel();

        window_frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        window_frame.setMinimumSize(new Dimension(min_window_wight, min_window_height));
        window_frame.setTitle(window_title);

        filename_label.setText(String.format(window_title, 0));
        elapsed_time_label.setText(String.format(elapsed_format, 0));
        average_speed_label.setText(String.format(average_speed_format, 0.0));
        current_speed_label.setText(String.format(current_speed_format, 0.0));
        estimate_time_label.setText(String.format(estimate_format, 0));


        window_panel.setLayout(new BoxLayout(window_panel, BoxLayout.Y_AXIS));
        window_panel.add(elapsed_time_label);
        window_panel.add(average_speed_label);
        window_panel.add(current_speed_label);
        window_panel.add(estimate_time_label);
        window_panel.add(filename_label);
        window_panel.add(progressBar);
        window_panel.add(cancel_button);

        progressBar.setMaximum(1);
        progressBar.setValue(0);

        Box box = new Box(BoxLayout.Y_AXIS);
        box.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        box.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        box.add(Box.createVerticalGlue());
        box.add(Box.createHorizontalGlue());
        box.add(window_panel);
        box.add(Box.createVerticalGlue());
        box.add(Box.createHorizontalGlue());
        window_frame.add(box);

        Helper helper = new Helper(filename_label, (pos, total) -> {
            progressBar.setValue((int) (pos * 1.0 / total * progressbar_size));
            progressBar.setMaximum(progressbar_size);
            window_frame.setTitle(String.format(window_title, 100L * pos / total));

            long current_time = System.currentTimeMillis();

            elapsed_time_label.setText(String.format(elapsed_format, (current_time - start_time) / 1000));

            if ((current_time - previous_time[0]) > 100) {

                double averageSpeed = pos / 1000.0 / (current_time - start_time);
                double currentSpeed = (pos - elapsed_downloaded_size[0]) / 1000.0 / (current_time - previous_time[0]);

                average_speed_label.setText(String.format(average_speed_format, averageSpeed));
                current_speed_label.setText(String.format(current_speed_format, currentSpeed));
                estimate_time_label.setText(String.format(estimate_format, (int) ((total - pos) / 1e6 / averageSpeed)));

                previous_time[0] = current_time;
                elapsed_downloaded_size[0] = pos;
                window_panel.updateUI();
            }
            return null;
        });

        cancel_button.addActionListener(e -> {
            window_frame.dispose();
            helper.copying_in_progress = false;
        });

        final int[] prevSizes = new int[3];
        final Font font = filename_label.getFont();
        prevSizes[0] = window_frame.getWidth();
        prevSizes[1] = window_frame.getHeight();

        window_frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                Component component = (Component) e.getSource();
                float deltaW = (float) component.getWidth() / (float) prevSizes[0];
                float deltaH = (float) component.getHeight() / (float) prevSizes[1];
                float delta = Math.min(deltaH, deltaW);
                if (delta > 1.05 || delta < 0.95) {
                    Font updFont = new Font(font.getName(), font.getStyle(), (int) (font.getSize() * delta));
                    filename_label.setFont(updFont);
                    average_speed_label.setFont(updFont);
                    current_speed_label.setFont(updFont);
                    elapsed_time_label.setFont(updFont);
                    estimate_time_label.setFont(updFont);
                    cancel_button.setFont(updFont);
                }

            }
        });

        window_frame.pack();
        window_frame.setVisible(true);
        helper.multiple_copy(from, to);
        window_frame.dispose();
    }
}