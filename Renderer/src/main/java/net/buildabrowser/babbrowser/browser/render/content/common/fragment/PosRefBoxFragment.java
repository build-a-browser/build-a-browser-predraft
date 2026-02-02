package net.buildabrowser.babbrowser.browser.render.content.common.fragment;

import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.paint.BoxPainter;
import net.buildabrowser.babbrowser.browser.render.paint.PaintCanvas;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.position.PositionValue;

// This exists to keep the fragment in-tree for proper ordering when scanning for positioned
// fragments, but also provide a target that can be ignored during standard layout operations
// TODO: Unfortunately, this often needs special-cased in layout code, so find a cleaner alternative
public class PosRefBoxFragment extends BoxFragment {

  private static final BoxPainter REF_PAINTER = new PosRefBoxFragmentPainter();

  private final BoxFragment refFragment;
  private final ElementBox refBox;
  private final LayoutContext refLayoutContext;

  public PosRefBoxFragment(
    ElementBox refBox,
    LayoutContext refLayoutContext
  ) {
    super(0, 0, refBox, REF_PAINTER);
    this.refLayoutContext = refLayoutContext;
    this.refBox = refBox;
    this.refFragment = null;
  }

  public PosRefBoxFragment(
    BoxFragment refFragment,
    LayoutContext refLayoutContext
  ) {
    super(refFragment.contentWidth(), refFragment.contentHeight(), refFragment.box(), REF_PAINTER);
    this.refLayoutContext = refLayoutContext;
    this.refBox = refFragment.box();
    this.refFragment = refFragment;
  }

  public PositionValue position() {
    return (PositionValue) box().activeStyles().getProperty(CSSProperty.POSITION);
  }

  public LayoutContext referenceLayoutContext() {
    return this.refLayoutContext;
  }

  public BoxFragment referenceFragment() {
    return this.refFragment;
  }

  public ElementBox referenceBox() {
    return this.refBox;
  }

  @Override
  public String toString() {
    return "[PosRefBoxFragment size=[" + contentWidth() + "x" + contentHeight() + "]]";
  }

  private static class PosRefBoxFragmentPainter implements BoxPainter {

    @Override
    public void paint(BoxFragment fragment, PaintCanvas canvas) {
      BoxFragment refFragment = ((PosRefBoxFragment) fragment).referenceFragment();
      refFragment.painter().paint(refFragment, canvas);
    }

    @Override
    public void paintBackground(BoxFragment fragment, PaintCanvas canvas) {
      BoxFragment refFragment = ((PosRefBoxFragment) fragment).referenceFragment();
      refFragment.painter().paintBackground(refFragment, canvas);
    }

  }

}
