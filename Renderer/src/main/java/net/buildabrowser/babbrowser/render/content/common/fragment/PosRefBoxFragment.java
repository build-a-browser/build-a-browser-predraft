package net.buildabrowser.babbrowser.render.content.common.fragment;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.paint.UnreachableBoxPainter;

// This exists to keep the fragment in-tree for proper ordering when scanning for positioned
// fragments, but also provide a target that can be ignored during standard layout operations
// TODO: Unfortunately, this often needs special-cased in layout code, so find a cleaner alternative
public class PosRefBoxFragment extends BoxFragment {

  public PosRefBoxFragment(ElementBox refBox) {
    super(0, 0, 0, 0, refBox, new UnreachableBoxPainter());
  }

  public PositionValue position() {
    return (PositionValue) box().activeStyles().getProperty(CSSProperty.POSITION);
  }

  @Override
  public String toString() {
    return "[PosRefBoxFragment size=[" + contentWidth() + "x" + contentHeight() + "]]";
  }

}
