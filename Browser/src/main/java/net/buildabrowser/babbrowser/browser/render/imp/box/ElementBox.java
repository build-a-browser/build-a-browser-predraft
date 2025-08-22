package net.buildabrowser.babbrowser.browser.render.imp.box;

import java.awt.Component;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.buildabrowser.babbrowser.browser.render.core.box.Box;
import net.buildabrowser.babbrowser.spec.dom.Element;

public class ElementBox implements Box {

  @SuppressWarnings("unused")
  private final Element element;
  private final List<Box> children;

  public ElementBox(Element element, List<Box> children) {
    this.element = element;
    this.children = children;
  }

  @Override
  public JComponent render() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    for (Box child: children) {
      JComponent childComponent = child.render();
      childComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
      panel.add(childComponent);
    }

    return panel;
  }

}
