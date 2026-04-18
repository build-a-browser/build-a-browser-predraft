package net.buildabrowser.babbrowser.browser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Supplier;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.buildabrowser.babbrowser.browser.chrome.WindowSetGUI;
import net.buildabrowser.babbrowser.browser.net.imp.FetchBackendImp;
import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.Window.WindowOptions;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.common.util.CommonUtil;
import net.buildabrowser.babbrowser.cssbase.cssom.StyleSheetList;
import net.buildabrowser.babbrowser.fetch.FetchEngine;
import net.buildabrowser.babbrowser.network.encoding.ContentEncodingRegistry;
import net.buildabrowser.babbrowser.render.RenderingEngine;
import net.buildabrowser.babbrowser.render.loader.DocumentLoaderRegistry;
import net.buildabrowser.babbrowser.render.loader.loaders.HTMLDocumentLoader;
import net.buildabrowser.babbrowser.render.paint.Painter;
import net.buildabrowser.babbrowser.render.paint.java2d.Java2DPainter;
import net.buildabrowser.babbrowser.render.paint.skija.SkijaPainter;

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

    DocumentLoaderRegistry loaderRegistry = DocumentLoaderRegistry.create();
    loaderRegistry.register("text/html", new HTMLDocumentLoader());

    ContentEncodingRegistry registry = ContentEncodingRegistry.createDefault();
    FetchEngine fetchEngine = FetchEngine.create(new FetchBackendImp(registry));
    Supplier<StyleSheetList> uaStyleSheetsSupplier = () -> CommonUtil.rethrow(() -> loadUAStyleSheets());

    RenderingEngine renderingEngine = RenderingEngine.create(
      fetchEngine, painter, loaderRegistry, uaStyleSheetsSupplier);
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

  private static StyleSheetList loadUAStyleSheets() throws IOException {
    try (
      Reader reader = new InputStreamReader(
        ClassLoader.getSystemClassLoader().getResourceAsStream("ua/ua.css"))
    ) {
      return StyleSheetList.createFromReader(reader);
    }
  }

}