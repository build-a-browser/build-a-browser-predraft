package net.buildabrowser.babbrowser.renderer.box.imp;

import java.util.Comparator;
import java.util.function.Consumer;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.renderer.box.MutableElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayerUtil;
import net.buildabrowser.babbrowser.renderer.content.flexbox.FlexBoxContent;
import net.buildabrowser.babbrowser.renderer.content.flow.FlowRootContent;
import net.buildabrowser.babbrowser.renderer.content.flow.FlowUtil;
import net.buildabrowser.babbrowser.renderer.content.table.TableContent;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.CachedLayoutResult;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;
import net.buildabrowser.babbrowser.renderer.layout.stacking.StackingContext;

public abstract class AbstractElementBoxImp extends AbstractBoxImp implements ElementBox {

  private static MutableElementBoxDimensions DEFAULT_DIMENSIONS = ElementBoxDimensions.create();

  private MutableElementBoxDimensions dimensions;

  private Box parentBox;
  private BoxLevel boxLevel;

  private CachedLayoutResult cache;
  private BoxFragment<?> positioningFragment;

  private LayoutContext layoutContext;
  private StackingContext stackingContext;

  Box childBoxes; // Package-level for the iterator
  Box nextBox;

  public AbstractElementBoxImp(
    Box parentBox, BoxLevel boxLevel
  ) {
    this.parentBox = parentBox;
    this.boxLevel = boxLevel;
  }

  @Override
  public Box parentBox() {
    return this.parentBox;
  }

  @Override
  public ElementBoxDimensions dimensions() {
    if (this.dimensions == null) {
      return DEFAULT_DIMENSIONS;
    }
    return this.dimensions;
  }

  @Override
  public void alterDimensions(
    boolean skipIfNone, Consumer<MutableElementBoxDimensions> alterFunc
  ) {
    if (dimensions == null && skipIfNone) return;
    if (dimensions == null) {
      this.dimensions = ElementBoxDimensions.create();
    }
    alterFunc.accept(dimensions);
  }

  @Override
  public ElementBoxIterator childBoxes() {
    // Hopefully the iterator is done being used by the next addChild.
    // Otherwise, undefined behaviour may occur.
    return new ElementBoxIteratorImp(this);
  }

  @Override
  public void addChild(Box box) {
    if (nextBox == null) {
      nextBox = IntrusiveList.last(childBoxes);
    }

    Box newBox = IntrusiveList.add(nextBox, box);
    if (childBoxes == null) {
      childBoxes = newBox;
    }

    nextBox = newBox;

    assert IntrusiveList._ensureNoLoops(childBoxes);
  }

  @Override
  public void clearChildren() {
    this.childBoxes = null;
    this.nextBox = null;
  }

  @Override
  public void sortChildren(Comparator<? super Box> comparator) {
    this.childBoxes = IntrusiveList.sort(childBoxes, comparator);
  }

  @Override
  public BoxLevel boxLevel() {
    return this.boxLevel;
  }

  @Override
  public void updateDetails(Box parentBox, BoxLevel boxLevel) {
    this.parentBox = parentBox;
    this.boxLevel = boxLevel;

    setLayoutContext(null);
  }

  @Override
  public UnmanagedBoxFragment<?> layout(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    return layoutWithContent(widthConstraint, heightConstraint, content());
  }

  protected UnmanagedBoxFragment<?> layoutWithContent(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint,
    BoxContent content
  ) {
    CachedLayoutResult current = cache;
    while (current != null) {
      if (current.applies(widthConstraint, heightConstraint)) {
        UnmanagedBoxFragment<?> fragment = current.fragment();
        fragment.setNext(null);
        return fragment;
      }
      current = current.next();
    }

    if (cache == null) {
      content.computeIntrinsics(this);
    }

    UnmanagedBoxFragment<?> layoutResult = content.layout(
      this, widthConstraint, heightConstraint);
    
    this.cache = CachedLayoutResult.create(
      widthConstraint, heightConstraint, layoutResult, cache);
    return layoutResult;
  }

  // TODO: This is rather unreliable, find an alternative way
  @Override
  public void updatePositioningFragment(BoxFragment<?> boxFragment) {
    this.positioningFragment = boxFragment;
  }

  @Override
  public BoxFragment<?> positioningFragment() {
    return this.positioningFragment;
  }

  @Override
  public LayoutContext layoutContext() {
    return this.layoutContext;
  }

  @Override
  public void setLayoutContext(LayoutContext layoutContext) {
    this.layoutContext = layoutContext;
    this.cache = null;
    this.positioningFragment = null;
    this.stackingContext = null;
    // Hopefully this is properly called before other methods that rely on cache
  }

  @Override
  public StackingContext stackingContext() {
    return this.stackingContext;
  }

  @Override
  public void setStackingContext(StackingContext stackingContext) {
    this.stackingContext = stackingContext;
  }

  @Override
  public boolean sharesContent(ElementBox elementBox) {
    boolean canShareFlow =
      content() instanceof FlowRootContent
      && !FlowUtil.isFloat(elementBox)
      && FlowUtil.isInFlowNoContent(elementBox)
      && !CompositeLayerUtil.hasScrollContent(this); // If a flow box is nested in a scrollbox

    return canShareFlow;
  }

  protected BoxContent createSpecifiedContent(InnerDisplayValue innerDisplay) {
    return switch (innerDisplay) {
      case TABLE -> TableContent.get();
      case FLEX -> FlexBoxContent.get();
      default -> FlowRootContent.get();
    };
  }

}
