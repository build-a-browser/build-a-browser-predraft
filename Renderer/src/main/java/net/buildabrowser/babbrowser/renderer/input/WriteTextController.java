package net.buildabrowser.babbrowser.renderer.input;

public interface WriteTextController {

  void setCursorFlat(int cursorFlat);

  void insertText(String text);

  void replaceText(String text);

  void moveCursorForward(int i);

  void moveCursorDownward(int i);

  void submit();
  
}
