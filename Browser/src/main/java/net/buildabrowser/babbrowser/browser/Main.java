package net.buildabrowser.babbrowser.browser;

import java.awt.Component;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.buildabrowser.babbrowser.browser.chrome.WindowSetGUI;
import net.buildabrowser.babbrowser.browser.net.imp.FetchBackendImp;
import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.Window.WindowOptions;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.fetch.FetchBackend;
import net.buildabrowser.babbrowser.network.encoding.ContentEncodingRegistry;
import net.buildabrowser.babbrowser.painter.core.ComponentPainter;
import net.buildabrowser.babbrowser.painter.java2d.Java2DPainter;
import net.buildabrowser.babbrowser.painter.skija.SkijaAWTPainter;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;

public class Main {
  
  public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
    System.setProperty("org.lwjgl.opengl.contextAPI", "GLX");
    setLookAndFeel();

    // TODO: Use a proper argument parser
    boolean useJava2d = false;
    boolean isSoftwareRendered = false;
    for (String arg: args) {
      useJava2d = useJava2d || arg.equals("--use-java2d");
      isSoftwareRendered = isSoftwareRendered || arg.equals("--use-software-rendering");
    }

    ComponentPainter<Component> painter = useJava2d ?
      new Java2DPainter() :
      new SkijaAWTPainter(isSoftwareRendered, false);

    DocumentLoaderRegistry loaderRegistry = DocumentLoaderRegistry.createDefault();
    ContentEncodingRegistry registry = ContentEncodingRegistry.createDefault();
    FetchBackend fetchBackend = new FetchBackendImp(registry);

    RenderingEngine renderingEngine = RenderingEngine.create(
      fetchBackend,
      Executors::newVirtualThreadPerTaskExecutor,
      painter,
      loaderRegistry,
      ClassLoader.getSystemClassLoader()::getResourceAsStream);
    BrowserInstance browserInstance = BrowserInstance.create(renderingEngine);
  
    WindowSet windowSet = WindowSet.create(browserInstance);
    Window window = windowSet.openWindow(new WindowOptions(false));
    for (String urlStr: args) {
      if (urlStr.startsWith("--")) continue;
      URI url = new URI(urlStr);
      window.openTab().navigate(url);
    }

    WindowSetGUI.create(windowSet, painter);
  }

  private static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
      throw new RuntimeException(e);
    }
  }

}