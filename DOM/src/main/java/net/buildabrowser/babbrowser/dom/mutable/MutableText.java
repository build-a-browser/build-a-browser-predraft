package net.buildabrowser.babbrowser.dom.mutable;

import net.buildabrowser.babbrowser.dom.Text;
import net.buildabrowser.babbrowser.dom.mutable.imp.MutableTextImp;

public interface MutableText extends MutableNode, Text {

  static MutableText create(String text) {
    return new MutableTextImp(text);
  }

  void appendCharacter(int ch);
  
}
