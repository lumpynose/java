package org.objecteffects.slideshow;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.StringUtils;

public class Slideshow extends JPanel implements ActionListener,
        WindowListener,
        ChangeListener {
    private static final long serialVersionUID = 1L;
    private final JLabel label;
    private final int delay = Main.pause * 1000;
    private final Timer timer;
    private final Dimension windowSize;
    private int numPics = 0;
    private int picNum = 0;
    private List<Path> paths;

    static void createAndShowGUI() {
        // Create and set up the window.
        final var frame = new JFrame("Slideshow");

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final var windowSize = Toolkit.getDefaultToolkit().getScreenSize();

        windowSize.setSize(windowSize.getWidth(),
                windowSize.getHeight());

        System.out.printf("width=%d, height=%d\n",
                Integer.valueOf((int) windowSize.getWidth()),
                Integer.valueOf((int) windowSize.getHeight()));

        frame.setPreferredSize(windowSize);
        frame.setMinimumSize(windowSize);

        final var sspanel = new Slideshow(windowSize);

        sspanel.setBackground(Color.BLACK);

        final var pane = frame.getContentPane();

        pane.setBackground(Color.BLACK);

        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

        pane.add(Box.createVerticalGlue());
        pane.add(Box.createHorizontalGlue());

        pane.add(sspanel);
        pane.add(Box.createHorizontalGlue());

        // Display the window.
        frame.pack();
        frame.setVisible(true);

        sspanel.startSlideshow();
    }

    public Slideshow(final Dimension _windowSize) {
        this.label = new JLabel("label");

        add(this.label);

        this.windowSize = _windowSize;

        this.timer = new Timer(this.delay, this);

        this.timer.setCoalesce(true);
    }

    public void startSlideshow() {
        this.paths = Main.getFiles();

        Collections.shuffle(this.paths);

        this.numPics = this.paths.size();

        System.out.printf("%d\n", Integer.valueOf(this.numPics));

        this.label.setText(StringUtils.EMPTY);

        this.picNum = 0;

        this.timer.start();
    }

    void updatePicture() {
        final var path = this.paths.get(this.picNum);

        System.out.printf("%s\n", path.toFile().toString());
        final var icon = new ImageIcon(path.toFile().toString());

        if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.out.printf("can't load image: %s\n",
                    path.toFile().toString());
        } else {
            final var scaledDimension = getScaledDimension(
                    new Dimension(icon.getIconWidth(),
                            icon.getIconHeight()),
                    this.windowSize);

            final var image = icon.getImage();
            final var newimg = image.getScaledInstance(
                    (int) scaledDimension.getWidth(),
                    (int) scaledDimension.getHeight(),
                    java.awt.Image.SCALE_SMOOTH);

            this.label.setIcon(new ImageIcon(newimg));
        }

        this.picNum++;

        if (this.picNum >= this.paths.size()) {
            this.paths = Main.getFiles();

            this.picNum = 0;
        }
    }

    /** Add a listener for window events. */
    void addWindowListener(final Window w) {
        w.addWindowListener(this);
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        updatePicture();

        this.timer.restart();
    }

    @Override
    public void windowOpened(final WindowEvent e) {
        System.out.printf("window opened\n");
    }

    @Override
    public void windowClosing(final WindowEvent e) {
        System.out.printf("window closing\n");
    }

    @Override
    public void windowClosed(final WindowEvent e) {
        System.out.printf("window closed\n");
    }

    @Override
    public void windowIconified(final WindowEvent e) {
        System.out.printf("window iconified\n");
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
        System.out.printf("window deiconified\n");
    }

    @Override
    public void windowActivated(final WindowEvent e) {
        System.out.printf("window activated\n");
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
        System.out.printf("window deactivated\n");
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
        System.out.printf("state changed\n");
    }

    @SuppressWarnings("unused")
    private static Dimension getOffset(final Dimension imgSize,
            final Dimension screenSize) {
        var hDiff = 0;
        var vDiff = 0;

        if (screenSize.getWidth() > imgSize.getWidth()) {
            hDiff = (int) (screenSize.getWidth() - imgSize.getWidth());
        }

        if (screenSize.getHeight() > imgSize.getHeight()) {
            vDiff = (int) (screenSize.getHeight() - imgSize.getHeight());
        }

        final var offset = new Dimension(hDiff / 2, vDiff / 2);

        return offset;
    }

    private static Dimension getScaledDimension(final Dimension imgSize,
            final Dimension boundary) {
        final var scaledDimension = new Dimension(imgSize);

        // first check if we need to scale width
        if (imgSize.width > boundary.width) {
            // scale width to fit
            scaledDimension.width = boundary.width;
            // scale height to maintain aspect ratio
            scaledDimension.height = scaledDimension.width * imgSize.height
                    / imgSize.width;
        }

        // then check if we need to scale even with the new height
        if (scaledDimension.height > boundary.height) {
            // scale height to fit instead
            scaledDimension.height = boundary.height;
            // scale width to maintain aspect ratio
            scaledDimension.width = scaledDimension.height * imgSize.width
                    / imgSize.height;
        }

        return scaledDimension;
    }
}
