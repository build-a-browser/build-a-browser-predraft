package net.buildabrowser.babbrowser.browser;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.buildabrowser.babbrowser.browser.chrome.WindowSetGUI;
import net.buildabrowser.babbrowser.browser.net.imp.PublicSuffixListImp;
import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.Window.WindowOptions;
import net.buildabrowser.babbrowser.browser.uistate.WindowSet;
import net.buildabrowser.babbrowser.browser.util.FileUtil;
import net.buildabrowser.babbrowser.cookies.CookieStore;
import net.buildabrowser.babbrowser.debugger.core.Debugger;
import net.buildabrowser.babbrowser.debugger.swing.SwingDebugger;
import net.buildabrowser.babbrowser.painter.core.ComponentPainter;

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
    
    Debugger debugger = new SwingDebugger();

    BrowserInstance browserInstance = BrowserInstance.create(
      profilePath, painter, cookieStore);
  
    WindowSet windowSet = browserInstance.windowSet();
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