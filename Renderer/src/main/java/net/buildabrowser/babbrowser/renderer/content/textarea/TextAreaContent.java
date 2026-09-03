package net.buildabrowser.babbrowser.renderer.content.textarea;

import java.util.List;

import net.buildabrowser.babbrowser.common.util.NumberUtil;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.overflow.OverflowValue;
import net.buildabrowser.babbrowser.cssbase.property.text.TextWrapModeValue;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.html.html.HTMLTextAreaElement;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.box.BoxContent;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxDimensions;
import net.buildabrowser.babbrowser.renderer.content.common.TextController;
import net.buildabrowser.babbrowser.renderer.content.common.TextWrapper;
import net.buildabrowser.babbrowser.renderer.content.input.text.TextTypeContent;
import net.buildabrowser.babbrowser.renderer.event.ContentEventHandler;
import net.buildabrowser.babbrowser.renderer.event.EventHandlerResponse;
import net.buildabrowser.babbrowser.renderer.event.handlers.textarea.TextAreaContentEventHandler;
import net.buildabrowser.babbrowser.renderer.fragment.FragmentFactory;
import net.buildabrowser.babbrowser.renderer.fragment.UnmanagedBoxFragment;
import net.buildabrowser.babbrowser.renderer.layout.LayoutConstraint;
import net.buildabrowser.babbrowser.renderer.layout.LayoutUtil;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.TextEditPainter;
import net.buildabrowser.babbrowser.renderer.paint.painters.scroll.ScrollBoxPainter;

public class TextAreaContent implements BoxContent {

  private static TextAreaContentEventHandler TEXT_AREA_FOCUS_EVENT_HANDLER = new TextAreaContentEventHandler();

  private final HTMLTextAreaElement element;
  private final TextAreaController textController;

  public TextAreaContent(
    HTMLTextAreaElement element,
    ElementBox box
  ) {
    this.element = element;
    this.textController = new TextAreaController(element, box);
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

    CSSValue overflowY = rootBox.properties().get(CSSProperty.OVERFLOW_Y);
    // Overflow-Y adds to X space, not Y
    boolean skipScrollbarPaddingX =
      overflowY.equals(OverflowValue.HIDDEN)
      || overflowY.equals(OverflowValue.CLIP);
    float paddingX = skipScrollbarPaddingX ? 0 : ScrollBoxPainter.GUTTER_WIDTH;

    FontMetrics fontMetrics = rootBox.layoutContext().font().metrics();
    float intrinsicWidth = paddingX + TextTypeContent.convertACharacterWidthToPixels(fontMetrics, cols);
    float intrinsicHeight = fontMetrics.height() * rows; // Don't include padding for the horizontal scrollbar
    rootBox.alterDimensions(false, dimensions -> {
      dimensions.setIntrinsicWidth(intrinsicWidth);
      dimensions.setIntrinsicHeight(intrinsicHeight);
    });
  }

  @Override
  public UnmanagedBoxFragment<?> layout(
    ElementBox rootBox,
    LayoutConstraint widthConstraint,
    LayoutConstraint heightConstraint
  ) {
    ElementBoxDimensions dimensions = rootBox.dimensions();
    float usedWidth = LayoutUtil.clampedUsedWidth(
      rootBox, widthConstraint, dimensions.intrinsicWidth());
    float usedHeight = LayoutUtil.clampedUsedHeight(
      rootBox, heightConstraint, dimensions.intrinsicHeight());
    float wrapWidth = Math.max(0, usedWidth - TextEditPainter.HORIZONTAL_PADDING * 2);
    FontMetrics fontMetrics = rootBox.layoutContext().font().metrics();
    float lastBaseline = fontMetrics.descent();

    boolean wrap = rootBox.properties()
      .get(CSSProperty.TEXT_WRAP_MODE)
      .equals(TextWrapModeValue.WRAP);
    TextAreaWrapTarget wrapTarget = new TextAreaWrapTarget(wrapWidth);
    // TODO: This does not wrap words larger than the line, add a mode to do so
    TextWrapper.layoutText(
      rootBox.layoutContext(),
      wrapTarget,
      Text.create(element.value()),
      element.value(),
      wrap);
    wrapTarget.finish();
    List<String> lines = wrapTarget.lines();
    textController.updateLines(
      lines, wrapTarget.continuations());
    textController.updateMetrics(fontMetrics);
    textController.scrollToCursor(usedWidth, usedHeight);

    float inkWidth = Math.max(usedWidth,
      wrapTarget.maxWidth() + TextEditPainter.HORIZONTAL_PADDING * 2);
    float inkHeight = Math.max(usedHeight,
      fontMetrics.height() * lines.size()
      + TextEditPainter.VERTICAL_PADDING_MULTILINE * 2);

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
  public <T extends BoxContent> EventHandlerResponse withContentEventHandler(
    ElementBox box,
    ContentEventHandlerFunc<T> withHandlerFunc
  ) {
    return withHandlerFunc.apply(
      (ContentEventHandler<T>) TEXT_AREA_FOCUS_EVENT_HANDLER,
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
