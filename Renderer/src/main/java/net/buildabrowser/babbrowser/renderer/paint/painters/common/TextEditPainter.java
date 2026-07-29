package net.buildabrowser.babbrowser.renderer.paint.painters.common;

import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.dom.Element;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.input.FocusManager;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.LoadedFont;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.TextController;
import net.buildabrowser.babbrowser.renderer.content.input.text.TextTypeContent;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;

public final class TextEditPainter {

  public static final float HORIZONTAL_PADDING = 8;

  private static final int CARET_OFFSET_Y = 3;
  
  private TextEditPainter() {}

  public static void paint(
    PaintCanvas canvas,
    TextController controller,
    BoxFragment<?> fragment
  ) {
    ElementBox box = fragment.box();
    LoadedFont font = box.layoutContext().font();
    FontMetrics metrics = font.metrics();
    Element element = box.element();
    FocusManager focusManager = ((HTMLDocument) element.nodeDocument()).focusManager();
    String displayValue = controller.displayValue();
    float posY = (fragment.height(Measurement.CONTENT) - metrics.height()) / 2;
    int codepointPos = displayValue.offsetByCodePoints(0, controller.cursorX());
    String beforeCursorText = displayValue.substring(0, codepointPos);
    float cursorOffset = metrics.stringWidth(beforeCursorText);
    float caretReplaceWidth = controller.cursorX() == displayValue.length() ?
      metrics.stringWidth(TextTypeContent.PLACEHOLDER_CHARACTER) :
      metrics.stringWidth(displayValue.substring(codepointPos,
        displayValue.offsetByCodePoints(codepointPos, 1)));
    boolean showCaret = focusManager.focused() == element;
    
    canvas.withClip(
      0, 0,
      fragment.width(Measurement.CONTENT),
      fragment.height(Measurement.CONTENT),
      c -> c.withPaintAndTransform(
        p -> {
          p.setColor(PropertiesUtil.textColor(box.properties()));
          p.setFont(font);
        },
        t -> t.translate(-controller.scrollX() + HORIZONTAL_PADDING, 0),
        c2 -> {
          c2.drawText(0, posY, displayValue);
          // TODO: Make a drawLine?
          if (showCaret && controller.isReplaceMode()) {
            c2.drawBox(cursorOffset, posY - metrics.ascent(), caretReplaceWidth, 1);
          } else if (showCaret) {
            c2.drawBox(cursorOffset, posY + CARET_OFFSET_Y, 1, -metrics.ascent());
          }
        }));
  }

}
