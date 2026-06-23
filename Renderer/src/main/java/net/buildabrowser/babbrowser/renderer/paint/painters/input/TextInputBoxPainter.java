package net.buildabrowser.babbrowser.renderer.paint.painters.input;

import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.html.html.HTMLDocument;
import net.buildabrowser.babbrowser.html.html.HTMLInputElement;
import net.buildabrowser.babbrowser.html.input.FocusManager;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.LoadedFont;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.input.InputContent;
import net.buildabrowser.babbrowser.renderer.content.input.text.TextTypeContent;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.input.TextInputFragment;
import net.buildabrowser.babbrowser.renderer.paint.BoxPainter;
import net.buildabrowser.babbrowser.renderer.paint.VpIntersection;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.ElementBackgroundPainter;

public class TextInputBoxPainter implements BoxPainter<TextInputFragment> {

  public static final float HORIZONTAL_PADDING = 8;

  private static final int CARET_OFFSET_Y = 3;

  @Override
  public void paint(
    TextInputFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    ElementBox box = fragment.box();
    HTMLInputElement element = (HTMLInputElement) box.element();
    FocusManager focusManager = ((HTMLDocument) element.nodeDocument()).focusManager();
    TextTypeContent content = ((InputContent) box.content()).innerContent();
    LoadedFont font = box.layoutContext().font();
    FontMetrics metrics = font.metrics();
    float posY = (fragment.height(Measurement.CONTENT) - metrics.height()) / 2;
    String beforeCursorText = element.value().substring(0, content.cursorX());
    float cursorOffset = metrics.stringWidth(beforeCursorText);
    float caretReplaceWidth = content.cursorX() == content.value().length() ?
      metrics.stringWidth(TextTypeContent.PLACEHOLDER_CHARACTER) :
      metrics.stringWidth(content.value().substring(content.cursorX(), content.cursorX() + 1));
    boolean showCaret = focusManager.focused() == element;
    canvas.withClip(
      0, 0,
      fragment.width(Measurement.CONTENT),
      fragment.height(Measurement.CONTENT),
      c -> canvas.withPaintAndTransform(
        p -> {
          p.setColor(PropertiesUtil.textColor(box.properties()));
          p.setFont(font);
        },
        t -> t.translate(-content.scrollX() + HORIZONTAL_PADDING, 0),
        c2 -> {
          c2.drawText(0, posY, element.value());
          // TODO: Make a drawLine?
          if (showCaret && content.isReplaceMode()) {
            c2.drawBox(cursorOffset, posY - metrics.ascent(), caretReplaceWidth, 1);
          } else if (showCaret) {
            c2.drawBox(cursorOffset, posY + CARET_OFFSET_Y, 1, -metrics.ascent());
          }
        }));
  }

  @Override
  public void paintBackground(
    TextInputFragment fragment,
    PaintCanvas canvas,
    VpIntersection vpIntersection
  ) {
    ElementBackgroundPainter.paintBackground(
      canvas, fragment, vpIntersection);
  }
  
}
