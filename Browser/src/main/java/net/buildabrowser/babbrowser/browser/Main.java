package net.buildabrowser.babbrowser.browser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.buildabrowser.babbrowser.browser.chrome.WindowSetGUI;
import net.buildabrowser.babbrowser.browser.net.imp.FetchBackendImp;
import net.buildabrowser.babbrowser.browser.render.RenderingEngine;
import net.buildabrowser.babbrowser.browser.render.paint.Painter;
import net.buildabrowser.babbrowser.browser.render.paint.java2d.Java2DPainter;
import net.buildabrowser.babbrowser.browser.render.paint.skija.SkijaPainter;
import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.Window.WindowOptions;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.fetch.FetchEngine;

public class Main {
  
  public static void main(String[] args) throws IOException, URISyntaxException {
    setLookAndFeel();

    // TODO: Use a proper argument parser
    boolean useJava2d = false;
    for (String arg: args) {
      useJava2d = useJava2d || arg.equals("--use-java2d");
    }

    Painter painter = useJava2d ?
      new Java2DPainter() :
      new SkijaPainter();

    FetchEngine fetchEngine = FetchEngine.create(new FetchBackendImp());
    RenderingEngine renderingEngine = RenderingEngine.create(fetchEngine, painter);
    BrowserInstance browserInstance = BrowserInstance.create(renderingEngine);
  
    WindowSet windowSet = WindowSet.create(browserInstance);
    Window window = windowSet.openWindow(new WindowOptions(false));
    for (String urlStr: args) {
      if (urlStr.startsWith("--")) continue;
      URI url = new URI(urlStr);
      window.openTab().navigate(url);
    }

    WindowSetGUI.create(windowSet);
  }

  private static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
      throw new RuntimeException(e);
    }
  }

}