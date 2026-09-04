package net.buildabrowser.babbrowser.renderer.input;

import java.util.List;

import net.buildabrowser.babbrowser.painter.core.FontMetrics;

public interface TextController extends WriteTextController {

  String value();

  String lineValue(int lineNum);

  List<String> displayLines();

  boolean isMultiLine();

  boolean isLineContinuation(int lineNum);

  int cursorFlat();

  int cursorX();

  void setCursorX(int cursorX);

  int cursorY();

  void setCursorY(int cursorY);

  float scrollX();

  void setScrollX(float scrollX);

  boolean isReplaceMode();

  void setIsReplaceMode(boolean isReplaceMode);

  void insertOrReplaceText(String text);

  void backspace();

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
