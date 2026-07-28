package net.buildabrowser.babbrowser.browser;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.buildabrowser.babbrowser.browser.chrome.WindowSetGUI;
import net.buildabrowser.babbrowser.browser.clipboard.AWTClipboardProvider;
import net.buildabrowser.babbrowser.browser.net.imp.FetchBackendImp;
import net.buildabrowser.babbrowser.browser.net.imp.PublicSuffixListImp;
import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.Window.WindowOptions;
import net.buildabrowser.babbrowser.browser.util.FileUtil;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.cookies.CookieStore;
import net.buildabrowser.babbrowser.debugger.core.Debugger;
import net.buildabrowser.babbrowser.debugger.swing.SwingDebugger;
import net.buildabrowser.babbrowser.fetch.FetchBackend;
import net.buildabrowser.babbrowser.fetch.FetchConfig;
import net.buildabrowser.babbrowser.network.encoding.ContentEncodingRegistry;
import net.buildabrowser.babbrowser.painter.core.ComponentPainter;
import net.buildabrowser.babbrowser.renderer.RenderingEngine;
import net.buildabrowser.babbrowser.renderer.clipboard.ClipboardProvider;
import net.buildabrowser.babbrowser.renderer.loader.DocumentLoaderRegistry;

public class Main {
  
  public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
    BrowserArguments arguments = BrowserArguments.parse(args);
    if (arguments == null) return;

    System.setProperty("org.lwjgl.opengl.contextAPI", "GLX");
    setLookAndFeel();

    URI profilePath = FileUtil.asDirectory(arguments.profilePath());
    new File(profilePath.getSchemeSpecificPart()).mkdirs();

    ComponentPainter<Component> painter = arguments.painter().get();
    CookieStore cookieStore = arguments.cookieStore().get(
      profilePath, new PublicSuffixListImp());

    ClipboardProvider<?> clipboardProvider = new AWTClipboardProvider();
    Debugger debugger = new SwingDebugger();

    DocumentLoaderRegistry loaderRegistry = DocumentLoaderRegistry.createDefault();
    ContentEncodingRegistry registry = ContentEncodingRegistry.createDefault();
    
    FetchBackend fetchBackend = new FetchBackendImp(registry);
    FetchConfig fetchConfig = new FetchConfig(
      fetchBackend, _ -> true, cookieStore);

    cookieStore.initialize();
    RenderingEngine renderingEngine = RenderingEngine.create(
      fetchConfig,
      Executors::newVirtualThreadPerTaskExecutor,
      painter,
      loaderRegistry,
      ClassLoader.getSystemClassLoader()::getResourceAsStream,
      clipboardProvider);
    BrowserInstance browserInstance = BrowserInstance.create(renderingEngine);
  
    WindowSet windowSet = WindowSet.create(browserInstance);
    Window window = windowSet.openWindow(new WindowOptions(false));
    for (URI url: arguments.launchPaths()) {
      window.openTab().navigate(url);
    }

    WindowSetGUI.create(windowSet, painter, debugger);
  }

  private static void setLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
      throw new RuntimeException(e);
    }
  }

}