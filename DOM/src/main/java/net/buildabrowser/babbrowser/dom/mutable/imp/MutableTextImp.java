package net.buildabrowser.babbrowser.dom.mutable.imp;

import net.buildabrowser.babbrowser.dom.mutable.MutableNode;
import net.buildabrowser.babbrowser.dom.mutable.MutableText;

public class MutableTextImp extends MutableNodeImp implements MutableText {

  private final StringBuilder data;

  public MutableTextImp(String text, MutableNode parentNode) {
    super(parentNode);
    this.data = new StringBuilder(text);
  }

  @Override
  public String data() {
    return this.data.toString();
  }

  @Override
  public String toString() {
    return this.data.toString();
  }

  @Override
  public void appendCharacter(int ch) {
    data.appendCodePoint(ch);
  }
  
}
