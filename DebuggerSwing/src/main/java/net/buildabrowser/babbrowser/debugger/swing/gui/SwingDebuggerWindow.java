package net.buildabrowser.babbrowser.debugger.swing.gui;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import net.buildabrowser.babbrowser.debugger.swing.SwingDebugger;
import net.buildabrowser.babbrowser.debugger.swing.SwingDebugger.SwingDebuggerListener;
import net.buildabrowser.babbrowser.debugger.swing.SwingFrameDebugger;

public class SwingDebuggerWindow extends JFrame {

  private JTabbedPane tabbedPane = new JTabbedPane();
  
  public SwingDebuggerWindow(SwingDebugger swingDebugger) {
    // TODO: Would be nice if we got this title from the actual browser
    super("BuildABrowser Test Program - Debugger");
    add(tabbedPane);
    setSize(800, 500);

    swingDebugger.attachListener(new WindowDebuggerListener());
  }
  
  private class WindowDebuggerListener implements SwingDebuggerListener {

    @Override
    public void onAttach(SwingFrameDebugger frameDebugger) {
      SwingUtilities.invokeLater(() -> {
        JComponent tab = new SwingFrameDebuggerTab(frameDebugger);
        tabbedPane.add("Tab", tab);
        tabbedPane.setSelectedComponent(tab);
        setVisible(true);
      });
    }

    @Override
    public void onDetach(SwingFrameDebugger frameDebugger) {
      // TODO: Remove
    }

  }

}
