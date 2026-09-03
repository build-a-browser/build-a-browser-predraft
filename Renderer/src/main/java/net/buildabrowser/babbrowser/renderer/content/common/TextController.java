package net.buildabrowser.babbrowser.renderer.content.common;

import java.util.List;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;

public interface TextController {

  String value();

  String lineValue(int lineNum);

  List<String> displayLines();

  boolean isMultiLine();

  boolean isLineContinuation(int lineNum);

  int cursorFlat();

  void setCursorFlat(int cursorFlat);

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

  void moveCursorDownward(int i);

  void moveHome();

  void moveEnd();

  void moveTop();

  void moveBottom();

  void movePageUp(float fragmentHeight);

  void movePageDown(float fragmentHeight);

  void delete();

  void toggleInsertMode();

  void scrollToCursor(
    float contentWidth,
    float contentHeight
  );

  void updateMetrics(FontMetrics fontMetrics);
  
}
