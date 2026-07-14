package net.buildabrowser.babbrowser.renderer.paint.painters.flow;

import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.util.PropertiesUtil;
import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.painter.core.PaintCanvas;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.context.SelectionContext;
import net.buildabrowser.babbrowser.renderer.fragment.BoxFragment;
import net.buildabrowser.babbrowser.renderer.fragment.LayoutFragment.Measurement;
import net.buildabrowser.babbrowser.renderer.fragment.TextFragment;

public final class FlowTextPainter {
  
  private FlowTextPainter() {}

  public static void paintTextFragment(
    PaintCanvas canvas,
    BoxFragment<?> refFragment,
    TextFragment textFragment
  ) {
    ElementBox box = refFragment.box();
    PropertyContainer properties = box.properties();
    SelectionContext selectionContext = box
      .layoutContext().global()
      .selectionContext();
    String allText = textFragment.text();
    
    boolean isSelected =
      textFragment.sourceNode() instanceof Text
      && selectionContext.selected(textFragment.sourceNode());
    if (isSelected) {
      int selectionStart = (int) selectionContext.selectionStart(textFragment.sourceNode());
      int selectionEnd = (int) selectionContext.selectionEnd(textFragment.sourceNode());
      int firstCharPos = Math.min(textFragment.textIndex(selectionStart), allText.length());
      int lastCharPos = Math.min(textFragment.textIndex(selectionEnd), allText.length());

      float textWidth = textFragment.width(Measurement.CONTENT);
      float textHeight = textFragment.height(Measurement.CONTENT);
      FontMetrics metrics = refFragment.box().layoutContext().font().metrics();
      String beforeText = allText.substring(0, firstCharPos);
      float selectionStartOffset = beforeText.length() == 0 ?
        0 : metrics.stringWidth(beforeText);
      String selectedText = allText.substring(firstCharPos, lastCharPos);
      float selectionLength = metrics.stringWidth(selectedText);
      float selectionEndOffset = selectionStartOffset + selectionLength;
      String endText = allText.substring(lastCharPos);

      if (beforeText.length() != 0) {
        canvas.withClip(
          0, 0,
          selectionStartOffset, textHeight,
          c -> c.drawText(0, 0, allText));
      }


      int selectionFgColor = PropertiesUtil.selectionFgColor(properties);
      int selectionBgColor = PropertiesUtil.selectionBgColor(properties);
      canvas.withPaint(
        // TODO: Move to PropertiesUtil
        p -> p.setColor(selectionBgColor),
        c -> c.drawBox(
          selectionStartOffset, 0,
          selectionLength, textHeight));
      canvas.withClip(
        selectionStartOffset, 0,
        selectionLength, textHeight,
        c -> c.withPaint(
          // TODO: Move to PropertiesUtil
          p -> p.setColor(selectionFgColor),
          c2 -> c2.drawText(0, 0, allText)));

      if (endText.length() != 0) {
        canvas.withClip(
          selectionEndOffset, 0,
          textWidth - selectionEndOffset, textHeight,
          c -> c.drawText(0, 0, allText));
      }
    } else {
      canvas.drawText(0, 0, allText);
    }
  }

}
