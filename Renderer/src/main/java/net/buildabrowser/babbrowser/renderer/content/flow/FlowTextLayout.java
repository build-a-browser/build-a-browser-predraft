package net.buildabrowser.babbrowser.renderer.content.flow;

import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.StagedText;
import net.buildabrowser.babbrowser.renderer.content.flow.mapping.MappingRLEBuffer;
import net.buildabrowser.babbrowser.renderer.layout.FontWordWidthCache;
import net.buildabrowser.babbrowser.renderer.layout.LayoutContext;

public final class FlowTextLayout {
  
  private FlowTextLayout() {}

  public static void layoutText(
    LayoutContext layoutContext, StagedText stagedText,
    InlineFormattingContext formattingContext, boolean autoWrap
  ) {
    // TODO: Properly handle whitespace at line start/end, and break-word
    Text textNode = stagedText.boxRef().textNode();
    MappingRLEBuffer mappingRLEBuffer = stagedText.sourceRunsRef() == null ?
      null : stagedText.sourceRunsRef().clone();
    formattingContext.lineBox().startText(
      textNode, mappingRLEBuffer);

    String allText = stagedText.currentText();
    int textCursor = 0;
    while (textCursor < allText.length()) {
      int ch = allText.codePointAt(textCursor);
      if (isForcedLineBreak(ch)) {
        formattingContext.nextLine();
        textCursor++;
        continue;
      }

      int startCursor = textCursor;
      while (
        textCursor < allText.length()
        && (textCursor == startCursor || (ch = allText.codePointAt(textCursor)) != ' ' && ch != '\u200B')
        && !isForcedLineBreak(ch)
      ) {
        textCursor++;
      }

      if (textCursor < allText.length()) {
        textCursor++;
      }

      String selectedText = allText.substring(startCursor, textCursor);
      addTextOrWrap(
        layoutContext,
        textNode, selectedText, startCursor,
        formattingContext, autoWrap);
    }
  }

  private static void addTextOrWrap(
    LayoutContext layoutContext,
    Text sourceText, String text, int sourceIndex,
    InlineFormattingContext formattingContext, boolean autoWrap
  ) {
    FontMetrics fontMetrics = layoutContext.font().metrics();
    FontWordWidthCache widthCache = layoutContext.global().fontWordWidthCache();
    float textWidth = widthCache.stringWidth(fontMetrics, text);
    float textHeight = fontMetrics.height(); // TODO: Need to check against fallbacks

    boolean textOverflows = !formattingContext.fits(textWidth, true);
    boolean shouldWrap = autoWrap && textOverflows;
    if (shouldWrap) {
      // TODO: If a float was involved, drop down to the next point the text would fit post-float
      formattingContext.nextLine();
    }

    formattingContext.lineBox().appendText(
      text, sourceIndex, textWidth, textHeight);
  }

  private static boolean isForcedLineBreak(int codepoint) {
    return switch (codepoint) {
      case '\f', '\r', '\n', '\u000B', '\u2028', '\u2029', '\u0085' -> true;
      default -> false;
    };
  }

}
