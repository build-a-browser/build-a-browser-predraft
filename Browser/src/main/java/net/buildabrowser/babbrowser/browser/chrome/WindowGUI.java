package net.buildabrowser.babbrowser.browser.chrome;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import net.buildabrowser.babbrowser.browser.uistate.Tab;
import net.buildabrowser.babbrowser.browser.uistate.Window;
import net.buildabrowser.babbrowser.browser.uistate.event.WindowMutationEventListener;

public class WindowGUI extends JFrame implements WindowMutationEventListener {
  
  private final JTabbedPane tabbedPane = new JTabbedPane();

  @SuppressWarnings("unused")
  private final Window window;

  private WindowGUI(Window window) {
    super("BuildABrowser Test Program");
    this.window = window;

    this.setSize(new Dimension(800, 500));
    this.setMaximumSize(new Dimension(800, 500));
    window.addWindowMutationEventListener(this, true);
    
    this.add(tabbedPane);
  }

  public void showWindow() {
    this.setVisible(true);
  }
  
  @Override
  public void onTabAdded(Window window, Tab tab) {
    TabGUI tabGUI = TabGUI.create(tab);
    tabbedPane.addTab(tab.getName(), tabGUI);
  }

  @Override
  public void onClose(Window window) {
    dispose();
  }

  public static WindowGUI create(Window window) {
    JFrame.setDefaultLookAndFeelDecorated(true);
    return new WindowGUI(window);
  }

}
