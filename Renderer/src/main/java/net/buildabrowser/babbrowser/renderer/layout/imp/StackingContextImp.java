package net.buildabrowser.babbrowser.renderer.layout.imp;

import java.util.function.Consumer;

import net.buildabrowser.babbrowser.common.datastruct.IntrusiveList;
import net.buildabrowser.babbrowser.common.datastruct.SinglyLinkedList;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.position.PositionValue;
import net.buildabrowser.babbrowser.cssbase.property.position.ZIndexValue;
import net.buildabrowser.babbrowser.painter.core.Painter;
import net.buildabrowser.babbrowser.renderer.box.Box;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBox.BoxLevel;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayer;
import net.buildabrowser.babbrowser.renderer.composite.CompositeLayerEntry;
import net.buildabrowser.babbrowser.renderer.content.common.position.PositionUtil;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.ScrollPort;
import net.buildabrowser.babbrowser.renderer.layout.StackingContext;
import net.buildabrowser.babbrowser.renderer.layout.StackingContextPosition;
import net.buildabrowser.babbrowser.renderer.layout.Viewport;
import net.buildabrowser.babbrowser.renderer.layout.StackingContextPosition.ScrollGetter;

// TODO: Some of the positioning code here is quite hacky
public class StackingContextImp implements StackingContext {

  private final StackingContext parentContext;
  private final ElementBox relatedBox;
  private final int zIndexOrder;
  private final PositionValue positioning;
  private final boolean isPassthrough;

  private float[] insets;
  private float[] absolutePosition;
  private SinglyLinkedList<StackingContext> childContexts;
  private SinglyLinkedList<StackingContext> lastContext;
  private CompositeLayerEntry entries;

  private float normalizedX = 0;
  private float normalizedY = 0;

  public StackingContextImp(StackingContext parentContext, ElementBox relatedBox) {
    PropertyContainer properties = relatedBox.properties();
    CSSValue zIndex = properties.get(CSSProperty.Z_INDEX);
    this.zIndexOrder = zIndex.equals(CSSValue.AUTO) ? 0 : ((ZIndexValue) zIndex).zIndex();
    this.positioning = (PositionValue) properties.get(CSSProperty.POSITION);
    this.isPassthrough = parentContext != null && zIndex.equals(CSSValue.AUTO);
    this.parentContext = parentContext;
    this.relatedBox = relatedBox;
  }

  @Override
  public void positionFragment(
    float posX, float posY,
    BoxFragment<?> fragment,
    ChildPositionFunc positionFunc
  ) {
    if (this.entries != null && fragment instanceof ScrollBoxFragment) {
      throw new RuntimeException();
    }

    // Relative/Static layers are initially added with positions preserved since the fragment might be split
    // and each fragment has a different pos (and it is difficult to normalize ahead of time)
    // TODO: This is really quite hacky
    if (
      this.entries == null &&
      (
        positioning.equals(PositionValue.RELATIVE)
        || positioning.equals(PositionValue.STICKY)
        || positioning.equals(PositionValue.STATIC))
    ) {
      normalizedX = posX;
      normalizedY = posY;
    }

    float layerX = posX - normalizedX;
    float layerY = posY - normalizedY;
    fragment.setLayerPos(layerX, layerY);
    entries = IntrusiveList.add(entries, new CompositeLayerEntry(
      layerX, layerY, fragment));
    positionFunc.position(layerX, layerY);
  }

  @Override
  public void positionNormalizedFragment(
    float posX, float posY,
    BoxFragment<?> fragment,
    ChildPositionFunc positionFunc
  ) {
    if (Float.isNaN(normalizedX)) {
      normalizedX = 0;
    }
    if (Float.isNaN(normalizedY)) {
      normalizedX = 0;
    }
    positionFragment(
      normalizedX + posX, normalizedY + posY,
      fragment, positionFunc);
  }
  
  @Override
  public StackingContext createChild(ElementBox relatedBox) {
    StackingContext childContext = new StackingContextImp(this, relatedBox);
    addChild(childContext);
    return childContext;
  }

  @Override
  public float[] computeInsets() {
    return this.insets =
      positioning.equals(PositionValue.RELATIVE) ? determineRelativeInsets() :
      positioning.equals(PositionValue.STICKY) ? determineStickyInsets() :
      positioning.equals(PositionValue.ABSOLUTE) ? determineAbsoluteInsets() :
      positioning.equals(PositionValue.FIXED) ? determineAbsoluteInsets() :
      new float[4];
  }

  @Override
  public void setAbsolutePosition(float[] position) {
    this.absolutePosition = position;
  }

  @Override
  public float[] computedBorder() {
    return relatedBox.dimensions().getComputedBorder();
  }

  @Override
  public CompositeLayer createLayer(Painter painter) {
    assert insets != null;
    StackingContextPosition ownPosition = StackingContextPosition.root();
    Viewport viewport = relatedBox.layoutContext().global().viewport();
    ScrollPort scrollPort = new ScrollPort(
      ownPosition, viewport.width(), viewport.height());
    CompositeLayer layer = createLayer(painter, ownPosition);
    SinglyLinkedList<StackingContext> childContext = childContexts;
    while (childContext != null) {
      StackingContextPosition childPosition = positionChild(
        ownPosition, childContext.item());
      childContext.item().addLayer(
        layer::addChild, painter, childPosition, scrollPort);
      childContext = childContext.next();
    }

    return layer;
  }

  @Override
  public void addLayer(
    Consumer<CompositeLayer> addFunc,
    Painter painter,
    StackingContextPosition parentPosition,
    ScrollPort scrollPort
  ) {
    assert insets != null;

    StackingContextPosition ownPosition = positionSelf(
      parentPosition, scrollPort);
    ScrollPort childScrollPort = determineChildScrollPort(
      ownPosition, scrollPort);
    
    CompositeLayer ownLayer = createLayer(painter, ownPosition);
    addFunc.accept(ownLayer);
    SinglyLinkedList<StackingContext> childContext = childContexts;
    while (childContext != null) {
      StackingContextPosition childPosition = positionChild(
        ownPosition, childContext.item());
      if (isPassthrough) {
        childContext.item().addLayer(addFunc, painter, childPosition, childScrollPort);
      } else {
        childContext.item().addLayer(ownLayer::addChild, painter, childPosition, childScrollPort);
      }
      childContext = childContext.next();
    }
  }

  private CompositeLayer createLayer(
    Painter painter,
    StackingContextPosition position
  ) {
    CompositeLayer layer = CompositeLayer.create(
      painter, position, zIndexOrder);
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
      BoxFragment<?> fragment = currentEntry.fragment();
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
      BoxFragment<?> fragment = currentEntry.fragment();
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
    BoxFragment<?> refFragment = elementBox.positioningFragment();
    return PositionUtil.computeRelativeInsets(
      refFragment.width(Measurement.CONTENT), refFragment.height(Measurement.CONTENT), relatedBox);
  }

  private float[] determineStickyInsets() {
    if (parentContext == null) return new float[4];
    float refWidth = parentContext.innerWidth();
    float refHeight = parentContext.innerHeight();
    return PositionUtil.computeStickyInsets(
      relatedBox, refWidth, refHeight);
  }

  private float[] determineAbsoluteInsets() {
    if (parentContext == null) return new float[4];
    float refWidth = parentContext.innerWidth();
    float refHeight = parentContext.innerHeight();
    return PositionUtil.computeAbsoluteInsets(
      relatedBox, refWidth, refHeight);
  }

  private StackingContextPosition positionSelf(
    StackingContextPosition parentPosition,
    ScrollPort scrollPort
  ) {
    return switch (positioning) {
      case STATIC -> parentPosition.relative(0, 0, normalizedX, normalizedY);
      case RELATIVE -> parentPosition.relative(insets[2], insets[0], normalizedX, normalizedY);
      case STICKY -> parentPosition.sticky(
        normalizedX, normalizedY,
        innerWidth(), innerHeight(),
        insets, scrollPort);
      case ABSOLUTE -> parentPosition.absolute(absolutePosition[0], absolutePosition[1]);
      case FIXED -> parentPosition.fixed(
        absolutePosition[0], absolutePosition[1],
        PositionUtil.isStaticX(relatedBox),
        PositionUtil.isStaticY(relatedBox));
      // TODO: Sticky
      default -> parentPosition.relative(0, 0, normalizedX, normalizedY);
    };
  }

  private StackingContextPosition positionChild(
    StackingContextPosition parentPosition,
    StackingContext childContext
  ) {
    float[] border = relatedBox.dimensions().getComputedBorder();
    StackingContextPosition childPosition = switch (childContext.positioning()) {
      case ABSOLUTE -> parentPosition.absolute(border[2], border[0]);
      default -> parentPosition;
    };

    ScrollBoxFragment scrollBoxFragment = relatedScrollBox();
    if (scrollBoxFragment == null) return childPosition;
    ScrollGetter scrollGetterX = () -> scrollBoxFragment.scrollX();
    ScrollGetter scrollGetterY = () -> scrollBoxFragment.scrollY();
    return childPosition.scroll(scrollGetterX, scrollGetterY);
  }

  private ScrollPort determineChildScrollPort(
    StackingContextPosition ownPosition,
    ScrollPort scrollPort
  ) {
    ScrollBoxFragment scrollBoxFragment = relatedScrollBox();
    if (scrollBoxFragment == null) return scrollPort;
    return new ScrollPort(
      ownPosition, innerWidth(), innerHeight());
  }

  private ScrollBoxFragment relatedScrollBox() {
    return entries != null
      && entries.next() == null
      && entries.fragment() instanceof ScrollBoxFragment scrollBoxFragment_
      ? scrollBoxFragment_ : null;
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
