package net.buildabrowser.babbrowser.browser;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.buildabrowser.babbrowser.browser.net.ProtocolRegistry;

public class Main {
  public static void main(String[] args) throws IOException, URISyntaxException {
    ProtocolRegistry protocolRegistry = ProtocolRegistry.create();
    URL url = new URI(args[0]).toURL();
    showWindow(loadURLText(protocolRegistry, url));
  }

  private static void showWindow(String text) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
      throw new RuntimeException(e);
    }

    JTextArea textArea = new JTextArea(text);
    textArea.setLineWrap(true);

    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame frame = new JFrame("BuildABrowser Test Program");
    frame.setSize(new Dimension(800, 500));
    frame.setMaximumSize(new Dimension(800, 500));
    frame.setUndecorated(true);
    frame.add(textArea);
    frame.setVisible(true);
  }

  private static String loadURLText(ProtocolRegistry protocolRegistry, URL url) throws IOException {
    try (InputStream inputStream = protocolRegistry.request(url)) {
      return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
  }

}
