package net.buildabrowser.babbrowser.renderer.content.common;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.mathClamp;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.event.util.MouseEventUtil;

public abstract class AbstractTextController implements TextController {

  private int cursorX = 0;
  private float scrollX = 0;
  private int cursorY = 0;
  private float startNavWidth = Float.NaN;
  private boolean isReplaceMode = false;

  @Override
  public int cursorX() {
    int lineX = displayLines().get(cursorY()).length();
    return mathClamp(this.cursorX, 0, lineX);
  }

  @Override
  public void setCursorX(int cursorX) {
    this.cursorX = cursorX;
    this.startNavWidth = Float.NaN;
  }

  @Override
  public int cursorY() {
    return mathClamp(this.cursorY, 0, rows() - 1);
  }

  @Override
  public void setCursorY(int cursorY) {
    this.cursorY = cursorY;
  }

  @Override
  public float scrollX() {
    return this.scrollX;
  }

  @Override
  public void setScrollX(float scrollX) {
    this.scrollX = scrollX;
  }

  @Override
  public boolean isReplaceMode() {
    return this.isReplaceMode;
  }

  @Override
  public void setIsReplaceMode(boolean isReplaceMode) {
    this.isReplaceMode = isReplaceMode;
  }

  @Override
	public void insertOrReplaceText(String text) {
    if (isReplaceMode()) {
      replaceText(text);
    } else {
      insertText(text);
    }
  }

  @Override
	public void insertText(String text) {
    int cursorX = cursorX();
    // TODO: Should probably use a StringBuilder instead
    setLineValue(lineValue().substring(0, cursorX) + text + lineValue().substring(cursorX));
    setCursorX(cursorX + text.length());
  }

  @Override
	public void replaceText(String text) {
    int cursorX = cursorX();
    if (cursorX == columns()) {
      insertText(text);
      return;
    }
    setLineValue(lineValue().substring(0, cursorX) + text + lineValue().substring(cursorX + 1));
    setCursorX(cursorX + text.length());
  }

  @Override
	public void backspace() {
    int cursorX = cursorX();
    int cursorY = cursorY();
    if (cursorX == 0 && cursorY == 0) return;
    if (cursorX != 0) {
      setLineValue(lineValue().substring(0, cursorX - 1) + lineValue().substring(cursorX));
      setCursorX(cursorX - 1);
    } else if (!isLineContinuation(cursorY)) {
      setLineContinuation(cursorY, true);
      setCursorY(cursorY - 1);
      moveEnd();
    } else {
      setCursorY(cursorY - 1);
      moveEnd();
      backspace();
    }
  }

  @Override
	public void moveCursorForward(int i) {
    if (i < 0) {
      moveCursorBackward(-i);
      return;
    }

    while (i > 0) {
      int maxAdvance = columns() - cursorX();
      if (
        i > maxAdvance
        && cursorY() < rows() - 1
      ) {
        setCursorY(cursorY() + 1);
        setCursorX(0);
        i -= maxAdvance + 1;
      } else {
        setCursorX(cursorX() + i);
        i = 0;
      }
    }

    skipSoftWrap();
  }

  private void moveCursorBackward(int i) {
    if (i < 0) {
      moveCursorForward(i);
      return;
    }

    while (i > 0) {
      int maxAdvance = cursorX();
      if (
        i > maxAdvance
        && cursorY() > 0
      ) {
        setCursorY(cursorY() - 1);
        setCursorX(columns());
        i -= maxAdvance;
      } else {
        setCursorX(cursorX() - i);
        i = 0;
      }
    }
  }

  @Override
  public void moveCursorDownward(
    FontMetrics metrics, int i
  ) {
    verticalMove(metrics, () -> setCursorY(mathClamp(
      cursorY() + i, 0, rows() - 1)));
  }

  @Override
	public void moveHome() {
    setCursorX(0);
  }

  @Override
	public void moveEnd() {
    setCursorX(columns());
  }

  @Override
  public void moveTop() {
    setCursorY(0);
    setCursorX(0);
  }

  @Override
  public void moveBottom() {
    setCursorY(rows() - 1);
    setCursorX(columns());
  }

  @Override
  public void movePageUp(
    FontMetrics metrics,
    float fragmentHeight
  ) {
    verticalMove(metrics, () -> {
      setCursorY(cursorY() - (int) (fragmentHeight / metrics.height()));
    });
  }

  @Override
  public void movePageDown(
    FontMetrics metrics,
    float fragmentHeight
  ) {
    verticalMove(metrics, () -> {
      setCursorY(cursorY() + (int) (fragmentHeight / metrics.height()));
    });
  }

  @Override
	public void delete() {
    int cursorX = cursorX();
    if (
      cursorY() >= rows()
      && cursorX() >= columns()
    ) return;
    if (cursorX() < columns()) {
      setLineValue(lineValue().substring(0, cursorX) + lineValue().substring(cursorX + 1));
    } else {
      setCursorY(cursorY + 1);
      moveHome();
      if (isLineContinuation(cursorY())) {
        delete();
      } else {
        setLineContinuation(cursorY(), true);
      }
    }
  }

  @Override
	public void toggleInsertMode() {
    setIsReplaceMode(!isReplaceMode());
  }

  protected void setCursorXRaw(int cursorX) {
    this.cursorX = cursorX;
  }

  protected void skipSoftWrap() {
    if (
      cursorX() == columns()
      && cursorY() < rows() - 1
      && isLineContinuation(cursorY() + 1)
    ) {
      setCursorY(cursorY() + 1);
      setCursorX(0);
    }
  }

  protected int rows() {
    return displayLines().size();
  }

  protected int columns() {
    return lineValue().length();
  }

  private void verticalMove(
    FontMetrics metrics, Runnable action
  ) {
    if (Float.isNaN(startNavWidth)) {
      this.startNavWidth = metrics.stringWidth(
        lineValue().substring(0, cursorX()));
    }
    action.run();
    // setCursorX interferes with startNavWidth
    this.cursorX = MouseEventUtil.determineTextMouseIndex(
      startNavWidth, metrics, lineValue());
  }
  
}
