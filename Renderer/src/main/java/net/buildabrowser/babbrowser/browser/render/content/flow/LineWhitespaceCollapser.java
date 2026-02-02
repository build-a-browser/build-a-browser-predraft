package net.buildabrowser.babbrowser.browser.render.content.flow;

import java.util.Deque;
import java.util.LinkedList;

import net.buildabrowser.babbrowser.browser.render.content.flow.InlineStagingArea.ManagedBoxEntryMarker;
import net.buildabrowser.babbrowser.browser.render.content.flow.InlineStagingArea.ManagedBoxExitMarker;
import net.buildabrowser.babbrowser.browser.render.content.flow.InlineStagingArea.StagedText;
import net.buildabrowser.babbrowser.css.engine.property.CSSProperty;
import net.buildabrowser.babbrowser.css.engine.property.whitespace.WhitespaceCollapseValue;
import net.buildabrowser.babbrowser.css.engine.styles.ActiveStyles;

public final class LineWhitespaceCollapser {
  
  private LineWhitespaceCollapser() {}

  public static void collapseWhitespace(InlineStagingArea stagingArea, WhitespaceCollapseValue whitespaceCollapse) {
    // TODO: Avoid a stack allocation...
    Deque<WhitespaceCollapseValue> modeStack = new LinkedList<>();
    modeStack.push(whitespaceCollapse);

    boolean lastTextWhitespaceTrailed = false;
    stagingArea.resetCursor();
    StringBuilder newText = new StringBuilder();
    while (!stagingArea.done()) {
      int nextText = stagingArea.cursorPos();
      switch (stagingArea.next()) {
        case StagedText _ -> {
          lastTextWhitespaceTrailed = switch (modeStack.peek()) {
            case COLLAPSE, PRESERVE_BREAKS -> collapseWhitespaceInner(
              stagingArea, nextText, newText, whitespaceCollapse, lastTextWhitespaceTrailed);
            case PRESERVE_SPACES -> preserveSpaces(stagingArea, nextText, lastTextWhitespaceTrailed);
            default -> false;
          };
        }
        case ManagedBoxEntryMarker entryMarker -> {
          ActiveStyles styles = entryMarker.elementBox().activeStyles();
          WhitespaceCollapseValue collapse = (WhitespaceCollapseValue) styles.getProperty(CSSProperty.WHITE_SPACE_COLLAPSE);
          modeStack.push(collapse);
        }
        case ManagedBoxExitMarker _ -> modeStack.pop();
        default -> {}
      }
    }
  }

  private static boolean collapseWhitespaceInner(
    InlineStagingArea stagingArea, int nextText, StringBuilder newText,
    WhitespaceCollapseValue collapseValue, boolean lastTextWhitespaceTrailed
  ) {
      String originalText = stagingArea.textAt(nextText);
      newText.setLength(0);
      newText.append(originalText);
      collapseAroundSegment(newText);
      if (collapseValue.equals(WhitespaceCollapseValue.COLLAPSE)) {
        collapseSegmentBreaks(newText);
      }
      collapseTabs(newText);
      lastTextWhitespaceTrailed = collapseSpaceStrings(newText, lastTextWhitespaceTrailed);
      
      String finalText = newText.toString();
      stagingArea.setText(nextText, finalText);

      return lastTextWhitespaceTrailed;
  }

  private static boolean preserveSpaces(InlineStagingArea stagingArea, int nextText, boolean lastTextWhitespaceTrailed) {
    String originalText = stagingArea.textAt(nextText);
    String transformedText = originalText
      .replace('\t', ' ')
      .replace('\n', ' ');
    stagingArea.setText(nextText, transformedText);

    return transformedText.isEmpty() ?
      lastTextWhitespaceTrailed :
      transformedText.endsWith(" ");
  }

  private static void collapseAroundSegment(StringBuilder newText) {
    int activeSpaceIndex = -1;
    boolean sawSegmentBreak = false;
    for (int i = 0; i < newText.length(); i++) {
      int ch = newText.codePointAt(i);
      if (sawSegmentBreak && (ch == ' ' || ch == '\t')) {
        newText.deleteCharAt(i);
        i--;
      } else if (activeSpaceIndex == -1 && (ch == ' ' || ch == '\t')) {
        activeSpaceIndex = i;
      } else if (activeSpaceIndex != -1 && ch == '\n') {
        newText.delete(activeSpaceIndex, i);
        i = activeSpaceIndex;
        activeSpaceIndex = -1;
      } else if (ch != ' ' && ch != '\t') {
        activeSpaceIndex = -1;
      }

      sawSegmentBreak =
        ch == ' ' || ch == '\t' ? sawSegmentBreak :
        ch == '\n';
    }
  }

  private static void collapseSegmentBreaks(StringBuilder newText) {
    boolean wasSegmentBreak = false;
    for (int i = 0; i < newText.length(); i++) {
      boolean isSegmentBreak = newText.codePointAt(i) == '\n';
      if (isSegmentBreak && wasSegmentBreak) {
        newText.deleteCharAt(i);
        i--;
      } else if (isSegmentBreak) {
        newText.setCharAt(i, ' ');
      }
      wasSegmentBreak = isSegmentBreak;
    }
  }

  private static void collapseTabs(StringBuilder newText) {
    for (int i = 0; i < newText.length(); i++) {
      if (newText.codePointAt(i) == '\t') {
        newText.setCharAt(i, ' ');
      }
    }
  }

  private static boolean collapseSpaceStrings(StringBuilder newText, boolean wasSpace) {
    for (int i = 0; i < newText.length(); i++) {
      boolean isSpace = newText.codePointAt(i) == ' ';
      if (isSpace && wasSpace) {
        newText.setCharAt(i, '\u200B');
      }
      wasSpace = isSpace;
    }

    return wasSpace;
  }

}
