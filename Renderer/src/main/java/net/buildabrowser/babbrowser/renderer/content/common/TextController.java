package net.buildabrowser.babbrowser.renderer.content.common;

public interface TextController {

  String value();

  void setValue(String value);

  String displayValue();

  int cursorX();

  void setCursorX(int cursorX);

  int cursorY();

  void setCursorY(int cursorY);

  float scrollX();

  void setScrollX(float scrollX);

  float scrollY();

  void setScrollY(float scrollY);

  boolean isReplaceMode();

  void setIsReplaceMode(boolean isReplaceMode);

  void submit();
  
}
