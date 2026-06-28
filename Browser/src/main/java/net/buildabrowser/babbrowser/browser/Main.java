package net.buildabrowser.babbrowser.browser;

import java.awt.Dimension;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
  
  public static void main(String[] args) throws URISyntaxException, IOException {
    URI url = new URI(args[0]);

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
      throw new RuntimeException(e);
    }

    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame frame = new JFrame("BuildABrowser Test Program");
    frame.setSize(new Dimension(800, 500));
    frame.setUndecorated(true);
    String text = loadURL(url);

    JTextPane textPane = new JTextPane();
    textPane.setText(text);

    frame.add(textPane);
    frame.setVisible(true);
  }

  private static String loadURL(URI url) throws IOException {
    try (InputStream inputStream = request(url)) {
      return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

  private static InputStream request(URI url) throws IOException {
    return switch (url.getScheme()) {
      case "file" -> new FileInputStream(url.getPath());
      case "http" -> url.toURL().openConnection().getInputStream();
      case "https" -> url.toURL().openConnection().getInputStream();
      default -> throw new UnsupportedOperationException("Not Implemented!");
    };
  }

}
