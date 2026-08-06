package net.buildabrowser.babbrowser.renderer.paint.painters.common;

import static net.buildabrowser.babbrowser.html.util.HTMLDomUtil.isHtmlElement;

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
import net.buildabrowser.babbrowser.renderer.context.RenderContext;
import net.buildabrowser.babbrowser.renderer.context.imp.FakeRootContextImp;
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
    float fragmentWidth = Math.max(0, fragment.width(Measurement.BORDER));
    float fragmentHeight = Math.max(0, fragment.height(Measurement.BORDER));

    // <html> is painted by the fake root
    // TODO: A bit hacky to do the check here
    if (
      !isHtmlElement(fragment.box().element(), "html")
      || (fragment.box().context() instanceof FakeRootContextImp)
    ) {
      paintBackgroundImages(
        canvas, fragment, vpIntersection,
        fragmentWidth, fragmentHeight);
    }

    ElementBorderPainter.paintBorders(
      canvas, fragment,
      fragmentWidth, fragmentHeight);
    ElementOutlinePainter.paintOutlines(
      canvas, fragment, fragmentWidth, fragmentHeight);
    paintDebugOutlines(canvas, fragmentWidth, fragmentHeight);
  }

  public static void paintDebugOutlines(
    PaintCanvas canvas, float fragmentWidth, float fragmentHeight
  ) {
    if (!DEBUG_OUTLINES) return;
    canvas.withPaint(
      p -> p.setColor(0xFFFF00FF),
      c -> {
        c.drawBox(0, 0, fragmentWidth, 1);
        c.drawBox(0, fragmentHeight - 1, fragmentWidth, 1);
        c.drawBox(0, 0, 1, fragmentHeight);
        c.drawBox(fragmentWidth - 1, 0, 1, fragmentHeight);
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

    ElementBackgroundImagePainter.paintBackgroundImagesAdjusted(
      canvas, fragment, vpIntersection,
      properties, fragmentWidth, fragmentHeight);
  }

  public static boolean inheritsBodyBackground(
    Node node, ElementBox refBox
  ) {
    if (!(
      node instanceof HTMLElement htmlElement // TODO: instanceof HTMLHtmlElement
      && htmlElement.name().equals("html")
    )) return false;

    RenderContext context = SlotItem.getExistingById(htmlElement, refBox.context().familyId());
    PropertyContainer properties = context.properties();
    return
      properties.get(CSSProperty.BACKGROUND_COLOR).equals(SRGBAColor.create(0, 0, 0, 0))
      && properties.get(CSSProperty.BACKGROUND_IMAGE).equals(ManyResult.create(CSSValue.NONE));
  }

}
