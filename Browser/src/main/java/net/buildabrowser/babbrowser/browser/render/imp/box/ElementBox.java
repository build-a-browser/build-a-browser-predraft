package net.buildabrowser.babbrowser.browser.render.imp.box;

import java.awt.Component;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.buildabrowser.babbrowser.browser.render.core.box.Box;
import net.buildabrowser.babbrowser.browser.render.core.context.ElementContext;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.dom.mutable.MutableElement;

public class ElementBox implements Box {

  private final Element element;
  private final List<Box> children;

  public ElementBox(Element element, List<Box> children) {
    this.element = element;
    this.children = children;
  }

  @Override
  public JComponent render(ActiveStyles parentStyles) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    // TODO: Should getContext() be moved to Node?
    ActiveStyles activeStyles = ((ElementContext) ((MutableElement) element).getContext()).activeStyles();
    for (Box child: children) {
      JComponent childComponent = child.render(activeStyles);
      childComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
      panel.add(childComponent);
    }

    return panel;
  }

}
