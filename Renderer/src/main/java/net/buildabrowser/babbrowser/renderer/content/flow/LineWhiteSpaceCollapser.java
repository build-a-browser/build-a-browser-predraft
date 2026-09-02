package net.buildabrowser.babbrowser.renderer.content.flow;

import java.util.Deque;
import java.util.LinkedList;

import net.buildabrowser.babbrowser.cssbase.property.CSSProperty;
import net.buildabrowser.babbrowser.cssbase.property.PropertyContainer;
import net.buildabrowser.babbrowser.cssbase.property.whitespace.WhiteSpaceCollapseValue;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.ManagedBoxEntryMarker;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.ManagedBoxExitMarker;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.StagedText;
import net.buildabrowser.babbrowser.renderer.content.flow.InlineStagingArea.StagedUnmanagedBox;
import net.buildabrowser.babbrowser.renderer.content.flow.mapping.MappedStringBuilder;

public final class LineWhiteSpaceCollapser {
  
  private LineWhiteSpaceCollapser() {}

  public static void collapseWhiteSpace(InlineStagingArea stagingArea, WhiteSpaceCollapseValue whitespaceCollapse) {
    // TODO: Avoid a stack allocation...
    Deque<WhiteSpaceCollapseValue> modeStack = new LinkedList<>();
    modeStack.push(whitespaceCollapse);

    boolean lastTextWhiteSpaceTrailed = false;
    stagingArea.resetCursor();
    MappedStringBuilder newText = new MappedStringBuilder();
    while (!stagingArea.done()) {
      int nextText = stagingArea.cursorPos();
      switch (stagingArea.next()) {
        case StagedText _1 -> {
          lastTextWhiteSpaceTrailed = switch (modeStack.peek()) {
            case COLLAPSE, PRESERVE_BREAKS -> collapseWhiteSpaceInner(
              stagingArea, nextText, newText, whitespaceCollapse, lastTextWhiteSpaceTrailed);
            case PRESERVE_SPACES -> preserveSpaces(stagingArea, nextText, lastTextWhiteSpaceTrailed);
            default -> false;
          };
        }
        case ManagedBoxEntryMarker entryMarker -> {
          PropertyContainer properties = entryMarker.elementBox().properties();
          WhiteSpaceCollapseValue collapse = (WhiteSpaceCollapseValue) properties.get(CSSProperty.WHITE_SPACE_COLLAPSE);
          modeStack.push(collapse);
        }
        case ManagedBoxExitMarker _1 -> modeStack.pop();
        case StagedUnmanagedBox _1 -> lastTextWhiteSpaceTrailed = false;
        default -> {}
      }
    }
  }

  private static boolean collapseWhiteSpaceInner(
    InlineStagingArea stagingArea, int nextText, MappedStringBuilder newText,
    WhiteSpaceCollapseValue collapseValue, boolean lastTextWhiteSpaceTrailed
  ) {
      String originalText = stagingArea.textAt(nextText);
      newText.restart(originalText);
      collapseAroundSegment(newText);
      if (collapseValue.equals(WhiteSpaceCollapseValue.COLLAPSE)) {
        collapseSegmentBreaks(newText);
      }
      collapseTabs(newText);
      lastTextWhiteSpaceTrailed = collapseSpaceStrings(newText, lastTextWhiteSpaceTrailed);
      
      String finalText = newText.toString();
      stagingArea.setText(
        nextText, finalText,
        newText.rleBuffer().clone());

      return lastTextWhiteSpaceTrailed;
  }

  private static boolean preserveSpaces(InlineStagingArea stagingArea, int nextText, boolean lastTextWhiteSpaceTrailed) {
    String originalText = stagingArea.textAt(nextText);
    String transformedText = originalText
      .replace('\t', ' ')
      .replace('\n', ' ');
    stagingArea.setTextPreserveMappings(nextText, transformedText);

    return transformedText.isEmpty() ?
      lastTextWhiteSpaceTrailed :
      transformedText.endsWith(" ");
  }

  private static void collapseAroundSegment(MappedStringBuilder newText) {
    int activeSpaceIndex = -1;
    boolean sawSegmentBreak = false;
    for (int i = 0; i < newText.length(); i++) {
      int ch = newText.codePointAt(i);
      if (sawSegmentBreak && (isSpace(ch) || ch == '\t')) {
        newText.deleteCharAt(i);
        i--;
      } else if (activeSpaceIndex == -1 && (isSpace(ch) || ch == '\t')) {
        activeSpaceIndex = i;
      } else if (activeSpaceIndex != -1 && ch == '\n') {
        newText.delete(activeSpaceIndex, i);
        i = activeSpaceIndex;
        activeSpaceIndex = -1;
      } else if (!isSpace(ch) && ch != '\t') {
        activeSpaceIndex = -1;
      }

      sawSegmentBreak =
        isSpace(ch) || ch == '\t' ? sawSegmentBreak :
        ch == '\n';
    }
  }

  private static void collapseSegmentBreaks(MappedStringBuilder newText) {
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

  private static void collapseTabs(MappedStringBuilder newText) {
    for (int i = 0; i < newText.length(); i++) {
      if (newText.codePointAt(i) == '\t') {
        newText.setCharAt(i, ' ');
      }
    }
  }

  private static boolean collapseSpaceStrings(MappedStringBuilder newText, boolean wasSpace) {
    for (
      int i = 0;
      i < newText.length();
      i = newText.raw().offsetByCodePoints(i, 1)
    ) {
      boolean isSpace = isSpace(newText.codePointAt(i));
      if (isSpace && wasSpace) {
        newText.setCharAt(i, '\u200B');
      }
      wasSpace = isSpace;
    }

    return wasSpace;
  }

  private static boolean isSpace(int ch) {
    // Carriage returns are treated the same as spaces
    return ch == ' ' || ch == '\r';
  }

}
