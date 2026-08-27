package net.buildabrowser.babbrowser.debugger.swing.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.WeightedStyleRule;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.debugger.core.DebugObject;
import net.buildabrowser.babbrowser.debugger.core.DebugObject.DebugObjectSelection;
import net.buildabrowser.babbrowser.debugger.core.DebugSnapshot;
import net.buildabrowser.babbrowser.debugger.swing.LazyDiffTree;
import net.buildabrowser.babbrowser.debugger.swing.SwingFrameDebugger;

public class SwingFrameDebuggerTab extends JPanel {

  private final SwingStyleView styleView = new SwingStyleView();
  private final List<JButton> ctsButtons = new ArrayList<>();

  private final SwingFrameDebugger debugger;

  private DebugObject prevDebugObject;

  public SwingFrameDebuggerTab(SwingFrameDebugger debugger) {
    this.debugger = debugger;
    setLayout(new BorderLayout());

    JComponent domView = createDOMView(debugger);
    JComponent boxView = createBoxView(debugger);

    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.add("DOM", domView);
    tabbedPane.add("Box", boxView);
    updateCTSButtonText();

    JSplitPane splitPane = new JSplitPane(
      JSplitPane.HORIZONTAL_SPLIT, tabbedPane, styleView);
    splitPane.setDividerLocation(0.6);
    splitPane.setResizeWeight(0.6);

    add(splitPane, BorderLayout.CENTER);
  }

  public SwingFrameDebugger debugger() {
    return this.debugger;
  }

  private JComponent createDOMView(SwingFrameDebugger debugger) {
    return createLazyTreeView(
      debugger.nodeTree(),
      node -> debugger.later(context -> onSelectDebugObject(
        context.debugObjectForNode(node))));
  }

  private JComponent createBoxView(SwingFrameDebugger debugger) {
    return createLazyTreeView(
      debugger.boxTree(),
      box -> debugger.later(context -> onSelectDebugObject(box)));
  }

  private <T> JComponent createLazyTreeView(
    LazyDiffTree<T> lazyDiffTree,
    Consumer<T> onItemSelection
  ) {
    JComponent tree = JLazyDiffTree.createJLazyDiffTree(
      lazyDiffTree, onItemSelection);
    JScrollPane scrollPane = new JScrollPane(tree);
    scrollPane.setViewportView(tree);

    JButton clickToSelectButton = new JButton();
    ctsButtons.add(clickToSelectButton);
    clickToSelectButton.addActionListener(_ -> {
      debugger.nodeSelection().toggleClickToSelectEnabled();
      updateCTSButtonText();
    });
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    buttonPanel.add(clickToSelectButton);

    JPanel treePanel = new JPanel();
    treePanel.setLayout(new BorderLayout());
    treePanel.add(buttonPanel, BorderLayout.NORTH);
    treePanel.add(scrollPane, BorderLayout.CENTER);
    
    return treePanel;
  }

  private void onSelectDebugObject(DebugObject debugObject) {
    DebugSnapshot snapshot = debugObject.snapshotDebugInfo();
    updateStyleInfo(snapshot);
    
    if (prevDebugObject != null) {
      prevDebugObject.markSelection(DebugObjectSelection.NONE);
    }
    
    debugObject.markSelection(DebugObjectSelection.SELECTED);
    this.prevDebugObject = debugObject;
    updateCTSButtonText();
  }

  private void updateStyleInfo(DebugSnapshot snapshot) {
    if (snapshot.computedStyles() == null) return;
    if (snapshot.styleRules() == null) return;
    PropertyContainer computedStyles = snapshot.computedStyles().get();
    List<WeightedStyleRule> styleRules = snapshot.styleRules().get();
    styleView.updateStyles(snapshot, computedStyles, styleRules);
  }

  private void updateCTSButtonText() {
    String newText = debugger.nodeSelection().clickToSelectEnabled() ?
      "Disable \"Click to Select\" Mode" :
      "Enable \"Click to Select\" Mode";
    for (JButton button: ctsButtons) {
      button.setText(newText);
    }
  }

}
