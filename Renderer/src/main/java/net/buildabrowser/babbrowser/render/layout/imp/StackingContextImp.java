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
import net.buildabrowser.babbrowser.render.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.render.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.render.composite.CompositeLayer;
import net.buildabrowser.babbrowser.render.composite.CompositeLayerEntry;
import net.buildabrowser.babbrowser.render.composite.imp.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.BoxFragment;
import net.buildabrowser.babbrowser.render.content.common.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.render.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.render.layout.StackingContext;

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
    entries = IntrusiveList.add(entries, new CompositeLayerEntry(posX, posY, fragment));
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
    CompositeLayer layer = createLayer(0, 0);
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
    // TODO: Need to normalize
    boolean useInsets = positioning.equals(PositionValue.RELATIVE);
    float myOffsetX = offsetX + (useInsets ? insets[2] : 0);
    float myOffsetY = offsetY + (useInsets ? insets[0] : 0);
    CompositeLayer ownLayer = createLayer(myOffsetX, myOffsetY);
    addFunc.accept(ownLayer);
    SinglyLinkedList<StackingContext> childContext = childContexts;
    while (childContext != null) {
      if (isPassthrough) {
        childContext.item().addLayer(addFunc, myOffsetX, myOffsetY);
      } else {
        childContext.item().addLayer(ownLayer::addChild, 0, 0);
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
  public float posX() {
    if (entries == null) return 0;
    float posX = Float.MAX_VALUE;
    CompositeLayerEntry currentEntry = entries;
    while (currentEntry != null) {
      ElementBoxDimensions dimensions = currentEntry.fragment().box().dimensions();
      float borderOffset = dimensions.getComputedBorder()[2];
      posX = Math.min(posX, currentEntry.offsetX() + borderOffset);
      currentEntry = currentEntry.next();
    }
    return posX;
  }

  @Override
  public float posY() {
    if (entries == null) return 0;
    float posY = Float.MAX_VALUE;
    CompositeLayerEntry currentEntry = entries;
    while (currentEntry != null) {
      ElementBoxDimensions dimensions = currentEntry.fragment().box().dimensions();
      float borderOffset = dimensions.getComputedBorder()[0];
      posY = Math.min(posY, currentEntry.offsetY() + borderOffset);
      currentEntry = currentEntry.next();
    }
    return posY;
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
      refFragment.contentWidth(), refFragment.contentHeight(), relatedBox);
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
