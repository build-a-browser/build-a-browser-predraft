package net.buildabrowser.babbrowser.renderer.paint.painters.common;

import net.buildabrowser.babbrowser.common.datastruct.SlotItem;
import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.CSSValue;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.PropertyValueParserUtil.ManyResult;
import net.buildabrowser.babbrowser.cssbase.property.color.ColorValue.SRGBAColor;
import net.buildabrowser.babbrowser.dom.Node;
import net.buildabrowser.babbrowser.html.html.HTMLElement;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.box.ElementBoxIterator;
import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollBox;
import net.buildabrowser.babbrowser.renderer.context.ElementContext;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;

public final class ElementBackgroundPainter {

  private static final boolean DEBUG_OUTLINES;

  static {
    DEBUG_OUTLINES = Boolean.getBoolean("babbrowser.debug");
  }
  
  private ElementBackgroundPainter() {}

  public static void paintBackground(
    PaintCanvas canvas,
    BoxFragment<?> fragment,
    VpIntersection vpIntersection
  ) {
    paintBackgroundImages(
      canvas, fragment, vpIntersection,
      fragment.width(Measurement.BORDER),
      fragment.height(Measurement.BORDER));

    ElementBorderPainter.paintBorders(canvas, fragment);
    paintDebugOutlines(canvas, fragment);
  }

  public static void paintDebugOutlines(PaintCanvas canvas, BoxFragment<?> fragment) {
    if (!DEBUG_OUTLINES) return;
    canvas.withPaint(
      p -> p.setColor(0xFFFF00FF),
      c -> {
        c.drawBox(0, 0, fragment.width(Measurement.BORDER), 1);
        c.drawBox(0, fragment.height(Measurement.BORDER) - 1, fragment.width(Measurement.BORDER), 1);
        c.drawBox(0, 0, 1, fragment.height(Measurement.BORDER));
        c.drawBox(fragment.width(Measurement.BORDER) - 1, 0, 1, fragment.height(Measurement.BORDER));
      });
  }

  public static void paintBackgroundImages(
    PaintCanvas canvas,
    BoxFragment<?> fragment,
    VpIntersection vpIntersection,
    float fragmentWidth,
    float fragmentHeight
  ) {
    PropertyContainer properties = fragment.box().properties();
    // TODO: Having this edge case check here is not great
    if (
      fragment.box().element() != null
      && fragment.box().element().name().equals("body") // TODO: instanceof BodyElement
      && inheritsBodyBackground(fragment.box().element().parentNode(), fragment.box())
    ) {
      return;
    }

    if (
      inheritsBodyBackground(fragment.box().element(), fragment.box())
    ) {
      properties = scanBodyProperties(fragment.box());
    }

    ElementBackgroundImagePainter.paintBackgroundImagesAdjusted(
      canvas, fragment, vpIntersection,
      properties, fragmentWidth, fragmentHeight);
  }

  private static boolean inheritsBodyBackground(
    Node node, ElementBox refBox
  ) {
    if (!(
      node instanceof HTMLElement htmlElement // TODO: instanceof HTMLHtmlElement
      && htmlElement.name().equals("html")
    )) return false;

    ElementContext context = SlotItem.getExistingById(htmlElement, refBox.context().familyId());
    PropertyContainer properties = context.properties();
    return
      properties.get(CSSProperty.BACKGROUND_COLOR).equals(SRGBAColor.create(0, 0, 0, 0))
      && properties.get(CSSProperty.BACKGROUND_IMAGE).equals(ManyResult.create(CSSValue.NONE));
  }

  private static PropertyContainer scanBodyProperties(ElementBox box) {
    // TODO: Would it be better to get boxes via DOM mappings to ignore any wrapper boxes?
    if (box instanceof ScrollBox) {
      ElementBoxIterator childIt = box.childBoxes();
      if (
        childIt.hasNext()
        && childIt.next() instanceof ElementBox elBox
      ) {
        box = elBox;
      } else {
        return box.properties();
      }
    }

    ElementBoxIterator childIt = box.childBoxes();
    while (childIt.hasNext()) {
      if (
        childIt.next() instanceof ElementBox childElBox
        && childElBox.element() != null
        && childElBox.element().name().equals("body") // TODO: instanceof
      ) {
        return childElBox.properties();
      }
    }

    return box.properties();
  }

}
