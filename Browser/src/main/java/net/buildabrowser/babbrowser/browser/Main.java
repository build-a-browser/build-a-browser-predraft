package net.buildabrowser.babbrowser.browser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
  public static void main(String[] args) throws IOException, URISyntaxException {
    URL url = new URI(args[0]).toURL();
    showWindow(loadURL(url));
  }

  private static void showWindow(String text) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
      throw new RuntimeException(e);
    }

    JTextPane textPane = new JTextPane();
    textPane.setText(text);

    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame frame = new JFrame("BuildABrowser Test Program");
    frame.setUndecorated(true);
    frame.add(textPane);
    frame.pack();
    frame.setVisible(true);
  }

  private static String loadURL(URL url) throws IOException {
    String filePath = url.getPath();

    try (InputStream fInputStream = new FileInputStream(filePath)) {
      return new String(fInputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

}
