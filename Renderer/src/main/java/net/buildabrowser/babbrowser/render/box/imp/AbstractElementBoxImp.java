package net.buildabrowser.babbrowser.render.box.imp;

import java.util.Comparator;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.render.box.Box;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.render.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.render.layout.CachedLayoutResult;
import net.buildabrowser.babbrowser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.render.layout.StackingContext;

public abstract class AbstractElementBoxImp extends AbstractBoxImp implements ElementBox {

  private final ElementBoxDimensions dimensions;
  private final Box parentBox;
  private final BoxLevel boxLevel;

  private CachedLayoutResult cache;
  private BoxFragment cachedFragment;

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
    if (
      cache != null
      && cachedFragment instanceof UnmanagedBoxFragment umCachedFragment
      && cache.applies(widthConstraint, heightConstraint)
    ) {
      umCachedFragment.setNext(null);
      return umCachedFragment;
    }
    if (cache == null) {
      content().computeIntrinsics();
    }

    // TODO: Is it worth scanning for and removing the old cache result?
    UnmanagedBoxFragment layoutResult = content().layout(widthConstraint, heightConstraint);
    this.cache = CachedLayoutResult.create(
      widthConstraint, heightConstraint, layoutResult.contentWidth(), layoutResult.contentHeight(), cache);
    this.cachedFragment = layoutResult;
    return layoutResult;
  }

  @Override
  public CachedLayoutResult cachedLayout(
    LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    CachedLayoutResult current = cache;
    while (current != null) {
      if (current.applies(widthConstraint, heightConstraint)) {
        return current;
      }
      current = current.next();
    }

    layout(widthConstraint, heightConstraint);
    return cache;
  }

  @Override
  public void updateFragment(BoxFragment boxFragment) {
    this.cachedFragment = boxFragment;
  }

  @Override
  public BoxFragment lastCachedFragment() {
    return this.cachedFragment;
  }

  @Override
  public LayoutContext layoutContext() {
    return this.layoutContext;
  }

  @Override
  public void setLayoutContext(LayoutContext layoutContext) {
    this.layoutContext = layoutContext;
    this.cache = null;
    this.cachedFragment = null;
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

}
