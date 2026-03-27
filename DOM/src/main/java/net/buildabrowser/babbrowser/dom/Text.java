package net.buildabrowser.babbrowser.dom;

import net.buildabrowser.babbrowser.dom.imp.TextImp;

public interface Text extends Node {

  String data();

  // Extensions

  void appendCharacter(int ch);

  static Text create(String text) {
    return new TextImp(text);
  }

}
