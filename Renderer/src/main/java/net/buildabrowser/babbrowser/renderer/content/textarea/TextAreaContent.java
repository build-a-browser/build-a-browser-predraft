package net.buildabrowser.babbrowser.renderer.content.textarea;

import net.buildabrowser.babbrowser.common.util.NumberUtil;
import net.buildabrowser.babbrowser.html.html.HTMLTextAreaElement;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.content.common.TextController;
import net.buildabrowser.babbrowser.renderer.content.input.text.TextTypeContent;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.FocusEventHandler;
import net.buildabrowser.babbrowser.renderer.event.handlers.textarea.TextAreaFocusEventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;

public class TextAreaContent implements BoxContent {

  public static final String PLACEHOLDER_CHARACTER = "a";

  private static TextAreaFocusEventHandler TEXT_AREA_FOCUS_EVENT_HANDLER = new TextAreaFocusEventHandler();

  private final TextController textController;

  public TextAreaContent(
    HTMLTextAreaElement element
  ) {
    this.textController = new TextAreaController(element);
  }

  public TextController textController() {
    return this.textController;
  }

  // TODO: Invalidate layout on col/rows changed
  @Override
  public void computeIntrinsics(ElementBox rootBox) {
    Integer cols = NumberUtil.parseNonNegativeInteger(
      rootBox.element().getAttribute("cols"));
    if (cols == null) cols = 20;
    Integer rows = NumberUtil.parseNonNegativeInteger(
      rootBox.element().getAttribute("rows"));
    if (rows == null) rows = 2;

    FontMetrics fontMetrics = rootBox.layoutContext().font().metrics();
    float intrinsicWidth = TextTypeContent.convertACharacterWidthToPixels(fontMetrics, cols);
    float intrinsicHeight = fontMetrics.height() * rows;
    rootBox.alterDimensions(false, dimensions -> {
      dimensions.setIntrinsicWidth(intrinsicWidth);
      dimensions.setInstrinsicHeight(intrinsicHeight);
    });
  }

  @Override
  public UnmanagedBoxFragment<?> layout(
    ElementBox rootBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    ElementBoxDimensions dimensions = rootBox.dimensions();
    float usedWidth = LayoutUtil.constraintOrDim(widthConstraint, dimensions.intrinsicWidth());
    float usedHeight = LayoutUtil.constraintOrDim(heightConstraint, dimensions.intrinsicHeight());
    float inkWidth = Math.max(usedWidth, dimensions.intrinsicWidth());
    float inkHeight = Math.max(usedWidth, dimensions.intrinsicHeight());
    FontMetrics fontMetrics = rootBox.layoutContext().font().metrics();
    float lastBaseline = fontMetrics.descent();
    FragmentFactory fragmentFactory = rootBox.layoutContext().global().fragmentFactory();
    UnmanagedBoxFragment<?> fragment = fragmentFactory.createTextAreaBoxFragment(
      usedWidth, usedHeight, inkWidth, inkHeight,
      0, lastBaseline, // TODO: Compute first baseline
      rootBox);
    rootBox.updatePositioningFragment(fragment);
    return fragment;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends BoxContent> EventHandlerResponse withFocusEventHandler(
    ElementBox box,
    FocusEventHandlerFunc<T> withHandlerFunc
  ) {
    return withHandlerFunc.apply(
      (FocusEventHandler<T>) TEXT_AREA_FOCUS_EVENT_HANDLER,
      (T) this);
  }

  @Override
  public void positionLayers(UnmanagedBoxFragment<?> fragment, float layerX, float layerY) {
    fragment.setLayerPos(layerX, layerY);
  }

  @Override
  public boolean hasCustomContent(ElementBox box) {
    return true;
  }
  
}
