package net.buildabrowser.babbrowser.renderer.input.imp;

import static net.buildabrowser.babbrowser.common.util.CompatUtil.mathClamp;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;
import net.buildabrowser.babbrowser.renderer.event.util.MouseEventUtil;
import net.buildabrowser.babbrowser.renderer.input.TextController;

public abstract class AbstractTextController implements TextController {

  private final StringBuilder value = new StringBuilder();

  private int cursorFlat = 0;private float scrollX = 0;
  private float startNavWidth = Float.NaN;
  private boolean isReplaceMode = false;
  private FontMetrics fontMetrics;

  @Override 
  public String value() {
    return value.toString();
  }

  @Override 
  public int cursorFlat() {
    return this.cursorFlat;
  }

  @Override
  public void setCursorFlat(int cursorFlat) {
    this.cursorFlat = cursorFlat;
    this.startNavWidth = Float.NaN;
  }
  
  @Override
  public int cursorX() {
    if (rows() == 0) return 0;
    int y = cursorY();
    int offset = lineStartOffset(y);
    int lineLen = lineValue(y).length();
    return mathClamp(this.cursorFlat - offset, 0, lineLen);
  }

  @Override
  public void setCursorX(int cursorX) {
    if (rows() == 0) {
      this.cursorFlat = 0;
      return;
    }

    int y = cursorY();
    int offset = lineStartOffset(y);
    int lineLen = lineValue(y).length();
    int maxLineX = (y < rows() - 1 && isLineContinuation(y + 1))
        ? Math.max(0, lineLen - 1)
        : lineLen;

    int clampedX = mathClamp(cursorX, 0, maxLineX);
    this.cursorFlat = offset + clampedX;
  }

  @Override
  public int cursorY() {
    int rows = rows();
    if (rows <= 1) return 0;
    
    int offset = 0;
    for (int y = 0; y < rows - 1; y++) {
      int lineSpan =
        lineValue(y).length()
        + (isLineContinuation(y + 1) ? 0 : 1);
      if (
        this.cursorFlat < offset + lineSpan
      ) return y;
      offset += lineSpan;
    }

    return rows - 1;
  }

  @Override
  public void setCursorY(int cursorY) {
    if (rows() == 0) {
      this.cursorFlat = 0;
      return;
    }

    int curX = cursorX();
    int targetY = mathClamp(cursorY, 0, rows() - 1);
    int offset = lineStartOffset(targetY);
    int lineLen = lineValue(targetY).length();
    boolean isNextLineContinuation =
      targetY < rows() - 1
      && isLineContinuation(targetY + 1);
    int maxLineX = isNextLineContinuation ?
      Math.max(0, lineLen - 1) :
      lineLen;

    int clampedX = mathClamp(curX, 0, maxLineX);
    this.cursorFlat = offset + clampedX;
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
    value.insert(cursorFlat(), text);
    afterValueUpdate();
    setCursorFlat(cursorFlat() + text.length());
  }

  @Override
	public void replaceText(String text) {
    int cursorX = cursorX();
    if (cursorX == columns()) {
      insertText(text);
      return;
    }

    int charsToReplace = Math.min(
      columns() - cursorX,
      text.length());
    int cursorFlat = cursorFlat();
    
    value.replace(cursorFlat, cursorFlat + charsToReplace, text);
    afterValueUpdate();
    setCursorFlat(cursorFlat + text.length());
  }

  @Override
	public void backspace() {
    int cursorFlat = cursorFlat();
    if (cursorFlat == 0) return;
    value.deleteCharAt(cursorFlat - 1);
    afterValueUpdate();
    setCursorFlat(cursorFlat - 1);
  }

  @Override
	public void moveCursorForward(int i) {
    int newCursorFlat = cursorFlat() + i;
    setCursorFlat(mathClamp(newCursorFlat, 0, value.length()));
  }

  @Override
  public void moveCursorDownward(int i) {
    int newCursorY = mathClamp(cursorY() + i, 0, rows() - 1);
    verticalMove(() -> setCursorY(newCursorY));
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
    setCursorFlat(0);
  }

  @Override
  public void moveBottom() {
    setCursorFlat(value.length());
  }

  @Override
  public void movePageUp(float fragmentHeight) {
    int newCursorY = cursorY() - (int) (fragmentHeight / metrics().height());
    verticalMove(() -> setCursorY(newCursorY));
  }

  @Override
  public void movePageDown(float fragmentHeight) {
    int newCursorY = cursorY() + (int) (fragmentHeight / metrics().height());
    verticalMove(() -> setCursorY(newCursorY));
  }

  @Override
	public void delete() {
    int cursorFlat = cursorFlat();
    if (cursorFlat == value.length()) return;
    value.deleteCharAt(cursorFlat);
    afterValueUpdate();
  }

  @Override
	public void toggleInsertMode() {
    setIsReplaceMode(!isReplaceMode());
  }

  @Override 
  public void updateMetrics(FontMetrics fontMetrics) {
    this.fontMetrics = fontMetrics;
  }

  protected int rows() {
    return displayLines().size();
  }

  protected int columns() {
    return lineValue(cursorY()).length();
  }

  protected FontMetrics metrics() {
    return this.fontMetrics;
  }

  protected void setValue(String value) {
    this.value.setLength(0);
    this.value.append(value);
  }

  protected abstract void afterValueUpdate();

  private void verticalMove(Runnable action) {
    if (Float.isNaN(startNavWidth)) {
      this.startNavWidth = fontMetrics.stringWidth(
        lineValue(cursorY()).substring(0, cursorX()));
    }
    action.run();
    float startNavWidth = this.startNavWidth;
    setCursorX(MouseEventUtil.determineTextMouseIndex(
      startNavWidth, fontMetrics, lineValue(cursorY())));
    this.startNavWidth = startNavWidth;
  }

  private int lineStartOffset(int lineNum) {
    int offset = 0;
    for (int i = 0; i < lineNum; i++) {
      offset += lineValue(i).length();
      if (!isLineContinuation(i + 1)) {
        offset++;
      }
    }
    
    return offset;
  }
  
}
