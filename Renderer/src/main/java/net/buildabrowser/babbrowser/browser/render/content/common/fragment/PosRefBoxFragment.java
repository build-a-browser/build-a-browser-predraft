package net.buildabrowser.babbrowser.browser.render.content.common.fragment;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.position.PositionValue;

// This exists to keep the fragment in-tree for proper ordering when scanning for positioned
// fragments, but also provide a target that can be ignored during standard layout operations
// TODO: Unfortunately, this often needs special-cased in layout code, so find a cleaner alternative
public class PosRefBoxFragment extends BoxFragment {

  private final LayoutContext refLayoutContext;

  public PosRefBoxFragment(int width, int height, ElementBox box, LayoutContext refLayoutContext) {
    super(width, height, box);
    this.refLayoutContext = refLayoutContext;
  }

  public PositionValue position() {
    return (PositionValue) box().activeStyles().getProperty(CSSProperty.POSITION);
  }

  public LayoutContext referenceLayoutContext() {
    return this.refLayoutContext;
  }

  @Override
  public String toString() {
    return "[PosRefBoxFragment size=[" + contentWidth() + "x" + contentHeight() + "]]";
  }

}
