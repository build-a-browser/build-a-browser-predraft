package net.buildabrowser.babbrowser.browser.render.box.imp;

import java.awt.Component;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.dom.Document;

public class DocumentBox implements Box {

  @SuppressWarnings("unused")
  private final Document document;
  private final List<Box> children;

  public DocumentBox(Document document, List<Box> children) {
    this.document = document;
    this.children = children;
  }

  @Override
  public JComponent render(ActiveStyles parentStyles) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    for (Box child: children) {
      JComponent childComponent = child.render(parentStyles);
      childComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
      panel.add(childComponent);
    }

    return panel;
  }

}
