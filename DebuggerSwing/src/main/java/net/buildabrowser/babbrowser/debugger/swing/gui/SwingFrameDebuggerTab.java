package net.buildabrowser.babbrowser.debugger.swing.gui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.buildabrowser.babbrowser.debugger.swing.SwingFrameDebugger;

public class SwingFrameDebuggerTab extends JPanel {

  private final SwingFrameDebugger debugger;

  public SwingFrameDebuggerTab(SwingFrameDebugger debugger) {
    this.debugger = debugger;
    JComponent tree = JLazyDiffTree.createJLazyDiffTree(debugger.nodeTree());
    JScrollPane scrollPane = new JScrollPane(tree);
    scrollPane.setViewportView(tree);
    setLayout(new BorderLayout());
    add(scrollPane, BorderLayout.CENTER);
  }

  public SwingFrameDebugger debugger() {
    return this.debugger;
  }

}
