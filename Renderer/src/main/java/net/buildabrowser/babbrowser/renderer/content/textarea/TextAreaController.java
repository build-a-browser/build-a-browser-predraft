package net.buildabrowser.babbrowser.renderer.content.textarea;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import net.buildabrowser.babbrowser.cssbase.cssom.extra.InvalidationLevel;
import net.buildabrowser.babbrowser.html.html.HTMLTextAreaElement;
import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.box.ElementBox;
import net.buildabrowser.babbrowser.renderer.content.common.AbstractTextController;
import net.buildabrowser.babbrowser.renderer.content.scroll.ScrollBox;
import net.buildabrowser.babbrowser.renderer.fragment.scroll.ScrollBoxFragment;
import net.buildabrowser.babbrowser.renderer.paint.painters.common.TextEditPainter;

public class TextAreaController extends AbstractTextController {

  private final HTMLTextAreaElement element;
  private final ElementBox box;

  // Must be mutable
  private List<String> lines = new ArrayList<>();
  private BitSet continuations;

  private int overallIndex = 0;

  public TextAreaController(
    HTMLTextAreaElement element,
    ElementBox box
  ) {
    this.element = element;
    this.box = box;
    lines.add("");
  }

  public void updateLines(
    List<String> lines,
    BitSet continuations
  ) {
    this.lines = lines;
    this.continuations = continuations;
  }

  @Override
  public String lineValue(int lineNum) {
    return lines.get(lineNum);
  }

  @Override
  public void setLineValue(int lineNum, String value) {
    setCursorX(cursorX()); // Resets startNavWidth
    lines.set(lineNum, value);
    updateElementValue();
    box.context().invalidate(InvalidationLevel.LAYOUT);
  }

  @Override
  public List<String> displayLines() {
    return this.lines;
  }

  @Override
  public boolean isMultiLine() {
    return true;
  }

  @Override
  public boolean isLineContinuation(int lineNum) {
    return continuations.get(lineNum);
  }

  @Override
  public void setLineContinuation(int lineNum, boolean isContinuation) {
    continuations.set(lineNum, isContinuation);
    updateElementValue();
    box.context().invalidate(InvalidationLevel.LAYOUT);
  }

  @Override
  public void submit() {}

  @Override
  public void scrollToCursor(
    FontMetrics fontMetrics,
    float contentWidth,
    float contentHeight
  ) {
    if (!(
      box.parentBox() instanceof ScrollBox scrollBox
      && scrollBox.positioningFragment() instanceof ScrollBoxFragment scrollFragment
    )) return;

    float paddedContentWidth = contentWidth - TextEditPainter.HORIZONTAL_PADDING * 2;
    String priorString = lineValue().substring(0, cursorX());
    float targetXLeft = fontMetrics.stringWidth(priorString);
    float targetXRight = targetXLeft + 2;
    float scrollX = scrollFragment.scrollX();
    if (scrollX + paddedContentWidth < targetXRight) {
      float determinedValue =
        targetXRight - paddedContentWidth
        + TextEditPainter.HORIZONTAL_PADDING;
      scrollFragment.setScrollX(determinedValue);
    } else if (scrollX > targetXLeft) {
      scrollFragment.setScrollX(targetXLeft);
    }

    float paddedContentHeight = contentHeight
      - TextEditPainter.VERTICAL_PADDING_MULTILINE * 2;
    float targetYTop = cursorY() * fontMetrics.height();
    float targetYBottom = (cursorY() + 1) * fontMetrics.height();
    float scrollY = scrollFragment.scrollY();
    if (scrollY + paddedContentHeight < targetYBottom) {
      scrollFragment.setScrollY(
        targetYBottom - paddedContentHeight
        + TextEditPainter.VERTICAL_PADDING_MULTILINE);
    } else if (scrollY > targetYTop) {
      scrollFragment.setScrollY(targetYTop);
    }
  }

  public void backupCursorWrap() {
    int newOverallIndex = cursorX();
    for (int y = 0; y < cursorY(); y++) {
      newOverallIndex += lineValue(y).length();
      if (!isLineContinuation(y + 1)) {
        newOverallIndex++; // Include \n
      }
    }

    this.overallIndex = newOverallIndex;
  }

  public void restoreCursorWrap() {
    setCursorXRaw(0);
    setCursorY(0);
    int currentIndex = overallIndex;
    while (
      currentIndex > columns()
      && cursorY() < rows() - 1
    ) {
      currentIndex -= columns();
      setCursorY(cursorY() + 1);
      if (!isLineContinuation(cursorY())) {
        currentIndex -= 1; // Include \n
      }
    }
    setCursorXRaw(Math.min(columns(), currentIndex));
    skipSoftWrap();
  }

  private void updateElementValue() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      if (i != 0 && !continuations.get(i)) {
        builder.append("\n"); // TODO: Preserve source newline (e.g. \r\n)
      }
      builder.append(line);
    }
    element.setValue(builder.toString());
  }
  
}
