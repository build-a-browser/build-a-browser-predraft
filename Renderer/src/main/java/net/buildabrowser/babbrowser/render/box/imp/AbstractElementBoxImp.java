package net.buildabrowser.babbrowser.render.box.imp;

import java.util.Comparator;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.css.engine.styles.util.ActiveStylesUtil;
import net.buildabrowser.babbrowser.cssbase.property.display.DisplayValue.InnerDisplayValue;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.render.box.Box;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.render.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.render.composite.CompositeLayerUtil;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.content.flow.FlowRootContent;
import net.buildabrowser.babbrowser.render.content.flow.FlowUtil;
import net.buildabrowser.babbrowser.render.layout.CachedLayoutResult;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.render.layout.StackingContext;

public abstract class AbstractElementBoxImp extends AbstractBoxImp implements ElementBox {

  private final ElementBoxDimensions dimensions;
  private final Box parentBox;
  private final BoxLevel boxLevel;

  private CachedLayoutResult cache;
  private BoxFragment positioningFragment;

  private LayoutContext layoutContext;
  private StackingContext stackingContext;

  Box childBoxes; // Package-level for the iterator
  Box nextBox;

  public AbstractElementBoxImp(Box parentBox, BoxLevel boxLevel) {
    this.dimensions = ElementBoxDimensions.create(this);
    this.parentBox = parentBox;
    this.boxLevel = boxLevel;
  }

  @Override
  public HTMLElement element() {
    throw new UnsupportedOperationException("Anonymous box is not associated with an element!");
  }

  @Override
  public Box parentBox() {
    return this.parentBox;
  }

  @Override
  public ElementBoxDimensions dimensions() {
    return this.dimensions;
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
  public UnmanagedBoxFragment layout(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    CachedLayoutResult current = cache;
    while (current != null) {
      if (current.applies(widthConstraint, heightConstraint)) {
        UnmanagedBoxFragment fragment = current.fragment();
        fragment.setNext(null);
        return fragment;
      }
      current = current.next();
    }

    if (cache == null) {
      content().computeIntrinsics();
    }

    UnmanagedBoxFragment layoutResult = content().layout(widthConstraint, heightConstraint);
    
    this.cache = CachedLayoutResult.create(
      widthConstraint, heightConstraint, layoutResult, cache);
    return layoutResult;
  }

  // TODO: This is rather unreliable, find an alternative way
  @Override
  public void updatePositioningFragment(BoxFragment boxFragment) {
    this.positioningFragment = boxFragment;
  }

  @Override
  public BoxFragment positioningFragment() {
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
    // TODO: Can't use FlowUtil.isInFlow here because isReplaced() requires content
    InnerDisplayValue otherInnerDisplay = ActiveStylesUtil.innerDisplayValue(elementBox.activeStyles());
    boolean canShareFlow =
      content() instanceof FlowRootContent
      && otherInnerDisplay.equals(InnerDisplayValue.FLOW)
      && !FlowUtil.isFloat(elementBox)
      && !CompositeLayerUtil.hasScrollContent(this); // If a flow box is nested in a scrollbox

    return canShareFlow;
  }

}
