package net.buildabrowser.babbrowser.browser.render.content.flow;

import net.buildabrowser.babbrowser.browser.render.content.flow.InlineStagingArea.StagedText;
import net.buildabrowser.babbrowser.browser.render.layout.LayoutContext;
import net.buildabrowser.babbrowser.browser.render.paint.FontMetrics;

public final class FlowTextLayout {
  
  private FlowTextLayout() {}

  public static void layoutText(
    LayoutContext layoutContext, StagedText stagedText,
    InlineFormattingContext formattingContext, boolean autoWrap
  ) {
    // TODO: Properly handle whitespace at line start/end, and break-word
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

      String selectedText = allText.substring(startCursor, textCursor);
      addTextOrWrap(layoutContext, selectedText, formattingContext, autoWrap);
    }
  }

  private static void addTextOrWrap(
    LayoutContext layoutContext, String selectedText,
    InlineFormattingContext formattingContext, boolean autoWrap
  ) {
    FontMetrics fontMetrics = layoutContext.fontMetrics();
    float textWidth = fontMetrics.stringWidth(selectedText);
    float textHeight = fontMetrics.fontHeight();

    boolean textOverflows = !formattingContext.fits(textWidth, true);
    boolean shouldWrap = autoWrap && textOverflows;
    if (shouldWrap) {
      // TODO: If a float was involved, drop down to the next point the text would fit post-float
      formattingContext.nextLine();
    }

    formattingContext.lineBox().appendText(selectedText, textWidth, textHeight);
  }

  private static boolean isForcedLineBreak(int codepoint) {
    return switch (codepoint) {
      case '\f', '\r', '\n', '\u000B', '\u2028', '\u2029', '\u0085' -> true;
      default -> false;
    };
  }

}
