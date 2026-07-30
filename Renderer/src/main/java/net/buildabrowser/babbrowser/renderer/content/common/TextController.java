package net.buildabrowser.babbrowser.renderer.content.common;

import java.util.List;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;

public interface TextController {

  default String lineValue() {
    return lineValue(cursorY());
  }

  default void setLineValue(String value) {
    setLineValue(cursorY(), value);
  }

  String lineValue(int lineNum);

  void setLineValue(int lineNum, String value);

  List<String> displayLines();

  boolean isMultiLine();

  boolean isLineContinuation(int lineNum);

  void setLineContinuation(int lineNum, boolean isContinuation);

  int cursorX();

  void setCursorX(int cursorX);

  int cursorY();

  void setCursorY(int cursorY);

  float scrollX();

  void setScrollX(float scrollX);

  boolean isReplaceMode();

  void setIsReplaceMode(boolean isReplaceMode);

  void submit();

  void insertOrReplaceText(String text);

  void insertText(String text);

  void replaceText(String text);

  void backspace();

  void moveCursorForward(int i);

  void moveCursorDownward(FontMetrics metrics, int i);

  void moveHome();

  void moveEnd();

  void moveTop();

  void moveBottom();

  void movePageUp(
    FontMetrics metrics,
    float fragmentHeight
  );

  void movePageDown(
    FontMetrics metrics,
    float fragmentHeight
  );

  void delete();

  void toggleInsertMode();

  void scrollToCursor(
    FontMetrics fontMetrics,
    float contentWidth,
    float contentHeight
  );
  
}
