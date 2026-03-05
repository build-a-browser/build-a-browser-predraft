package net.buildabrowser.babbrowser.browser.render.box.imp;

import net.buildabrowser.babbrowser.browser.render.box.Box;
import net.buildabrowser.babbrowser.browser.render.box.ElementBox;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.browser.render.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.browser.render.content.common.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.browser.render.layout.CachedLayoutResult;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.dom.Element;

public abstract class AbstractElementBoxImp extends AbstractBoxImp implements ElementBox {

  private final ElementBoxDimensions dimensions;
  private final Box parentBox;
  private final BoxLevel boxLevel;

  private Object cacheKey;
  private CachedLayoutResult cache;
  private UnmanagedBoxFragment cachedFragment;

  Box childBoxes; // Package-level for the iterator
  Box nextBox;

  public AbstractElementBoxImp(Box parentBox, BoxLevel boxLevel) {
    this.dimensions = ElementBoxDimensions.create(this);
    this.parentBox = parentBox;
    this.boxLevel = boxLevel;
  }

  @Override
  public Element element() {
    throw new UnsupportedOperationException("Anonymous box is not associated with an element!");
  }

  @Override
  public void invalidate(InvalidationLevel invalidationLevel) {
    parentBox.invalidate(invalidationLevel);
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
  public BoxLevel boxLevel() {
    return this.boxLevel;
  }

  @Override
  public UnmanagedBoxFragment layout(
    LayoutContext layoutContext, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    verifyCache(layoutContext);
    if (cache != null && cache.applies(widthConstraint, heightConstraint)) {
      return cachedFragment;
    }
    if (cache == null) {
      content().computeIntrinsics(layoutContext);
    }

    // TODO: Is it worth scanning for and removing the old cache result?
    UnmanagedBoxFragment layoutResult = content().layout(layoutContext, widthConstraint, heightConstraint);
    this.cache = CachedLayoutResult.create(
      widthConstraint, heightConstraint, layoutResult.contentWidth(), layoutResult.contentHeight(), cache);
    this.cachedFragment = layoutResult;
    return layoutResult;
  }

  @Override
  public CachedLayoutResult cachedLayout(
    LayoutContext layoutContext, LayoutConstraint widthConstraint, LayoutConstraint heightConstraint
  ) {
    verifyCache(layoutContext);
    CachedLayoutResult current = cache;
    while (current != null) {
      if (current.applies(widthConstraint, heightConstraint)) {
        return current;
      }
      current = current.next();
    }

    layout(layoutContext, widthConstraint, heightConstraint);
    return cache;
  }

  private void verifyCache(LayoutContext layoutContext) {
    if (layoutContext.global().cacheKey() != cacheKey) {
      this.cacheKey = layoutContext.global().cacheKey();
      this.cache = null;
      this.cachedFragment = null;
    }
  }
  
}
