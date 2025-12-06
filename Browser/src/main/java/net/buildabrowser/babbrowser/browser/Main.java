package net.buildabrowser.babbrowser.browser;

import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.buildabrowser.babbrowser.browser.net.imp.ProtocolRegistryImp;
import net.buildabrowser.babbrowser.browser.network.ProtocolRegistry;
import net.buildabrowser.babbrowser.browser.render.Renderer;

public class Main {
  
  public static void main(String[] args) throws IOException, URISyntaxException {
    setLookAndFeel();

    ProtocolRegistry protocolRegistry = new ProtocolRegistryImp();
    URL url = new URI(args[0]).toURL();
    Renderer renderer = Renderer.create(protocolRegistry, url);
    showWindow(renderer.render());
  }

  private static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
      throw new RuntimeException(e);
    }
  }

  private static void showWindow(Component content) {
    JFrame.setDefaultLookAndFeelDecorated(true);
    JFrame frame = new JFrame("BuildABrowser Test Program");
    frame.setSize(new Dimension(800, 500));
    frame.setMaximumSize(new Dimension(800, 500));
    frame.add(content);
    frame.setVisible(true);
  }

}