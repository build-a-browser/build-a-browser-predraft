package net.buildabrowser.babbrowser.render.layout.imp;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.common.datastruct.SinglyLinkedList;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.cssbase.property.position.ZIndexValue;
import net.buildabrowser.babbrowser.render.box.Box;
import net.buildabrowser.babbrowser.render.box.ElementBox;
import net.buildabrowser.babbrowser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.render.composite.CompositeLayerEntry;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.render.content.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.render.layout.StackingContext;

// TODO: Some of the positioning code here is quite hacky
public class StackingContextImp implements StackingContext {

  private final StackingContext parentContext;
  private final ElementBox relatedBox;
  private final int zIndexOrder;
  private final PositionValue positioning;
  private final boolean isPassthrough;

  private float[] insets;
  private SinglyLinkedList<StackingContext> childContexts;
  private SinglyLinkedList<StackingContext> lastContext;
  private CompositeLayerEntry entries;

  private float normalizedX = 0;
  private float normalizedY = 0;

  public StackingContextImp(StackingContext parentContext, ElementBox relatedBox) {
    ActiveStyles activeStyles = relatedBox.activeStyles();
    CSSValue zIndex = activeStyles.getProperty(CSSProperty.Z_INDEX);
    this.zIndexOrder = zIndex.equals(CSSValue.AUTO) ? 0 : ((ZIndexValue) zIndex).zIndex();
    this.positioning = (PositionValue) activeStyles.getProperty(CSSProperty.POSITION);
    this.isPassthrough = parentContext != null && zIndex.equals(CSSValue.AUTO);
    this.parentContext = parentContext;
    this.relatedBox = relatedBox;
  }

  @Override
  public void addFragment(float posX, float posY, BoxFragment fragment) {
    if (this.entries != null && fragment instanceof ScrollBoxFragment) {
      throw new RuntimeException();
    }

    // Relative/Static layers are initially added with positions preserved since the fragment might be split
    // and each fragment has a different pos (and it is difficult to normalize ahead of time)
    if (
      this.entries == null &&
      (
        positioning.equals(PositionValue.RELATIVE)
        || positioning.equals(PositionValue.STATIC))
    ) {
      normalizedX = posX;
      normalizedY = posY;
    }
        
    entries = IntrusiveList.add(entries, new CompositeLayerEntry(
      posX - normalizedX, posY - normalizedY, fragment));
  }
  
  @Override
  public StackingContext createChild(ElementBox relatedBox) {
    StackingContext childContext = new StackingContextImp(this, relatedBox);
    addChild(childContext);
    return childContext;
  }

  @Override
  public float[] computeInsets() {
    CSSValue position = relatedBox.activeStyles().getProperty(CSSProperty.POSITION);
    return this.insets =
      position.equals(PositionValue.RELATIVE) ? determineRelativeInsets() :
      position.equals(PositionValue.ABSOLUTE) ? determineAbsoluteInsets() :
      new float[4];
  }

  @Override
  public CompositeLayer createLayer() {
    assert insets != null;
    CompositeLayer layer = createLayer(normalizedX, normalizedY);
    SinglyLinkedList<StackingContext> childContext = childContexts;
    while (childContext != null) {
      childContext.item().addLayer(layer::addChild, 0, 0);
      childContext = childContext.next();
    }

    return layer;
  }

  @Override
  public void addLayer(Consumer<CompositeLayer> addFunc, float offsetX, float offsetY) {
    assert insets != null;

    boolean useInsets = positioning.equals(PositionValue.RELATIVE);
    float myOffsetX = offsetX + (useInsets ? insets[2] : 0) + normalizedX;
    float myOffsetY = offsetY + (useInsets ? insets[0] : 0) + normalizedY;
    
    float[] border = relatedBox.dimensions().getComputedBorder();
    
    CompositeLayer ownLayer = createLayer(myOffsetX, myOffsetY);
    addFunc.accept(ownLayer);
    SinglyLinkedList<StackingContext> childContext = childContexts;
    while (childContext != null) {
      // Absolutely positioned layers need to be inside the border box
      boolean useAbsOffset = childContext.item().positioning().equals(PositionValue.ABSOLUTE);
      float absOffsetX = useAbsOffset ? border[2] : 0;
      float absOffsetY = useAbsOffset ? border[0] : 0;
      if (isPassthrough) {
        childContext.item().addLayer(addFunc, myOffsetX + absOffsetX, myOffsetY + absOffsetY);
      } else {
        childContext.item().addLayer(ownLayer::addChild, absOffsetX, absOffsetY);
      }
      childContext = childContext.next();
    }
  }

  private CompositeLayer createLayer(float offsetX, float offsetY) {
    CompositeLayer layer = CompositeLayer.create(
      positioning, offsetX, offsetY, zIndexOrder);
    layer.addEntries(entries);
    return layer;
  }

  @Override
  public StackingContext parentContext() {
    return this.parentContext;
  }

  @Override
  public PositionValue positioning() {
    return this.positioning;
  }

  @Override
  public float innerWidth() {
    float minX = Integer.MAX_VALUE;
    float maxX = Integer.MIN_VALUE;
    CompositeLayerEntry currentEntry = entries;
    while (currentEntry != null) {
      BoxFragment fragment = currentEntry.fragment();
      float adjustedWidth = fragment.width(Measurement.PADDING);
      minX = Math.min(minX, currentEntry.offsetX());
      maxX = Math.max(maxX, currentEntry.offsetX() + adjustedWidth);
      currentEntry = currentEntry.next();
    }

    return Math.max(0, maxX - minX);
  }

  @Override
  public float innerHeight() {
    float minY = Integer.MAX_VALUE;
    float maxY = Integer.MIN_VALUE;
    CompositeLayerEntry currentEntry = entries;
    while (currentEntry != null) {
      BoxFragment fragment = currentEntry.fragment();
      float adjustedHeight = fragment.height(Measurement.PADDING);
      minY = Math.min(minY, currentEntry.offsetY());
      maxY = Math.max(maxY, currentEntry.offsetY() + adjustedHeight);
      currentEntry = currentEntry.next();
    }

    return Math.max(0, maxY - minY);
  }

  private float[] determineRelativeInsets() {
    Box refBox = relatedBox.parentBox();
    while (
      refBox instanceof ElementBox elementBox
      && !elementBox.boxLevel().equals(BoxLevel.BLOCK_LEVEL)
    ) {
      refBox = elementBox.parentBox();
    }
    if (!(refBox instanceof ElementBox elementBox)) return new float[4];

    if (elementBox.positioningFragment() == null) {
      // TODO: Remove this check once every content properly handles positions
      return new float[4];
    }
    BoxFragment refFragment = elementBox.positioningFragment();
    return PositionUtil.computeRelativeInsets(
      refFragment.width(Measurement.CONTENT), refFragment.height(Measurement.CONTENT), relatedBox);
  }

  private float[] determineAbsoluteInsets() {
    if (parentContext == null) return new float[4];
    float refWidth = parentContext.innerWidth();
    float refHeight = parentContext.innerHeight();
    return PositionUtil.computeAbsoluteInsets(
      relatedBox, refWidth, refHeight);
  }

  private void addChild(StackingContext childContext) {
    SinglyLinkedList<StackingContext> newContext = new SinglyLinkedList<>(childContext);
    if (lastContext == null) {
      childContexts = IntrusiveList.add(childContexts, newContext);
    } else {
      IntrusiveList.add(lastContext, newContext);
    }
    lastContext = newContext;
  }
  
}
