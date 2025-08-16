package net.buildabrowser.babbrowser.browser;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.buildabrowser.babbrowser.browser.dom.Document;
import net.buildabrowser.babbrowser.browser.net.ProtocolRegistry;
import net.buildabrowser.babbrowser.browser.parser.HTMLParser;

public class Main {
  
  public static void main(String[] args) throws IOException, URISyntaxException {
    ProtocolRegistry protocolRegistry = ProtocolRegistry.create();
    URL url = new URI(args[0]).toURL();
    try (InputStream inputStream = protocolRegistry.request(url)) {
      Document document = HTMLParser.create().parse(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
      showWindow(document.toString());
    }
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
    frame.add(textArea);
    frame.setVisible(true);
  }

}
