package net.buildabrowser.babbrowser.browser;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
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
import net.buildabrowser.babbrowser.painter.core.CanvasCallbacks;
import net.buildabrowser.babbrowser.painter.core.ComponentPainter;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.painter.java2d.Java2DPainter;

public class Main {

  private static final long GRAPHICS_CHECK_TIMEOUT = 1500;
  
  public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
    BrowserArguments arguments = BrowserArguments.parse(args);
    if (arguments == null) return;

    System.setProperty("org.lwjgl.opengl.contextAPI", "GLX");
    setLookAndFeel();

    URI profilePath = FileUtil.asDirectory(arguments.profilePath());
    new File(profilePath.getSchemeSpecificPart()).mkdirs();

    ComponentPainter<Component> painter = arguments.painter().get();
      if (!testPainter(painter)) {
        JOptionPane pane = new JOptionPane(
          "Failed to initialize graphics backend. Falling back to Java2D - Your browsing experience will be significantly degraded.",
          JOptionPane.ERROR_MESSAGE
        );
        JDialog dialog = pane.createDialog("Graphics Initialization Failed!");
        dialog.setAlwaysOnTop(true);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        
        painter = new Java2DPainter();
    }

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

  private static boolean testPainter(ComponentPainter<Component> painter) {
    CompletableFuture<Boolean> future = new CompletableFuture<>();

    SwingUtilities.invokeLater(() -> {
      JFrame dummyFrame = new JFrame();
      try {
        Component dummyComponent = painter.createComponent(new CanvasCallbacks() {
          @Override 
          public void paint(PaintCanvas canvas) {
            future.complete(true);
            SwingUtilities.invokeLater(dummyFrame::dispose);
          }
        });

        dummyFrame.setSize(10, 10);
        dummyFrame.setUndecorated(true);
        dummyFrame.add(dummyComponent);
        dummyFrame.setVisible(true);

        new Thread(() -> {
          try {
            Thread.sleep(GRAPHICS_CHECK_TIMEOUT);
          } catch (InterruptedException e) {}
          if (dummyFrame.isVisible()) {
            dummyFrame.dispose();
          }
        }).start();
      } catch (Throwable t) {
        t.printStackTrace();
        future.complete(false);
      }
    });

    try {
      return future.get(GRAPHICS_CHECK_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      return false;
    }
  }

}