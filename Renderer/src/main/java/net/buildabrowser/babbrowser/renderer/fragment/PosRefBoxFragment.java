package net.buildabrowser.babbrowser.renderer.fragment;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.event.EventHandler;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.UnreachableBoxPainter;

// This exists to keep the fragment in-tree for proper ordering when scanning for positioned
// fragments, but also provide a target that can be ignored during standard layout operations
// TODO: Unfortunately, this often needs special-cased in layout code, so find a cleaner alternative
public class PosRefBoxFragment extends BoxFragment<PosRefBoxFragment> {

  private final BoxPainter<PosRefBoxFragment> unreachablePainter;

  public PosRefBoxFragment(ElementBox refBox) {
    super(0, 0, 0, 0, refBox);
    this.unreachablePainter = UnreachableBoxPainter.create(refBox.element());
  }

  public PositionValue position() {
    return (PositionValue) box().properties().get(CSSProperty.POSITION);
  }

  @Override
  public String toString() {
    return "[PosRefBoxFragment size=[" + width(Measurement.CONTENT) + "x" + height(Measurement.CONTENT) + "]]";
  }

  @Override
  public BoxPainter<PosRefBoxFragment> painter() {
    return unreachablePainter;
  }

  @Override
  protected EventHandler<PosRefBoxFragment> eventHandler() {
    // TODO Return an unreachable event handler
    throw new IllegalStateException("Unreachable eventHandler call reached");
  }

}
